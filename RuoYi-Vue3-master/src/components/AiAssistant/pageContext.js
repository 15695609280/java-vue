import { topDialog, selectedText } from './controls'
import { visible, controlName } from './guide'

/**
 * 采集当前页面的 DOM 快照，供 AI 结合真实数据做页面分析。
 * 只读取可见文本（统计数字、表格行列、表单值、页签、按钮、弹窗等），
 * 不读取密码框内容，输出整体限长。
 */
const MAX_LEN = 20000
const MAX_TABLES = 3
const MAX_ROWS = 100
const MAX_BUTTONS = 25
const MAX_FORM_ITEMS = 20
const MAX_TREE_NODES = 40

function t(el) {
  return (el ? el.innerText || '' : '').replace(/\s+/g, ' ').trim()
}

function isVisible(el) {
  return visible(el)
}

function cellText(cell) {
  const c = cell.querySelector('.cell')
  const select = cell.querySelector('.el-select')
  if (select) return selectedText(select) || '(未选择)'
  const input = cell.querySelector('input:not([type=password]):not([type=checkbox]):not([type=radio]), textarea')
  if (input) return input.value || '(未填写)'
  return t(c || cell)
}

/** 读取一个 el-table：列名 + 前 N 行数据 */
function readTable(table) {
  const heads = Array.from(table.querySelectorAll('thead th'))
    .map(cellText)
    .filter(Boolean)
  const trs = Array.from(table.querySelectorAll('tbody tr')).filter(isVisible)
  const rows = []
  for (const tr of trs.slice(0, MAX_ROWS)) {
    const cells = Array.from(tr.querySelectorAll('td')).map(cellText).filter(Boolean)
    if (cells.length) rows.push(cells.join(' | '))
  }
  const empty = !trs.length && table.querySelector('.el-table__empty-text')
  return { heads, rows, total: trs.length, empty: !!empty }
}

/** 读取表单：返回全部字段名 + 已填写的值（跳过密码框） */
function readForm(scope) {
  const filled = []
  const labels = []
  const missing = []
  for (const item of scope.querySelectorAll('.el-form-item')) {
    if (!isVisible(item)) continue
    let label = t(item.querySelector('.el-form-item__label')).replace(/[:：]\s*$/, '')
    // 无 label 的表单项（如独立复选框"仅看低库存"），用控件文字作字段名
    if (!label) {
      const cb = item.querySelector('.el-checkbox, .el-switch')
      if (cb) {
        label = t(cb)
        const value = cb.classList.contains('is-checked') ? '开' : '关'
        if (label && filled.length < MAX_FORM_ITEMS) filled.push(`${label}=${value}`)
        if (label && labels.length < MAX_FORM_ITEMS && !labels.includes(label)) labels.push(label)
      }
      continue
    }
    if (labels.length < MAX_FORM_ITEMS && !labels.includes(label)) labels.push(label)
    let value = ''
    const select = item.querySelector('.el-select')
    const input = !select && item.querySelector('input:not([type=checkbox]):not([type=radio]):not([type=password]), textarea')
    if (input && input.value) value = String(input.value).trim()
    if (select) value = selectedText(select)
    if (!value) {
      const checks = Array.from(item.querySelectorAll('.el-checkbox.is-checked .el-checkbox__label')).map(t).filter(Boolean)
      if (checks.length) value = checks.join('、')
    }
    if (!value) {
      const radio = item.querySelector('.el-radio.is-checked .el-radio__label, .el-radio-button.is-checked .el-radio-button__inner')
      if (radio) value = t(radio)
    }
    if (!value) {
      const sw = item.querySelector('.el-switch')
      if (sw) value = sw.classList.contains('is-checked') ? '开' : '关'
    }
    if (value && filled.length < MAX_FORM_ITEMS) filled.push(`${label}=${value}`)
    if (!value && item.classList.contains('is-required') && !item.querySelector('input[type=password]')
      && missing.length < MAX_FORM_ITEMS) missing.push(label)
  }
  return { filled, labels, missing }
}

