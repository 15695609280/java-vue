<template>
  <teleport to="body">
    <!-- 聊天面板：头部可整体拖动 -->
    <transition name="el-zoom-in-bottom">
      <div v-show="panelOpen" ref="panelRef" class="ai-panel" :style="panelStyle">
        <div class="ai-panel-head" @pointerdown="onPanelPointerDown">
          <div class="ai-panel-title">
            <el-icon :size="18"><MagicStick /></el-icon>
            <span>AI 智慧助手</span>
          </div>
          <div class="ai-panel-actions">
            <el-tooltip content="清空对话" placement="bottom">
              <el-icon class="head-btn" @click="clearChat"><Delete /></el-icon>
            </el-tooltip>
            <el-icon class="head-btn" @click="panelOpen = false"><Close /></el-icon>
          </div>
        </div>

        <div ref="msgBoxRef" class="ai-panel-body">
          <div v-if="!messages.length" class="ai-welcome">
            <div class="welcome-text">您好，我是智慧医疗助手。可以问我系统操作或医疗业务问题，例如：</div>
            <div class="ai-suggests">
              <div v-for="s in suggestions" :key="s" class="suggest-chip" @click="send(s)">{{ s }}</div>
            </div>
          </div>

          <template v-for="(m, i) in messages" :key="i">
            <div :class="['ai-msg', m.role]">
              <div class="ai-msg-bubble" :class="{ 'is-error': m.error }">
                <div v-if="m.images && m.images.length" class="ai-msg-imgs">
                  <el-image
                    v-for="(img, idx) in m.images"
                    :key="idx"
                    class="msg-img"
                    :src="img"
                    :preview-src-list="m.images"
                    :initial-index="idx"
                    fit="cover"
                    preview-teleported
                  />
                </div>
                <span v-if="m.content">{{ m.content }}</span>
              </div>
              <div v-if="m.role === 'assistant' && hasGuide(m)" class="ai-msg-guide">
                <el-button size="small" type="primary" plain icon="Position" @click="replayGuide(m)">开始引导</el-button>
                <span v-if="m.autoGuidePending" class="guide-hint">即将自动开始引导…</span>
              </div>
            </div>
          </template>

          <div v-if="loading" class="ai-msg assistant">
            <div class="ai-msg-bubble ai-thinking">
              <span class="dot" /><span class="dot" /><span class="dot" />
            </div>
          </div>
        </div>

        <div class="ai-panel-foot">
          <div class="ai-quick">
            <span class="ai-page-tag" :title="route.path">
              <el-icon :size="13"><Monitor /></el-icon>
              <span class="tag-text">{{ route.meta.title || '当前页面' }}</span>
            </span>
            <span class="ai-quick-btn" :class="{ disabled: loading }" @click="analyzePage">
              <el-icon :size="13"><DataAnalysis /></el-icon>分析本页
            </span>
            <span class="ai-quick-btn" :class="{ disabled: loading }" @click="guideMe">
              <el-icon :size="13"><Compass /></el-icon>教我操作
            </span>
            <span class="ai-quick-btn" :class="{ active: agentMode, disabled: loading }" @click="toggleAgent">
              <el-icon :size="13"><Cpu /></el-icon>{{ agent.running ? '停止' : '自动办事' }}
            </span>
          </div>
          <div v-if="pendingImages.length" class="ai-pending-imgs">
            <div v-for="(img, i) in pendingImages" :key="i" class="pending-img">
              <img :src="img.dataUrl" alt="" />
              <el-icon class="pending-del" @click="pendingImages.splice(i, 1)"><Close /></el-icon>
            </div>
          </div>
          <div class="ai-input-row">
            <input ref="fileInput" type="file" accept="image/*" multiple hidden @change="onFiles" />
            <el-icon
              class="attach-btn"
              :class="{ disabled: loading || agent.running }"
              title="发送图片（可直接 Ctrl+V 粘贴截图）"
              @click="pickImages"
            ><Picture /></el-icon>
            <el-input
              v-model="input"
              :placeholder="pendingAgentGoal ? '补充所需信息，继续办理' : agentMode ? '输入要办的事，如：帮我新建一个采购单' : '输入问题，可粘贴截图，回车发送'"
              :disabled="loading || agent.running"
              @keyup.enter="send()"
              @paste="onPaste"
            />
            <el-button type="primary" :loading="loading" :disabled="(!input.trim() && !pendingImages.length) || agent.running" @click="send()">发送</el-button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 可拖动吸附气泡 -->
    <div
      ref="bubbleRef"
      class="ai-bubble"
      :class="{ dragging: dragging }"
      :style="bubbleStyle"
      @pointerdown="onBubblePointerDown"
    >
      <el-badge v-if="guide.active" is-dot class="bubble-dot">
        <el-icon :size="20" color="#fff"><MagicStick /></el-icon>
      </el-badge>
      <el-icon v-else :size="20" color="#fff"><MagicStick /></el-icon>
      <span class="bubble-text">AI 助手</span>
    </div>
  </teleport>

  <GuideOverlay />
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { chatWithAssistant } from '@/api/ai/assistant'
import GuideOverlay from './GuideOverlay.vue'
import { guide, startGuide, performActions } from './guide'
import { collectPageContext } from './pageContext'
import { agent, runAgent } from './agent'
import { conversationPayload } from './conversation'

