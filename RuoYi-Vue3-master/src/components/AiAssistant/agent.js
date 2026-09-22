import { reactive } from 'vue'
import { ElMessageBox } from 'element-plus'
import { agentStep } from '@/api/ai/assistant'
import { collectPageContext, activeFormState } from './pageContext'
import { findByText, norm, visible } from './guide'
import { inputScopes, topDialog, selectedText, enabled } from './controls'
import { observeRequests } from '@/utils/requestObserver'

/**
 * 自动办事 Agent：
 * 循环「采集页面快照 → 模型决策下一步动作 → 执行动作」直到目标完成或中止。
 * 支持动作：navigate(跳页面)/click/input(填输入框)/select(下拉选择)/check(勾选)/wait/done/fail。
 * 删除、作废、结算等不可逆操作执行前会弹确认框，由用户把关。
 */
export const agent = reactive({ running: false, stop: false })

const MAX_STEPS = 300
const ACTION_DETAIL = 12
const DIGEST_MAX = 80
const STEP_RETRY = 2
const DANGER_RE = /删除|作废|退费|清空|移除|强退|出院|结算|发药|停止|取消/
// 提交类按钮：成功点击次数过多时暂停，防止模型忘记 done 导致重复建单
const SUBMIT_RE = /新建|创建|新增|提交|保存|确认|确定|审核|结算|挂号|收费|发药|入库|出库/
const SUBMIT_LIMIT = 3

function waitFor(fn, timeout = 6000, interval = 150) {
  return new Promise(resolve => {
    const start = Date.now()
    const t = setInterval(() => {
      const result = fn()
      if (result || agent.stop || Date.now() - start > timeout) {
        clearInterval(t)
        resolve(result)
      }
    }, interval)
  })
}

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/** 动作执行前短暂描边高亮 */
function flash(el) {
  el.style.outline = '3px solid #409eff'
  el.style.outlineOffset = '2px'
  el.style.borderRadius = '4px'
  setTimeout(() => {
    el.style.outline = ''
    el.style.outlineOffset = ''
    el.style.borderRadius = ''
  }, 1400)
}

/** Vue 受控输入框赋值：必须用原生 setter 再派发事件 */
function setNativeValue(el, value) {
  const proto = el.tagName === 'TEXTAREA' ? HTMLTextAreaElement.prototype : HTMLInputElement.prototype
  const setter = Object.getOwnPropertyDescriptor(proto, 'value').set
  setter.call(el, value)
  el.dispatchEvent(new Event('input', { bubbles: true }))
  el.dispatchEvent(new Event('change', { bubbles: true }))
}

/** 按 label 或控件文字定位 el-form-item */
function findFormItem(label) {
  const needle = norm(label).replace(/[:：]$/, '')
  if (!needle) return null
  // 弹窗/抽屉中的字段优先，避免同名查询栏字段抢占自动填写目标。
  for (const scope of inputScopes()) {
    const items = Array.from(scope.querySelectorAll('.el-form-item')).filter(visible)
    const labelOf = item => norm(item.querySelector('.el-form-item__label')?.innerText).replace(/[:：]$/, '')
    const exact = items.find(item => labelOf(item) === needle)
    if (exact) return exact
    const matches = items.filter(item => labelOf(item) && (labelOf(item).includes(needle) || needle.includes(labelOf(item))))
    if (matches.length === 1) return matches[0]
  }
  const scope = inputScopes()[0]
  const el = findByText(label, scope)
  return el ? el.closest('.el-form-item') || el.closest('label') || el.parentElement : null
}

