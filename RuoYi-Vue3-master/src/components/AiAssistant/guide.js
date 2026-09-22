import { reactive } from 'vue'
import useAppStore from '@/store/modules/app'

/**
 * AI 操作引导引擎：
 * - steps[].type === 'menu'  ：在左侧菜单中定位并高亮菜单项，随后自动跳转路由
 * - steps[].type === 'page'  ：在当前页面按文字查找控件并聚光高亮
 * 遮罩不拦截点击，用户可按提示亲自操作；「下一步/上一步」手动推进。
 */
export const guide = reactive({
  active: false,
  done: false,
  steps: [],
  index: 0,
  navigate: null,
  rect: null,
  notFound: false,
  resolving: false
})

const MENU_SCOPE = '.sidebar-container'
const PAGE_SELECTORS = [
  'button', '.el-button', '.el-menu-item', '.el-sub-menu__title', 'a',
  '.el-radio-button', '.el-radio', '.el-checkbox', '.el-tabs__item', '[role="tab"]',
  '.el-select-dropdown__item', '.el-dropdown-menu__item', '.el-cascader-node',
  'label', '.el-form-item__label', '.el-step__title', '.el-tag', 'td', 'th', 'span'
]
const INTERACTIVE = 'button,.el-button,.el-menu-item,.el-sub-menu__title,a,.el-radio-button,.el-tabs__item,[role="tab"],.el-select-dropdown__item,.el-dropdown-menu__item,label'

let router = null
let trackTimer = null
let autoTimer = null
let currentEl = null
let destroyed = false

export function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

export function until(fn, timeout = 6000, interval = 150) {
  return new Promise(resolve => {
    const start = Date.now()
    const timer = setInterval(() => {
      if (fn() || Date.now() - start > timeout || destroyed) {
        clearInterval(timer)
        resolve()
      }
    }, interval)
  })
}

export function norm(s) {
  return (s || '').replace(/\s+/g, '')
}

export function visible(el) {
  if (!el || !el.isConnected) return false
  const r = el.getBoundingClientRect()
  if (r.width < 2 || r.height < 2) return false
  const style = window.getComputedStyle(el)
  return style.display !== 'none' && style.visibility !== 'hidden'
}

/** 在指定根节点内按文字查找最匹配的可交互元素；root 支持选择器字符串或元素 */
export function findByText(text, rootSel) {
  const needle = norm(text)
  if (!needle) return null
  const roots = rootSel instanceof Element ? [rootSel]
    : rootSel ? Array.from(document.querySelectorAll(rootSel)) : [document.body]
  let best = null
  let bestScore = -1
  for (const root of roots) {
    if (!root) continue
    for (const el of root.querySelectorAll(PAGE_SELECTORS.join(','))) {
      if (!visible(el)) continue
      const t = norm(el.innerText || el.textContent || '')
      if (!t || !t.includes(needle)) continue
      let score = t === needle ? 1000 : (needle.length / t.length) * 100
      if (el.matches(INTERACTIVE)) score += 300
      const r = el.getBoundingClientRect()
      score += Math.max(0, 60 - (r.width * r.height) / 6000)
      if (score > bestScore) {
        bestScore = score
        best = el
      }
    }
  }
  // 命中纯文本节点时，优先高亮其可点击父元素
  if (best && !best.matches(INTERACTIVE)) {
    const clickable = best.closest(INTERACTIVE)
    if (clickable && visible(clickable) && norm(clickable.innerText).includes(needle)) {
      best = clickable
    }
  }
  return best
}

function pickRect(el) {
  const r = el.getBoundingClientRect()
  const pad = 6
  return {
    top: Math.max(0, r.top - pad),
    left: Math.max(0, r.left - pad),
    width: r.width + pad * 2,
    height: r.height + pad * 2
  }
}