const router = useRouter()
const route = useRoute()

/* ---------- 气泡拖动与边缘吸附 ---------- */
const savedPos = (() => {
  try { return JSON.parse(localStorage.getItem('ai-assistant-pos')) } catch (e) { return null }
})()
const pos = ref(savedPos || { x: window.innerWidth - 120, y: Math.round(window.innerHeight * 0.55) })
const dragging = ref(false)
const moved = ref(0)
const bubbleRef = ref(null)
let dragOffset = { x: 0, y: 0 }

function bubbleSize() {
  const el = bubbleRef.value
  return el ? { w: el.offsetWidth, h: el.offsetHeight } : { w: 110, h: 48 }
}

function clampPos(p) {
  const { w, h } = bubbleSize()
  return {
    x: Math.min(Math.max(p.x, 4), window.innerWidth - w - 4),
    y: Math.min(Math.max(p.y, 4), window.innerHeight - h - 4)
  }
}
pos.value = clampPos(pos.value)

const bubbleStyle = computed(() => ({
  left: pos.value.x + 'px',
  top: pos.value.y + 'px',
  transition: dragging.value ? 'none' : 'left .25s ease, top .25s ease'
}))

function onBubblePointerDown(e) {
  dragging.value = true
  moved.value = 0
  dragOffset = { x: e.clientX - pos.value.x, y: e.clientY - pos.value.y }
  e.currentTarget.setPointerCapture(e.pointerId)
  window.addEventListener('pointermove', onBubbleMove)
  window.addEventListener('pointerup', onBubbleUp, { once: true })
}

function onBubbleMove(e) {
  if (!dragging.value) return
  const nx = e.clientX - dragOffset.x
  const ny = e.clientY - dragOffset.y
  moved.value += Math.abs(nx - pos.value.x) + Math.abs(ny - pos.value.y)
  pos.value = clampPos({ x: nx, y: ny })
}

function onBubbleUp() {
  dragging.value = false
  window.removeEventListener('pointermove', onBubbleMove)
  if (moved.value < 6) {
    panelOpen.value = !panelOpen.value
    return
  }
  // 松手后吸附到最近一侧
  const { w } = bubbleSize()
  pos.value.x = pos.value.x + w / 2 < window.innerWidth / 2 ? 10 : window.innerWidth - w - 10
  pos.value = clampPos(pos.value)
  localStorage.setItem('ai-assistant-pos', JSON.stringify(pos.value))
}

/* ---------- 聊天面板（可拖动） ---------- */
const panelOpen = ref(false)
const panelRef = ref(null)
const panelPos = ref(null) // 拖动后的自定义位置，null 时跟随气泡
let panelDragOffset = { x: 0, y: 0 }

// Allow replies while a business dialog is open, without releasing its other focus guards.
function allowAssistantFocus(event) {
  if (panelOpen.value && panelRef.value?.contains(event.relatedTarget)
    && event.target instanceof Element && event.target.closest('.el-dialog, .el-drawer')) {
    event.stopPropagation()
  }
}

onMounted(() => document.addEventListener('focusout', allowAssistantFocus, true))
onBeforeUnmount(() => document.removeEventListener('focusout', allowAssistantFocus, true))