export function activeFormState() {
  return readForm(topDialog() || document.querySelector('.app-main') || document.body)
}

/** 页面上可见的按钮文字（去重、限长） */
function readButtons(scope) {
  const out = []
  for (const b of scope.querySelectorAll('.el-button, button')) {
    if (!isVisible(b)) continue
    const x = controlName(b)
    if (x && x.length <= 12 && !out.includes(x)) out.push(x)
    if (out.length >= MAX_BUTTONS) break
  }
  return out
}

/**
 * 生成当前页面快照文本
 * @param route 当前路由对象（取 meta.title / path）
 * @returns {string} 供提示词使用的多行文本
 */
export function collectPageContext(route) {
  const lines = []
  try {
    const title = (route && route.meta && route.meta.title) || document.title || ''
    const path = (route && route.path) || window.location.pathname
    lines.push(`页面：${title}（${path}）`)

    const root = document.querySelector('.app-main .app-container')
      || document.querySelector('.app-container')
      || document.querySelector('.app-main')
      || document.body

    // 统计数字 / 指标卡（.stat/.stat-card 为项目自定义看板样式）
    const stats = []
    for (const el of root.querySelectorAll('.el-statistic, .card-panel, .stat, .stat-card, [class*="statistic"]')) {
      if (!isVisible(el)) continue
      const s = t(el)
      if (s && s.length <= 80 && /\d/.test(s) && !stats.includes(s)) stats.push(s)
      if (stats.length >= 16) break
    }
    if (stats.length) lines.push('统计/指标：' + stats.join('；'))

    const cards = Array.from(root.querySelectorAll('.dept-card')).filter(isVisible).slice(0, MAX_ROWS)
    if (cards.length) lines.push('科室列表：' + cards.map(card =>
      `${t(card.querySelector('.dname'))}（编码：${t(card.querySelector('.code'))}）`).join('；'))

    // 表单字段与已填条件
    const { filled, labels } = readForm(root)
    if (filled.length) lines.push('已填表单值：' + filled.join('；'))
    if (labels.length) lines.push('查询/表单字段：' + labels.join('、'))

    // 页签
    const tabs = [...new Set(
      Array.from(root.querySelectorAll('.el-tabs__item, .el-radio-button')).filter(isVisible).map(t).filter(Boolean)
    )].slice(0, 15)
    if (tabs.length) {
      const active = t(root.querySelector('.el-tabs__item.is-active')) || t(root.querySelector('.el-radio-button.is-active'))
      lines.push(`页签：${tabs.join('、')}${active ? `（当前：${active}）` : ''}`)
    }

    // 描述列表（详情信息）
    const descs = []
    for (const cell of root.querySelectorAll('.el-descriptions__body td, .el-descriptions__body .el-descriptions__cell')) {
      if (!isVisible(cell)) continue
      const l = t(cell.querySelector('.el-descriptions__label'))
      const c = t(cell.querySelector('.el-descriptions__content')) || (l ? '' : t(cell))
      if (l || c) descs.push(l ? `${l}:${c}` : c)
      if (descs.length >= 20) break
    }
    if (descs.length) lines.push('详情：' + descs.join('；'))

    // 树形结构（部门/菜单等）
    const treeNodes = [...new Set(
      Array.from(root.querySelectorAll('.el-tree .el-tree-node__label, .el-tree .el-tree-node__content'))
        .filter(isVisible).map(t).filter(Boolean)
    )].slice(0, MAX_TREE_NODES)
    if (treeNodes.length) lines.push('树形节点：' + treeNodes.join('、'))

    // 步骤条
    const stepTitles = Array.from(root.querySelectorAll('.el-step__title')).filter(isVisible).map(t).filter(Boolean)
    if (stepTitles.length) lines.push('流程步骤：' + stepTitles.join(' → '))

    // 数据表格
    const tables = Array.from(root.querySelectorAll('.el-table')).filter(isVisible).slice(0, MAX_TABLES)
    tables.forEach((tb, i) => {
      const { heads, rows, total, empty } = readTable(tb)
      if (!heads.length && !rows.length && !empty) return
      lines.push(`表格${tables.length > 1 ? i + 1 : ''}（列：${heads.join(' | ') || '未知'}，当前显示 ${total} 行${empty ? '，暂无数据' : ''}）`)
      rows.forEach(r => lines.push('  ' + r))
      if (total > rows.length) lines.push(`  …其余 ${total - rows.length} 行未列出`)
    })

    // 分页总数
    const totalText = t(root.querySelector('.el-pagination__total'))
    if (totalText) lines.push('分页：' + totalText)

    // 页面提示
    const alerts = Array.from(root.querySelectorAll('.el-alert')).filter(isVisible).map(t).filter(Boolean).slice(0, 3)
    if (alerts.length) lines.push('页面提示：' + alerts.join('；'))

    // 图表数量（canvas 内部数据读不到）
    const canvasCount = root.querySelectorAll('canvas').length
    if (canvasCount) lines.push(`图表：页面含 ${canvasCount} 个图表（无法读取图表内部数据）`)

    // 页面按钮
    const btns = readButtons(root)
    if (btns.length) lines.push('页面按钮：' + btns.join('、'))

    // 打开的弹窗 / 抽屉
    const dlg = topDialog()
    if (dlg) {
      const dTitle = t(dlg.querySelector('.el-dialog__title, .el-drawer__title, .el-drawer__header, .el-message-box__title'))
      lines.push(`打开的弹窗：「${dTitle || '未命名'}」`)
      const dForm = readForm(dlg)
      if (dForm.filled.length) lines.push('弹窗表单已填：' + dForm.filled.join('；'))
      if (dForm.labels.length) lines.push('弹窗表单字段：' + dForm.labels.join('、'))
      if (dForm.missing.length) lines.push('弹窗尚未填写的必填项：' + dForm.missing.join('、'))
      const errors = Array.from(dlg.querySelectorAll('.el-form-item__error')).filter(isVisible).map(t)
      if (errors.length) lines.push('表单校验错误：' + errors.join('；'))
      const dTable = dlg.querySelector('.el-table')
      if (dTable) {
        const d = readTable(dTable)
        if (d.heads.length) {
          lines.push(`弹窗表格（列：${d.heads.join(' | ')}，显示 ${d.total} 行）`)
          d.rows.forEach(r => lines.push('  ' + r))
        }
      }
      const dBtns = readButtons(dlg)
      if (dBtns.length) lines.push('弹窗按钮：' + dBtns.join('、'))
      if (dBtns.includes('关闭')) lines.push('关闭当前弹窗：click target="关闭"（右上角关闭图标）；关闭后重新观察页面，再执行后续操作。')
    }

    const notices = Array.from(document.querySelectorAll('.el-message, .el-notification')).filter(isVisible).map(t)
    if (notices.length) lines.push('操作结果提示：' + notices.join('；'))

    // 打开中的下拉/弹出选项（teleport 到 body，不在 root 内）
    const popItems = []
    for (const pop of document.querySelectorAll('.el-select-dropdown, .el-dropdown-menu, .el-cascader__dropdown')) {
      if (!isVisible(pop)) continue
      for (const it of pop.querySelectorAll('.el-select-dropdown__item, .el-dropdown-menu__item, .el-cascader-node')) {
        const v = t(it)
        if (v) popItems.push(v)
        if (popItems.length >= 40) break
      }
      if (popItems.length >= 40) break
    }
    if (popItems.length) lines.push('当前弹出的选项：' + [...new Set(popItems)].join('、'))
  } catch (e) {
    // 采集失败不阻塞对话
  }
  let out = lines.join('\n')
  if (out.length > MAX_LEN) out = out.slice(0, MAX_LEN) + '…(内容过长已截断)'
  return out
}
