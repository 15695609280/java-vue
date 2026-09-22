import { visible } from './guide'

export function topDialog() {
  return Array.from(document.querySelectorAll('.el-dialog, .el-drawer, .el-message-box'))
    .filter(visible).at(-1) || null
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