const panelStyle = computed(() => {
  const h = Math.min(560, window.innerHeight - 80)
  if (panelPos.value) {
    return { left: panelPos.value.x + 'px', top: panelPos.value.y + 'px', height: h + 'px' }
  }
  const top = Math.min(Math.max(pos.value.y - h / 2, 40), Math.max(40, window.innerHeight - h - 20))
  const { w } = bubbleSize()
  const onRight = pos.value.x + w / 2 > window.innerWidth / 2
  return onRight
    ? { right: '88px', top: top + 'px', height: h + 'px' }
    : { left: '88px', top: top + 'px', height: h + 'px' }
})

function onPanelPointerDown(e) {
  if (e.target.closest('.head-btn')) return
  const rect = panelRef.value.getBoundingClientRect()
  panelDragOffset = { x: e.clientX - rect.left, y: e.clientY - rect.top }
  window.addEventListener('pointermove', onPanelMove)
  window.addEventListener('pointerup', onPanelUp, { once: true })
}

function onPanelMove(e) {
  const w = panelRef.value ? panelRef.value.offsetWidth : 380
  const h = panelRef.value ? panelRef.value.offsetHeight : 560
  panelPos.value = {
    x: Math.min(Math.max(e.clientX - panelDragOffset.x, 0 - w + 120), window.innerWidth - 120),
    y: Math.min(Math.max(e.clientY - panelDragOffset.y, 0), window.innerHeight - 60)
  }
}

function onPanelUp() {
  window.removeEventListener('pointermove', onPanelMove)
}

/* ---------- 对话 ---------- */
const input = ref('')
const loading = ref(false)
const messages = ref([])
const msgBoxRef = ref(null)
let autoTimer = null

const suggestions = [
  '怎么给患者挂号？',
  '医生接诊、开处方的流程',
  '药品采购入库怎么做',
  '如何办理出院结算',
  '在哪里录入检验结果'
]

function hasGuide(m) {
  return (m.navigate && m.navigate.path) || (m.steps && m.steps.length)
}

// 用户消息里明确表达"要去做某个操作"才允许自动开始引导
const GUIDE_INTENT_RE = /怎么做|如何做|怎么办|怎么弄|怎么操作|如何操作|在哪|带我去|带我|演示|教我|帮我|指引我|操作步骤|操作一下|流程怎么走/
// 问题里提及当前页面/页面数据时，自动附带页面快照
const CONTEXT_RE = /本页|当前页|这个页面|这页|该页面|页面里|页面上|这些数据|这个表|上面的|屏幕|界面/
// 用户要求助手直接操作页面控件（"点击中成药""切换到库存预警"）
const ACTION_INTENT_RE = /^\s*(帮我|请|给我|帮忙|麻烦)?\s*(点击|点一下|点下|按下|切换到|切换成|切换至|转到|打开|关闭|勾选|选中|筛选|刷新)/
// 任务型请求（"帮我新建采购单""帮我给患者挂号"）→ 直接交给 Agent 代办
const AGENT_INTENT_RE = /(帮我|帮忙|麻烦|给我|请).{0,10}(新建|创建|添加|录入|办理|挂号|开方|开药|开检查|开检验|提交|申请|登记|填写|排班|结算|收费|发药|采购|入库|出库|出院|建档|审核|删除|删掉|移除)|^\s*(新建|创建|添加|录入|办理|挂号|提交|申请|登记|填写|删除|删掉|移除)|(删除|删掉|删了|移除)(刚|那个|这个|这条|那条|上一|它|掉|一下)|(把|将).{1,24}(删除|删掉|删了|移除)/
// 多步任务信号（"点一下这些，然后统计分析"）：连续动作交给 Agent 分步执行
const MULTI_STEP_RE = /然后|接着|随后|顺便|并且|再.{0,4}(统计|分析|汇总|检查|看看|筛选|对比)/
// 但带"怎么/如何"等请教字眼时，仍按求指导处理
const ASK_HOW_RE = /怎么|如何|学习|教程|介绍|流程/

// Agent 反问后挂起的任务目标：用户下一句回复作为补充信息继续执行
const pendingAgentGoal = shallowRef(null)
// Agent 暂停（步数上限/循环/重复提交护栏）后挂起的目标：回复「继续」从当前页面接着办
let pausedAgentGoal = null

// 触发过页面分析后，同一页面内的后续追问继续携带快照
let ctxRoute = null

