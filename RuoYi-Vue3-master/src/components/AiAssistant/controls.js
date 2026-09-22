import { visible, norm, controlName, CLOSE_CONTROL } from './guide'

export function topDialog() {
  return Array.from(document.querySelectorAll('.el-dialog, .el-drawer, .el-message-box'))
    .filter(el => visible(el) && !el.closest('.ai-panel'))
    .map((el, index) => ({ el, index, z: Number(getComputedStyle(el.closest('.el-overlay') || el).zIndex) || 0 }))
    .sort((a, b) => a.z - b.z || a.index - b.index).at(-1)?.el || null
}

export function isDismissControl(el, dialog = topDialog()) {
  return !!(el && dialog?.contains(el) && (el.matches(CLOSE_CONTROL)
    || /^(关闭|取消|close|cancel)$/i.test(norm(controlName(el)))))
}

export function findDismissControl(target, dialog = topDialog(), { lenient = false } = {}) {
  if (!dialog) return null
  const text = norm(target).replace(/^(点击|单击|点一下|按下)/, '')
  const strict = /^(关闭|关掉|取消|close|cancel|×|x)(当前|这个|详情|弹窗|弹框|窗口|对话框|抽屉|页面|右上角|的|按钮|图标)*$/i.test(text)
  // 宽松模式："关闭科室详情弹窗"这类带业务名的说法也算关闭意图（仅在弹窗内找不到同名按钮时使用）
  const loose = lenient && /^(关闭|关掉|close)/i.test(text) && /(弹窗|弹框|窗口|对话框|抽屉|详情|页面|它|掉)$/.test(text)
  if (!strict && !loose) return null
  const buttons = Array.from(dialog.querySelectorAll('button,.el-button'))
    .filter(el => visible(el) && enabled(el) && isDismissControl(el, dialog))
  const exact = buttons.find(el => norm(controlName(el)) === text)
  return exact || buttons.find(el => el.matches(CLOSE_CONTROL)) || buttons[0] || null
}

export function inputScopes() {
  const dialog = topDialog()
  return [dialog || document.querySelector('.app-main') || document.body]
}

export function selectedText(select) {
  // The search input and the empty placeholder are not selected values.
  return Array.from(select.querySelectorAll('.el-select__selected-item:not(.el-select__input-wrapper):not(.is-transparent), .el-select-tags-wrapper .el-tag'))
    .map(el => (el.textContent || '').trim()).filter(Boolean).join('、')
}

export function enabled(el) {
  return el && !el.disabled && el.getAttribute('aria-disabled') !== 'true'
    && !el.closest('.is-disabled, .is-loading')
}