/** 查找未展开的子菜单标题（用于逐级展开目标所在分组） */
function findClosedSubMenuTitle(text) {
  for (const sm of document.querySelectorAll(`${MENU_SCOPE} .el-sub-menu`)) {
    if (sm.classList.contains('is-opened')) continue
    const title = sm.querySelector(':scope > .el-sub-menu__title')
    if (title && norm(title.innerText).includes(text)) return title
  }
  return null
}

/** 展开侧边栏并逐级点开目标菜单所在的分组（如 智慧医疗>门诊管理） */
async function resolveMenuTarget(step) {
  const appStore = useAppStore()
  if (appStore.sidebar.hide) return null
  if (!appStore.sidebar.opened) {
    appStore.$patch(state => { state.sidebar.opened = true })
    await sleep(350)
  }
  // 菜单可能已展开，先直接找
  let el = findByText(step.target, MENU_SCOPE)
  if (el) return el
  // 按分组链路逐级展开（group 形如 "智慧医疗>门诊管理"）
  const groups = (step.group || '').split(/[>＞/]/).map(g => g.trim()).filter(Boolean)
  for (const g of groups) {
    const title = findClosedSubMenuTitle(g)
    if (title) {
      title.click()
      await sleep(330)
    }
    el = findByText(step.target, MENU_SCOPE)
    if (el) return el
  }
  // 兜底：展开所有未展开的子菜单
  for (let round = 0; round < 3 && !el; round++) {
    const titles = Array.from(document.querySelectorAll(`${MENU_SCOPE} .el-sub-menu`))
      .filter(sm => !sm.classList.contains('is-opened'))
      .map(sm => sm.querySelector(':scope > .el-sub-menu__title'))
      .filter(Boolean)
    if (!titles.length) break
    titles.forEach(t => t.click())
    await sleep(330)
    el = findByText(step.target, MENU_SCOPE)
  }
  return el
}

async function resolveStepTarget(step) {
  if (step.type === 'menu') {
    return resolveMenuTarget(step)
  }
  return findByText(step.target, null)
}

function clearAuto() {
  if (autoTimer) {
    clearTimeout(autoTimer)
    autoTimer = null
  }
}

async function showStep(i) {
  if (!guide.active || i < 0 || i >= guide.steps.length) return
  guide.index = i
  guide.rect = null
  guide.notFound = false
  guide.resolving = true
  const step = guide.steps[i]
  const el = await resolveStepTarget(step)
  guide.resolving = false
  if (!guide.active || guide.index !== i) return
  if (el) {
    currentEl = el
    el.scrollIntoView({ block: 'center', behavior: 'smooth' })
    await sleep(320)
    if (!guide.active || guide.index !== i) return
    guide.rect = pickRect(el)
    guide.notFound = false
    // 菜单定位步骤短暂停留后自动推进（触发路由跳转）
    if (step.type === 'menu') {
      clearAuto()
      autoTimer = setTimeout(() => goNext(), 1800)
    }
  } else {
    currentEl = null
    guide.rect = null
    guide.notFound = true
  }
}

async function navigateThen() {
  const path = guide.navigate && guide.navigate.path
  if (!path || !router) return
  if (router.currentRoute.value.path !== path) {
    router.push(path).catch(() => {})
    await until(() => window.location.pathname === path && document.querySelector('.app-container'), 6000)
  }
  await sleep(400)
}

export async function goNext() {
  if (!guide.active) return
  clearAuto()
  const cur = guide.steps[guide.index]
  if (cur && cur.type === 'menu') {
    await navigateThen()
    if (!guide.active) return
  }
  if (guide.index < guide.steps.length - 1) {
    showStep(guide.index + 1)
  } else {
    finishGuide()
  }
}

export function prevStep() {
  if (!guide.active) return
  clearAuto()
  if (guide.index > 0) showStep(guide.index - 1)
}

export function finishGuide() {
  clearAuto()
  guide.active = false
  guide.rect = null
  guide.done = true
  stopTrack()
}