function historyPayload() {
  return conversationPayload(messages.value)
}

async function send(text, options = {}) {
  const typed = (text !== undefined ? text : input.value).trim()
  const imgs = text === undefined ? pendingImages.value.splice(0).map(i => i.dataUrl) : []
  const content = typed || (imgs.length ? '请分析图片内容，告诉我你的理解' : '')
  if (!content || loading.value || agent.running) return
  // Agent 反问后的回复：并入原任务目标继续执行（可带截图补充）
  if (text === undefined && pendingAgentGoal.value) {
    const pending = pendingAgentGoal.value
    const goal = `${pending.goal}（待补充问题：${pending.record.finalSay}；用户补充：${content}）`
    pendingAgentGoal.value = null
    input.value = ''
    messages.value.push({ role: 'user', content, images: imgs.length ? imgs : undefined })
    scrollBottom()
    await runAgentGoal(goal, imgs, pending.record)
    return
  }
  // 暂停中的任务：回复「继续」从当前页面状态接着执行同一目标
  if (text === undefined && pausedAgentGoal && /^\s*(继续|接着|接着做|继续做|接着办|继续办|resume)\s*[!！。.]*$/.test(content)) {
    const goal = pausedAgentGoal
    pausedAgentGoal = null
    input.value = ''
    messages.value.push({ role: 'user', content, images: imgs.length ? imgs : undefined })
    scrollBottom()
    await runAgentGoal(goal, imgs)
    return
  }
  // 自动办事模式 / 任务型指令（"帮我新建采购单"）→ Agent 代办；
  // 带图的操作指令或多步指令（"点一下这些，然后统计"）也走 Agent，首轮携带截图
  const wantsAgent = !ASK_HOW_RE.test(content) && (
    (AGENT_INTENT_RE.test(content) && !ACTION_INTENT_RE.test(content))
    || (ACTION_INTENT_RE.test(content) && (imgs.length > 0 || MULTI_STEP_RE.test(content)))
  )
  if (text === undefined && (agentMode.value || wantsAgent)) {
    input.value = ''
    messages.value.push({ role: 'user', content, images: imgs.length ? imgs : undefined })
    scrollBottom()
    await runAgentGoal(content, imgs)
    return
  }
  const display = options.display || content
  const conversation = historyPayload()
  input.value = ''
  messages.value.push({
    role: 'user',
    content: display,
    apiContent: display === content ? undefined : content,
    images: imgs.length ? imgs : undefined
  })
  scrollBottom()
  loading.value = true
  try {
    const withCtx = !!(options.withContext || CONTEXT_RE.test(content) || ACTION_INTENT_RE.test(content) || ctxRoute === route.path)
    const res = await chatWithAssistant({
      message: content,
      ...conversation,
      page: `${route.path} ${route.meta.title || ''}`.trim(),
      pageContext: withCtx ? collectPageContext(route) : '',
      images: imgs
    })
    if (withCtx) ctxRoute = route.path
    const msg = {
      role: 'assistant',
      content: res.answer || '这个问题我暂时没有答案',
      navigate: res.navigate || null,
      steps: Array.isArray(res.steps) ? res.steps : []
    }
    messages.value.push(msg)
    scrollBottom()
    if (hasGuide(msg)) {
      if (msg.steps.length && ACTION_INTENT_RE.test(content)) {
        executeActions(msg)
      } else if (options.autoGuide || GUIDE_INTENT_RE.test(content)) {
        scheduleAutoGuide(msg)
      }
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: 'AI 服务暂时不可用，请稍后重试', error: true })
  } finally {
    loading.value = false
    scrollBottom()
  }
}

/* ---------- 图片附件（粘贴截图 / 选择文件，客户端压缩为 base64） ---------- */
const pendingImages = ref([])
const fileInput = ref(null)
const MAX_IMAGES = 4

function pickImages() {
  if (loading.value || agent.running) return
  if (fileInput.value) fileInput.value.click()
}

function onFiles(e) {
  const files = Array.from(e.target.files || []).filter(f => f.type.startsWith('image/'))
  e.target.value = ''
  addImages(files)
}

function onPaste(e) {
  const items = Array.from((e.clipboardData || {}).items || [])
  const files = items
    .filter(i => i.kind === 'file' && i.type.startsWith('image/'))
    .map(i => i.getAsFile())
    .filter(Boolean)
  if (!files.length) return
  e.preventDefault()
  addImages(files)
}