/** 解析行号引用："数量@2"、"第2行 数量" → { label:'数量', row:2 } */
function parseRowRef(raw) {
  const s = String(raw || '').trim()
  let m = s.match(/^第\s*(\d+)\s*行[\s:：\-—]*(.*)$/)
  if (m) return { label: m[2], row: parseInt(m[1], 10) }
  m = s.match(/^(.*?)[@#＃]\s*(\d+)\s*(?:行)?$/)
  if (m) return { label: m[1], row: parseInt(m[2], 10) }
  return { label: s, row: 0 }
}

/** 单元格是否"未填写"：输入框为空、下拉未选、纯按钮/开关列视为无数据 */
function cellEmpty(td) {
  if (!td) return true
  const select = td.querySelector('.el-select')
  if (select) return !selectedText(select)
  const inp = td.querySelector('input, textarea')
  if (inp) return !String(inp.value || '').trim()
  if (td.querySelector('.el-button, button, .el-checkbox, .el-switch')) return true
  return !norm(td.innerText)
}

// 记住上一次操作的表格行：同一明细行的连续填写应落在同一行，而不是猜"哪行有内容"
let lastRowRef = { table: null, row: -1 }

/**
 * 行内编辑表格（采购明细这类）没有 form-item label：按列名定位单元格。
 * 支持"列名@行号/第N行 列名"指定行；不指定时：
 * - preferEmpty(select 场景)：取该列第一个未填写的行，填完一行自动推进到下一行
 * - 否则(input 场景)：沿用上一次操作的行（正在编辑的行），没有再取该列首个空行、首行
 */
function findTableCell(label, preferEmpty) {
  const ref = parseRowRef(label)
  const needle = norm(ref.label)
  if (!needle) return null
  for (const scope of inputScopes()) {
    for (const table of scope.querySelectorAll('.el-table')) {
      if (!visible(table)) continue
      const head = table.querySelector('.el-table__header-wrapper') || table
      const ths = Array.from(head.querySelectorAll('thead th'))
      const idx = ths.findIndex(th => {
        const h = norm(th.innerText)
        return h && (h === needle || h.includes(needle) || needle.includes(h))
      })
      if (idx < 0) continue
      const trs = Array.from(table.querySelectorAll('.el-table__body-wrapper tbody tr')).filter(visible)
      if (!trs.length) continue
      const cellOf = tr => tr.querySelectorAll('td')[idx]
      let row
      if (ref.row) {
        if (ref.row > trs.length) return null
        row = ref.row - 1
      } else {
        const remembered = lastRowRef.table === table && lastRowRef.row >= 0 && lastRowRef.row < trs.length
          ? lastRowRef.row : -1
        const emptyIdx = trs.findIndex(tr => cellEmpty(cellOf(tr)))
        row = preferEmpty
          ? (emptyIdx >= 0 ? emptyIdx : remembered >= 0 ? remembered : trs.length - 1)
          : (remembered >= 0 ? remembered : emptyIdx >= 0 ? emptyIdx : 0)
      }
      lastRowRef = { table, row }
      const td = cellOf(trs[row])
      if (td) return td
    }
  }
  return null
}

/** 按输入框占位提示匹配（如 placeholder="请输入数量"） */
function findInputByPlaceholder(label) {
  const needle = norm(label).replace(/^请输入/, '')
  if (!needle) return null
  for (const scope of inputScopes()) {
    for (const el of scope.querySelectorAll('input:not([type=checkbox]):not([type=radio]):not([type=password]), textarea')) {
      if (!visible(el)) continue
      const ph = norm(el.placeholder).replace(/^请输入/, '')
      if (ph && (ph.includes(needle) || needle.includes(ph))) return el
    }
  }
  return null
}

/** 定位 el-select：先按 form-item label，再按表格列名，最后按占位提示/已选文字 */
function findSelect(target) {
  const needle = norm(target)
  const item = findFormItem(target)
  const inItem = item && item.querySelector('.el-select')
  if (inItem) return inItem
  const td = findTableCell(target, true)
  const inCell = td && td.querySelector('.el-select')
  if (inCell) return inCell
  for (const sel of inputScopes()[0].querySelectorAll('.el-select')) {
    if (!visible(sel)) continue
    const ph = norm((sel.querySelector('input') && sel.querySelector('input').placeholder)
      || (sel.querySelector('.el-select__placeholder') && sel.querySelector('.el-select__placeholder').innerText) || '')
    const cur = norm(selectedText(sel))
    if (ph && (ph.includes(needle) || needle.includes(ph))) return sel
    if (cur && (cur.includes(needle) || needle.includes(cur))) return sel
  }
  return null
}

// click 动作只允许落在真实可交互元素上：弹窗标题、纯文本 td/span 不算"点到了"
const CLICKABLE_SEL = 'button,.el-button,a,.el-menu-item,.el-sub-menu__title,.el-radio-button,.el-tabs__item,[role=tab],[role=button],.el-select-dropdown__item,.el-dropdown-menu__item,.el-cascader-node,.el-checkbox,.el-switch,label,[onclick]'
// 浮层选项 teleport 到 body，不在 .el-overlay 内，单独放行
const POPPER_SEL = '.el-select-dropdown,.el-dropdown-menu,.el-cascader__dropdown,.el-popper,.el-picker__popper'

function toClickable(el) {
  if (!el) return null
  const c = el.matches(CLICKABLE_SEL) ? el : el.closest(CLICKABLE_SEL)
  return c && visible(c) && enabled(c) && !c.closest('.ai-panel, .ai-bubble') ? c : null
}

// 模型给的 target 常带修饰："点击住院登记按钮""页面顶部新建按钮"→ 剥掉动词/位置/控件后缀再匹配
const LEAD_VERB_RE = /^(点击|单击|按下|点一下|点开|打开|进入|跳转|前往|选择|勾选|选中|填写|输入|切换至|切换到)\s*/
const LOC_RE = /^(页面顶部|页面底部|右上角|左上角|右下角|左下角|顶部|底部|右侧|左侧|页面|表格中|表格里|列表中|列表里|弹窗中|弹窗里|窗口中|窗口里)的?/
const TAIL_NOUN_RE = /(按钮|按键|链接|菜单项|菜单|选项|页签|标签页|标签|输入框|文本框|复选框|下拉框|图标|框|项)$/

function targetVariants(target) {
  const raw = String(target || '').trim()
  if (!raw) return []
  const out = [raw]
  let s = raw.replace(LOC_RE, '').replace(LEAD_VERB_RE, '').replace(TAIL_NOUN_RE, '').trim()
  if (s && s !== raw) out.push(s)
  return out
}

/** 弹窗打开时只在最上层弹窗（及浮层选项）里找目标，避免点到遮罩后面的页面按钮 */
function findClickTarget(target) {
  const variants = targetVariants(target)
  const overlays = Array.from(document.querySelectorAll('.el-overlay')).filter(visible)
  if (overlays.length) {
    const top = overlays[overlays.length - 1]
    for (const v of variants) {
      const inDlg = toClickable(findByText(v, top))
      if (inDlg) return inDlg
    }
    for (const pop of document.querySelectorAll(POPPER_SEL)) {
      if (!visible(pop)) continue
      for (const v of variants) {
        const c = toClickable(findByText(v, pop))
        if (c) return c
      }
    }
    return null
  }
  // 先页面主体（.app-main），命中不到再全文档——避免侧栏同名菜单项抢走点击
  for (const v of variants) {
    const el = toClickable(findByText(v, '.app-main'))
    if (el) return el
  }
  for (const v of variants) {
    const el = toClickable(findByText(v, null))
    if (el) return el
  }
  return null
}

async function confirmDanger(target) {
  try {
    await ElMessageBox.confirm(
      `AI 助手请求执行「${target}」，该操作可能不可撤销，是否允许？`,
      '敏感操作确认',
      { confirmButtonText: '允许执行', cancelButtonText: '取消', type: 'warning' }
    )
    return true
  } catch (e) {
    return false
  }
}

async function clickTarget(target) {
  if (!target) return false
  if (DANGER_RE.test(target)) {
    const allow = await confirmDanger(target)
    if (!allow) return 'denied'
  }
  const el = findClickTarget(target)
  if (!el || agent.stop) return false
  el.scrollIntoView({ block: 'center', behavior: 'smooth' })
  await sleep(320)
  flash(el)
  await sleep(380)
  if (agent.stop || !enabled(el)) return false
  el.click()
  await sleep(450)
  return true
}

async function fillInput(target, value) {
  const item = findFormItem(target)
  let el = item && item.querySelector('input:not([type=checkbox]):not([type=radio]):not([type=password]), textarea')
  // 表单项找不到时：按表格列名定位行内编辑单元格，再退化为占位提示匹配
  if (!el || !visible(el)) {
    const td = findTableCell(target, false)
    el = td && td.querySelector('input:not([type=checkbox]):not([type=radio]):not([type=password]), textarea')
  }
  if (!el || !visible(el)) el = findInputByPlaceholder(target)
  // 下拉框内部的输入框不能当普通输入框写（会写出"无匹配数据"的脏值），该用 select
  if (el && el.closest && el.closest('.el-select')) return false
  if (!el || !visible(el) || !enabled(el) || el.readOnly) return false
  // 值已是目标值：无需重复填写，明确反馈让模型继续下一步
  if (String(el.value || '').trim() === String(value == null ? '' : value).trim()) return 'already'
  el.focus()
  setNativeValue(el, value == null ? '' : String(value))
  el.blur()
  await sleep(250)
  return true
}

async function selectOption(target, value) {
  const needle = norm(value)
  const short = String(value || '').trim().split(/[（(]/)[0].trim()
  const sel = findSelect(target)
  if (!needle || !sel || !enabled(sel)) return false
  const input = sel.querySelector('input[role=combobox], input')
  if (!input || !enabled(input)) return false
  const matches = text => norm(text) === needle || norm(text).includes(needle)
  if (matches(selectedText(sel))) {
    input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', code: 'Escape', bubbles: true }))
    return 'already'
  }
  // Element Plus teleports each dropdown to body. Restrict options to this control.
  const tryPick = () => {
    const popup = document.getElementById(input.getAttribute('aria-controls'))
    const options = Array.from(popup?.querySelectorAll('.el-select-dropdown__item') || []).filter(opt => visible(opt) && enabled(opt))
    const exact = options.filter(opt => norm(opt.innerText) === needle)
    if (exact.length === 1) return exact[0]
    const candidates = options.filter(opt => matches(opt.innerText))
    return candidates.length === 1 ? candidates[0] : null
  }
  if (input.getAttribute('aria-expanded') !== 'true') {
    const trigger = sel.querySelector('.el-select__wrapper') || sel
    trigger.click()
  }
  let hit = await waitFor(tryPick, 500, 100)
  if (!hit && !input.readOnly && !agent.stop) {
    input.focus()
    setNativeValue(input, short)
  }
  if (!hit) hit = await waitFor(tryPick, 6000, 100)
  if (hit) {
    flash(hit)
    await sleep(200)
    if (agent.stop || !enabled(hit)) return false
    hit.click()
    return !!await waitFor(() => matches(selectedText(sel)), 1500, 100)
  }
  input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', code: 'Escape', bubbles: true }))
  return false
}

async function setCheck(target, value) {
  const needle = norm(target)
  for (const cb of inputScopes()[0].querySelectorAll('.el-checkbox')) {
    if (!visible(cb) || !norm(cb.innerText).includes(needle)) continue
    const want = !(value === false || value === 'false')
    if (cb.classList.contains('is-checked') === want) return 'already'
    const lbl = cb.querySelector('.el-checkbox__label') || cb
    flash(lbl)
    await sleep(300)
    lbl.click()
    await sleep(250)
    return true
  }
  return false
}

async function execAction(a, router) {
  switch (a.type) {
    case 'navigate': {
      const path = a.path || a.target
      if (!path || !router) return false
      if (router.currentRoute.value.path === path) return 'already'
      router.push(path).catch(() => {})
      await waitFor(() => window.location.pathname === path && document.querySelector('.app-container'), 6000)
      await sleep(400)
      return true
    }
    case 'wait':
      await sleep(1500)
      return true
    case 'click': {
      // 以业务写入响应确认提交结果；弹窗关闭本身不能证明保存成功。
      const dialogBefore = topDialog()
      const writes = new Map()
      const unsubscribe = observeRequests(event => {
        const config = event.config
        if (!config || !['post', 'put', 'patch', 'delete'].includes(config.method)
          || !/^(?:\/dev-api)?\/(his|system|demo)\//.test(config.url || '')) return
        if (event.phase === 'start') writes.set(config, event)
        else if (writes.has(config)) writes.set(config, event)
      })
      try {
        const result = await clickTarget(a.target)
        if (result !== true) return result
        if (writes.size) {
          await waitFor(() => [...writes.values()].every(event => event.phase === 'end'), 15000, 100)
          const events = [...writes.values()]
          if (events.some(event => event.phase !== 'end' || (!event.ok && !event.businessResponse))) return 'submit-unknown'
          if (events.some(event => !event.ok)) return 'submitted-open'
          await waitFor(() => !dialogBefore || !visible(dialogBefore), 1500, 100)
          return 'submitted'
        }
        if (topDialog() && topDialog() !== dialogBefore) return 'opened'
        if (dialogBefore && SUBMIT_RE.test(String(a.target || ''))) return 'submitted-open'
        return result
      } finally {
        unsubscribe()
      }
    }
    case 'input':
      return fillInput(a.target, a.value)
    case 'select':
      return selectOption(a.target, a.value)
    case 'check':
      return setCheck(a.target, a.value)
    default:
      return false
  }
}

/** 单步决策请求：失败自动重试，避免一次网络抖动就中断长任务 */
async function stepRequest(payload) {
  let err
  for (let t = 0; t <= STEP_RETRY; t++) {
    try {
      return await agentStep(payload)
    } catch (e) {
      err = e
      if (agent.stop) return null
      if (t < STEP_RETRY) await sleep(1200 * (t + 1))
    }
  }
  throw err
}

/** 超出明细窗口的更早动作压成一行摘要，让模型在长任务里记得已做过什么 */
/** 动作结果是否算成功（用于熔断/计数判断）；submitted-open=点了但弹窗没关，算未成功 */
function okSuccess(ok) {
  return ok === 'true' || ok === 'already' || ok === 'opened' || ok === 'submitted'
}

function digestOf(actions) {
  if (actions.length <= ACTION_DETAIL) return undefined
  const older = actions.slice(0, actions.length - ACTION_DETAIL).slice(-DIGEST_MAX)
  const base = actions.length - ACTION_DETAIL - older.length
  return older.map((a, idx) => {
    const v = a.value ? `=${a.value.slice(0, 30)}` : ''
    const mark = a.ok === 'submitted-open' ? '(提交后弹窗未关)'
      : okSuccess(a.ok) ? (a.ok === 'submitted' ? '(已提交)' : a.ok === 'already' ? '(已是目标状态)' : '')
      : a.ok === 'denied' ? '(用户拒绝)' : '(失败)'
    return `${base + idx + 1}.${a.type}「${a.target}」${v}${mark}`
  })
}

/** 检测尾部动作是否陷入死循环：同一动作连做 5 次；周期 ≤4 重复 3 轮，或周期 5~12 重复 2 轮（覆盖"新建→填表→提交"这类长循环） */
function findLoop(actions) {
  const sig = x => `${x.type}|${x.target}|${x.value}`
  const n = actions.length
  if (n >= 5 && sig(actions[n - 1]) === sig(actions[n - 2])
    && sig(actions[n - 2]) === sig(actions[n - 3])
    && sig(actions[n - 3]) === sig(actions[n - 4])
    && sig(actions[n - 4]) === sig(actions[n - 5])) {
    return true
  }
  for (let p = 2; p <= 12; p++) {
    const need = p <= 4 ? 3 : 2
    if (n < p * need) continue
    const tail = actions.slice(n - p * need)
    let cyclic = true
    for (let j = p; j < tail.length; j++) {
      if (sig(tail[j]) !== sig(tail[j % p])) { cyclic = false; break }
    }
    if (cyclic) return true
  }
  return false
}

/**
 * 执行一个自动办事任务
 * @param goal 用户目标，如"帮我新建一个采购单"
 * @param ctx  { route, router, onSay, images } onSay 推送进展；images 为首轮携带的截图
 * @returns { finished, stopped, asked, error, record } record 为办事记录，供对话历史回溯
 */
export async function runAgent(goal, ctx) {
  if (agent.running) throw new Error('自动办事正在执行')
  const { route, router, onSay } = ctx
  const images = Array.isArray(ctx.images) ? ctx.images : []
  const say = typeof onSay === 'function' ? onSay : () => {}
  const actions = (ctx.previousRecord?.steps || []).map(step => ({ ...step }))
  let finished = false
  let stopped = false
  let asked = false
  let error = false
  let lastSay = ''
  let outcome = 'done'
  let failStreak = 0
  let waitStreak = 0
  let alreadyStreak = 0
  let noProgressStreak = 0
  const submitClicks = new Map()
  lastRowRef = { table: null, row: -1 }
  agent.running = true
  agent.stop = false
  try {
    for (let i = 0; i < MAX_STEPS && !finished; i++) {
      if (agent.stop) { stopped = true; break }
      let res
      try {
        res = await stepRequest({
          goal,
          page: `${route.path} ${route.meta.title || ''}`.trim(),
          pageContext: collectPageContext(route),
          // 最近动作带明细（含填写值），更早的压成摘要，长任务也不丢记忆
          actions: actions.slice(-ACTION_DETAIL).map(a => ({ type: a.type, target: a.target, value: a.value, ok: a.ok })),
          digest: digestOf(actions),
          stepNo: actions.length + 1,
          // 截图只在首轮携带，让模型看到"这些/图中"所指的内容
          images: i === 0 && images.length ? images : undefined
        })
      } catch (e) {
        error = true
        outcome = 'error'
        say(`第 ${i + 1} 步时 AI 服务异常，任务中断（已完成 ${actions.length} 步），可重新下达指令继续`)
        break
      }
      if (res === null || agent.stop) { stopped = true; break }
      const a = res.action || {}
      let message = res.say || ''
      if (a.type === 'ask') {
        const question = typeof a.value === 'string' ? a.value.trim() : ''
        if (question) message = question
        const form = activeFormState()
        // Short model replies sometimes omit the actual question. Use live required fields.
        if (!question && form.missing.length && !form.missing.some(label => message.includes(label))) {
          message = `当前表单尚未提交。请补充：${form.missing.join('、')}。`
          if (form.filled.length) message += `当前已填：${form.filled.join('；')}。`
          message += '收到信息后继续填写并提交。'
        } else if (!message.trim()) {
          message = '还需要补充办理信息，请说明操作对象和要填写的内容。'
        }
      }
      if (message) { lastSay = message; say(message) }
      // A concrete action must execute even when the model also sets done=true.
      if (!a.type || a.type === 'done' || a.type === 'fail' || a.type === 'ask') {
        finished = true
        asked = a.type === 'ask'
        outcome = asked ? 'asked' : a.type === 'fail' ? 'failed' : 'done'
        break
      }
      // 提交类按钮护栏：同一按钮已成功点击多次还再点，多半是模型忘了 done 在重复建单
      if (a.type === 'click' && SUBMIT_RE.test(String(a.target || ''))) {
        const c = submitClicks.get(a.target) || 0
        if (c >= SUBMIT_LIMIT) {
          say(`「${a.target}」已成功执行 ${c} 次，为避免重复创建/提交已暂停。若确实需要继续请回复「继续」`)
          finished = true
          outcome = 'paused'
          break
        }
      }
      const ok = await execAction(a, router)
      // 只有真正产生状态变化（提交成功/打开弹窗）才计入提交次数
      if ((ok === 'submitted' || ok === 'opened') && a.type === 'click' && SUBMIT_RE.test(String(a.target || ''))) {
        submitClicks.set(a.target, (submitClicks.get(a.target) || 0) + 1)
      }
      actions.push({
        type: a.type,
        target: a.target || a.path || '',
        value: a.value === undefined || a.value === null ? '' : String(a.value).slice(0, 60),
        ok: ok === true ? 'true' : String(ok)
      })
      if (ok === 'denied') {
        say(`已取消「${a.target || ''}」，任务结束`)
        finished = true
        outcome = 'denied'
        break
      }
      if (ok === 'submit-unknown') {
        lastSay = '提交结果暂时无法确认，已暂停。请先核对列表，避免重复创建。'
        say(lastSay)
        finished = true
        outcome = 'paused'
        break
      }
      failStreak = okSuccess(String(ok)) ? 0 : failStreak + 1
      waitStreak = a.type === 'wait' && ok === true ? waitStreak + 1 : 0
      alreadyStreak = ok === 'already' ? alreadyStreak + 1 : 0
      // 实质进展 = 真实改变了页面状态；already/失败/弹窗未关 都算原地踏步
      noProgressStreak = (ok === true || ok === 'submitted' || ok === 'opened') ? 0 : noProgressStreak + 1
      // 同一动作连续失败 3 次：熔断，防止原地打转
      const last3 = actions.slice(-3)
      if (last3.length === 3 && last3.every(x => !okSuccess(x.ok) && x.type === last3[0].type && x.target === last3[0].target)) {
        say(`「${last3[0].target || last3[0].type}」多次尝试未成功，已暂停。请手动协助，或回复「继续」让我再试`)
        finished = true
        outcome = 'paused'
        break
      }
      if (failStreak >= 6) {
        say('连续多个操作未成功，已暂停执行。请检查页面后回复「继续」或换个说法')
        finished = true
        outcome = 'paused'
        break
      }
      if (waitStreak >= 4) {
        say('等待页面响应超时，已暂停执行，可回复「继续」接着办理')
        finished = true
        outcome = 'paused'
        break
      }
      // 连续重复"已是目标状态"的动作：模型不知道该往下走了
      if (alreadyStreak >= 3) {
        say(`「${a.target || a.type}」已是目标状态但任务未推进，已暂停。可回复「继续」或换个说法`)
        finished = true
        outcome = 'paused'
        break
      }
      // already、失败、弹窗未关混着来：连续多步没有实质进展
      if (noProgressStreak >= 6) {
        say('连续多步操作没有实质进展，已暂停。请检查页面或换个说法后回复「继续」')
        finished = true
        outcome = 'paused'
        break
      }
      if (findLoop(actions)) {
        say('检测到操作在重复执行（可能已重复提交），已暂停。若确实需要继续请回复「继续」')
        finished = true
        outcome = 'paused'
        break
      }
      await sleep(300)
    }
    if (!finished && agent.stop) stopped = true
    if (!finished && !stopped && !error) outcome = 'paused'
  } finally {
    agent.running = false
    agent.stop = false
  }
  if (stopped) { outcome = 'stopped'; say('已停止执行') }
  else if (!finished && !error) say('已达到单任务步数上限，已暂停。回复「继续」我会接着办理')
  return {
    finished,
    stopped,
    asked,
    error,
    outcome,
    record: {
      goal,
      outcome,
      steps: actions,
      finalSay: lastSay,
      page: `${route.path} ${route.meta.title || ''}`.trim(),
      snapshot: collectPageContext(route)
    }
  }
}