export function closeDone() {
  guide.done = false
}

export function stopGuide() {
  clearAuto()
  destroyed = true
  guide.active = false
  guide.done = false
  guide.rect = null
  guide.notFound = false
  guide.steps = []
  guide.index = 0
  currentEl = null
  stopTrack()
}

function startTrack() {
  stopTrack()
  trackTimer = setInterval(() => {
    if (!guide.active || guide.resolving) return
    const step = guide.steps[guide.index]
    if (!step) return
    if (currentEl && visible(currentEl)) {
      guide.rect = pickRect(currentEl)
      guide.notFound = false
      return
    }
    // 目标丢失（页面重绘 / 弹窗刚打开）时按文字重新查找
    const el = step.type === 'menu' ? findByText(step.target, MENU_SCOPE) : findByText(step.target, null)
    if (el) {
      currentEl = el
      guide.rect = pickRect(el)
      guide.notFound = false
    } else {
      guide.rect = null
      guide.notFound = true
    }
  }, 300)
}

function stopTrack() {
  if (trackTimer) {
    clearInterval(trackTimer)
    trackTimer = null
  }
}

/**
 * 开始一次操作引导
 * @param {{navigate?:{path,menuText,group}, steps?:Array}} payload
 * @param routerInstance vue-router 实例
 */
export function startGuide(payload, routerInstance) {
  if (!payload) return
  router = routerInstance
  destroyed = false
  stopTrack()
  clearAuto()

  const steps = []
  const nav = payload.navigate
  if (nav && nav.path) {
    steps.push({
      type: 'menu',
      target: nav.menuText || '',
      title: `打开菜单「${nav.menuText || '目标页面'}」`,
      content: '已为您在左侧菜单定位，即将自动进入页面'
    })
  }
  for (const s of payload.steps || []) {
    if (s && s.target) {
      steps.push({ type: 'page', target: s.target, title: s.title || s.target, content: s.content || '' })
    }
  }
  if (!steps.length) return

  guide.navigate = nav || null
  guide.steps = steps
  guide.index = 0
  guide.done = false
  guide.rect = null
  guide.notFound = false
  guide.active = true
  startTrack()
  showStep(0)
}

/**
 * 直接执行页面操作：按步骤定位控件并真实点击（用于"点击中成药"这类指令）
 * @param {{navigate?:{path}, steps?:Array}} payload
 * @param routerInstance vue-router 实例
 * @returns {Promise<{navigated:boolean, results:Array<{target:string, ok:boolean}>}>}
 */
export async function performActions(payload, routerInstance) {
  const out = { navigated: false, results: [] }
  if (!payload) return out
  router = routerInstance
  if (guide.active) stopGuide()
  destroyed = false

  const nav = payload.navigate
  if (nav && nav.path && router && router.currentRoute.value.path !== nav.path) {
    router.push(nav.path).catch(() => {})
    await until(() => window.location.pathname === nav.path && document.querySelector('.app-container'), 6000)
    await sleep(400)
    out.navigated = true
  }

  for (const s of payload.steps || []) {
    if (!s || !s.target) continue
    const el = findByText(s.target, null)
    if (!el) {
      out.results.push({ target: s.target, ok: false })
      continue
    }
    el.scrollIntoView({ block: 'center', behavior: 'smooth' })
    await sleep(320)
    flashEl(el)
    await sleep(380)
    el.click()
    await sleep(280)
    out.results.push({ target: s.target, ok: true })
  }
  return out
}

/** 点击前短暂描边高亮，给用户视觉反馈 */
function flashEl(el) {
  el.style.outline = '3px solid #409eff'
  el.style.outlineOffset = '2px'
  el.style.borderRadius = '4px'
  el.style.transition = 'outline-color .3s'
  setTimeout(() => {
    el.style.outline = ''
    el.style.outlineOffset = ''
    el.style.borderRadius = ''
    el.style.transition = ''
  }, 1400)
}