function addImages(files) {
  const room = MAX_IMAGES - pendingImages.value.length
  if (files.length && room <= 0) {
    ElMessage.warning(`最多添加 ${MAX_IMAGES} 张图片`)
    return
  }
  files.slice(0, room).forEach(f => {
    compressImage(f).then(dataUrl => {
      if (dataUrl) pendingImages.value.push({ name: f.name || '截图.png', dataUrl })
    })
  })
  if (files.length > room) ElMessage.warning(`最多添加 ${MAX_IMAGES} 张图片，多余的已忽略`)
}

// 压缩到最长边 1280px、JPEG，控制 base64 体积（约 200~400KB/张）
function compressImage(file) {
  return new Promise(resolve => {
    const reader = new FileReader()
    reader.onload = () => {
      const img = new Image()
      img.onload = () => {
        const MAX = 1280
        const scale = Math.min(1, MAX / Math.max(img.width, img.height))
        const w = Math.round(img.width * scale)
        const h = Math.round(img.height * scale)
        const canvas = document.createElement('canvas')
        canvas.width = w
        canvas.height = h
        canvas.getContext('2d').drawImage(img, 0, 0, w, h)
        resolve(canvas.toDataURL('image/jpeg', 0.82))
      }
      img.onerror = () => resolve(null)
      img.src = reader.result
    }
    reader.onerror = () => resolve(null)
    reader.readAsDataURL(file)
  })
}

// 分析当前页面：携带页面真实数据快照，让 AI 做解读与建议
function analyzePage() {
  if (loading.value) return
  send(
    '请完整分析当前页面的所有内容：先说明页面用途，再结合统计指标和表格中的每条记录逐一解读（引用具体数字），指出异常或需要关注的地方，并给出可操作建议。',
    { display: '分析当前页面', withContext: true }
  )
}

// 指引当前页面操作：携带快照让 AI 按真实按钮生成引导步骤
function guideMe() {
  if (loading.value) return
  send(
    '请一步一步指引我使用当前页面，告诉我最常用的操作应该点哪里。',
    { display: '教我操作本页', withContext: true, autoGuide: true }
  )
}

/* ---------- 自动办事 Agent ---------- */
const agentMode = ref(false)

onBeforeUnmount(() => {
  if (agent.running) agent.stop = true
  if (autoTimer) clearTimeout(autoTimer)
})

function toggleAgent() {
  if (agent.running) {
    agent.stop = true
    return
  }
  agentMode.value = !agentMode.value
}

// 把一次自动办事过程压缩成可追溯文本：目标、结果、关键步骤、办结时页面快照
function buildRecordText(r) {
  const outcomeText = {
    done: '已完成', asked: '等待用户补充信息', stopped: '被用户停止',
    error: '服务异常中断', denied: '用户取消了敏感操作', failed: '办理失败', paused: '暂停（达到步数上限或检测到重复操作）'
  }[r.outcome] || '已结束'
  const lines = [`【自动办事记录】目标：${r.goal}`, `结果：${outcomeText}，共执行 ${r.steps.length} 步`]
  if (r.finalSay) lines.push(`结语：${r.finalSay}`)
  const stepLines = r.steps.map((s, i) => {
    const v = s.value ? `=${s.value}` : ''
    const res = ['true', 'already', 'opened', 'closed', 'submitted'].includes(s.ok) ? '成功' : s.ok === 'denied' ? '用户拒绝' : '失败'
    return `${i + 1}.${s.type}「${s.target}」${v}→${res}`
  })
  const shown = stepLines.length > 30
    ? [...stepLines.slice(0, 8), `…中间省略 ${stepLines.length - 28} 步…`, ...stepLines.slice(-20)]
    : stepLines
  if (shown.length) lines.push('执行明细：\n' + shown.join('\n'))
  let text = lines.join('\n')
  // 剩余额度留给办结时的页面快照（新单据通常已能在列表中看到）
  const budget = 1900 - text.length
  if (r.snapshot && budget > 200) {
    text += `\n办结时页面「${r.page}」快照：\n` + r.snapshot.slice(0, budget)
  }
  return text.slice(0, 1900)
}

async function runAgentGoal(goal, images = [], previousRecord = null) {
  const conversation = historyPayload()
  // 结构化办事记录（含每步填写值）：让"删除刚创建的"能直接定位到具体记录
  const records = messages.value.filter(m => m.taskRecordData).map(m => m.taskRecordData).slice(-6)
  messages.value.push({ role: 'assistant', progress: true, content: previousRecord ? '收到补充信息，继续办理。' : `好的，开始为您办理「${goal}」` })
  scrollBottom()
  const runMsgs = []
  try {
    const outcome = await runAgent(goal, {
      route,
      router,
      images,
      previousRecord,
      records,
      ...conversation,
      onSay: text => {
        if (!text) return
        const m = { role: 'assistant', content: text, progress: true }
        messages.value.push(m)
        runMsgs.push(m)
        scrollBottom()
      }
    })
    // Agent 反问（缺信息）：挂起目标，等用户回复后继续；暂停的任务可回复「继续」断点续办
    pendingAgentGoal.value = outcome && outcome.asked ? { goal, record: outcome.record } : null
    pausedAgentGoal = outcome && outcome.outcome === 'paused' ? goal : null
    // 办事记录挂到最后一条进展消息上：界面只显示结语，历史里带完整记录供追问回溯
    if (outcome && outcome.record) {
      const recText = buildRecordText(outcome.record)
      const target = runMsgs.length ? runMsgs[runMsgs.length - 1] : null
      if (target) {
        target.apiContent = recText
        target.taskRecord = true
        target.taskOutcome = outcome.record.outcome
        target.taskRecordData = outcome.record
      } else {
        messages.value.push({ role: 'assistant', content: '本次自动办事已结束', apiContent: recText, taskRecord: true, taskOutcome: outcome.record.outcome, taskRecordData: outcome.record })
      }
    }
    // 后续追问携带当前页快照，刚创建的记录在表格里即可被看到
    ctxRoute = route.path
  } catch (e) {
    pendingAgentGoal.value = null
    messages.value.push({ role: 'assistant', content: '自动办事执行中断，请稍后重试', error: true })
  }
  scrollBottom()
}

// 直接执行页面操作（如"点击中成药页签"），执行结果回显到对话
async function executeActions(msg) {
  const { navigated, results } = await performActions({ navigate: msg.navigate, steps: msg.steps }, router)
  const ok = results.filter(r => r.ok)
  const fail = results.filter(r => !r.ok)
  const parts = []
  if (navigated) parts.push(`已跳转到「${(msg.navigate && msg.navigate.menuText) || ''}」`)
  if (ok.length) parts.push('已点击：' + ok.map(r => `「${r.target}」`).join('、'))
  if (fail.length) parts.push('未找到控件：' + fail.map(r => `「${r.target}」`).join('、'))
  messages.value.push({ role: 'assistant', content: parts.join('；') || '未执行任何操作' })
  scrollBottom()
}

function scheduleAutoGuide(msg) {
  if (autoTimer) clearTimeout(autoTimer)
  msg.autoGuidePending = true
  autoTimer = setTimeout(() => {
    msg.autoGuidePending = false
    panelOpen.value = false
    startGuide({ navigate: msg.navigate, steps: msg.steps }, router)
  }, 1500)
}

function replayGuide(msg) {
  if (autoTimer) clearTimeout(autoTimer)
  panelOpen.value = false
  startGuide({ navigate: msg.navigate, steps: msg.steps }, router)
}

function clearChat() {
  if (agent.running) agent.stop = true
  if (autoTimer) clearTimeout(autoTimer)
  ctxRoute = null
  pendingAgentGoal.value = null
  pausedAgentGoal = null
  messages.value = []
}

function scrollBottom() {
  nextTick(() => {
    if (msgBoxRef.value) msgBoxRef.value.scrollTop = msgBoxRef.value.scrollHeight
  })
}
</script>

<style scoped>
/* ---------- 气泡 ---------- */
.ai-bubble {
  position: fixed;
  height: 48px;
  padding: 0 16px;
  border-radius: 24px;
  background: linear-gradient(135deg, var(--current-color, #409eff) 0%, #7a5cff 100%);
  box-shadow: 0 6px 18px rgba(64, 120, 255, 0.45);
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: grab;
  z-index: 4500;
  user-select: none;
  touch-action: none;
}
.ai-bubble:hover {
  box-shadow: 0 8px 24px rgba(64, 120, 255, 0.6);
  transform: scale(1.04);
}
.ai-bubble.dragging {
  cursor: grabbing;
  transform: scale(1.05);
}
.bubble-text {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
  white-space: nowrap;
}
.bubble-dot :deep(.el-badge__content) {
  z-index: 3002;
}

/* ---------- 面板 ---------- */
.ai-panel {
  position: fixed;
  width: 380px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.22);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 3000;
}

.ai-panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: linear-gradient(135deg, var(--current-color, #409eff) 0%, #7a5cff 100%);
  color: #fff;
  cursor: move;
  user-select: none;
  touch-action: none;
}
.ai-panel-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
}
.ai-panel-actions {
  display: flex;
  gap: 10px;
}
.head-btn {
  cursor: pointer;
  opacity: 0.85;
}
.head-btn:hover { opacity: 1; }

.ai-panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 14px;
  background: #f6f8fc;
}

.ai-welcome { margin-top: 8px; }
.welcome-text {
  font-size: 13px;
  color: #606266;
  line-height: 1.7;
  margin-bottom: 10px;
}
.ai-suggests {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.suggest-chip {
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  color: #303133;
  cursor: pointer;
  transition: all 0.2s;
}
.suggest-chip:hover {
  border-color: var(--current-color, #409eff);
  color: var(--current-color, #409eff);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.18);
}

.ai-msg {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
.ai-msg.user { align-items: flex-end; }
.ai-msg.assistant { align-items: flex-start; }

.ai-msg-bubble {
  max-width: 85%;
  padding: 9px 12px;
  border-radius: 10px;
  font-size: 13px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.ai-msg.user .ai-msg-bubble {
  background: var(--current-color, #409eff);
  color: #fff;
  border-bottom-right-radius: 2px;
}
.ai-msg.assistant .ai-msg-bubble {
  background: #fff;
  color: #303133;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 2px;
}
.ai-msg-bubble.is-error {
  color: #f56c6c;
  border-color: #fde2e2;
  background: #fef0f0;
}

.ai-msg-guide {
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.guide-hint {
  font-size: 12px;
  color: #e6a23c;
}

.ai-thinking {
  display: flex;
  gap: 5px;
  align-items: center;
  padding: 12px 14px;
}
.ai-thinking .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #c0c4cc;
  animation: ai-blink 1.2s infinite;
}
.ai-thinking .dot:nth-child(2) { animation-delay: 0.2s; }
.ai-thinking .dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes ai-blink {
  0%, 80%, 100% { opacity: 0.3; }
  40% { opacity: 1; }
}

.ai-panel-foot {
  display: flex;
  flex-direction: column;
  border-top: 1px solid #ebeef5;
  background: #fff;
}

.ai-quick {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px 0;
}
.ai-page-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  max-width: 140px;
  margin-right: auto;
}
.ai-page-tag .tag-text {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.ai-quick-btn {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: var(--current-color, #409eff);
  border: 1px solid var(--current-color, #409eff);
  border-radius: 10px;
  padding: 2px 8px;
  cursor: pointer;
  white-space: nowrap;
  user-select: none;
}
.ai-quick-btn:hover {
  background: rgba(64, 158, 255, 0.08);
}
.ai-quick-btn.active {
  background: var(--current-color, #409eff);
  color: #fff;
}
.ai-quick-btn.disabled {
  opacity: 0.5;
  pointer-events: none;
}

.ai-input-row {
  display: flex;
  gap: 8px;
  padding: 10px 12px;
}
.attach-btn {
  align-self: center;
  font-size: 18px;
  color: #909399;
  cursor: pointer;
  flex-shrink: 0;
}
.attach-btn:hover {
  color: var(--current-color, #409eff);
}
.attach-btn.disabled {
  opacity: 0.4;
  pointer-events: none;
}

.ai-msg-imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 6px;
}
.msg-img {
  width: 120px;
  height: 90px;
  border-radius: 6px;
  cursor: zoom-in;
}

.ai-pending-imgs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 12px 0;
}
.pending-img {
  position: relative;
  width: 56px;
  height: 56px;
}
.pending-img img {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}
.pending-del {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 14px;
  height: 14px;
  background: #f56c6c;
  color: #fff;
  border-radius: 50%;
  font-size: 9px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
