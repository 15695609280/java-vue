// playwright-cli run-code --filename tests/agent-history.cjs
// Actual assistant and department form; API writes are intercepted.
async (page) => {
  const check = (ok, message) => { if (!ok) throw new Error(message) }
  await page.unrouteAll({ behavior: 'wait' })
  await page.context().clearCookies()
  const requests = []
  const writes = []
  const actions = [
    { type: 'click', target: '新增科室' },
    { type: 'input', target: '科室名称', value: '回归康复科' },
    { type: 'input', target: '科室编码', value: 'TESTKF' },
    { type: 'select', target: '科室类型', value: '门诊科室' },
    { type: 'click', target: '确定' },
    { type: 'done' }
  ]
  let step = 0
  await page.route('**/dev-api/**', async route => {
    const req = route.request()
    const url = req.url().split('?')[0]
    let response = { code: 200, data: [], rows: [], total: 0, captchaEnabled: false }
    if (url.endsWith('/ai/agent')) {
      requests.push({ kind: 'agent', ...req.postDataJSON() })
      const action = actions[step++] || { type: 'ask' }
      response = { code: 200, action, say: action.type === 'done' ? '已创建回归康复科，编码TESTKF' : action.type === 'ask' ? '请确认要处理的科室' : '正在办理' }
    } else if (url.endsWith('/ai/chat')) {
      requests.push({ kind: 'chat', ...req.postDataJSON() })
      response.answer = '已收到本轮问题'
    } else if (url.endsWith('/system/dict/data/type/his_dept_type')) {
      response.data = [{ dictLabel: '门诊科室', dictValue: '0' }]
    } else if (req.method() === 'POST' && url.endsWith('/his/dept')) {
      writes.push(req.postDataJSON())
    }
    await route.fulfill({ json: response })
  })
  await page.goto('http://localhost:5174/login')
  await page.getByPlaceholder('账号', { exact: true }).waitFor()
  await page.evaluate(async () => {
    const app = document.querySelector('#app').__vue_app__
    const router = app.config.globalProperties.$router
    const { h } = await import('/node_modules/.vite/deps/vue.js')
    const { default: Dept } = await import('/src/views/his/base/dept/index.vue')
    app._context.directives.hasPermi = {}
    app.config.globalProperties.$pinia._s.get('user').roles = ['admin']
    document.cookie = 'Admin-Token=regression-only; path=/'
    router.addRoute({ path: '/his/base/dept', component: { render: () => h('main', { class: 'app-main' }, h(Dept)) } })
    await router.push('/his/base/dept')
  })
  await page.locator('.ai-bubble').click()
  await page.locator('.ai-quick-btn').filter({ hasText: '自动办事' }).click()
  const input = page.locator('.ai-panel input.el-input__inner')
  const send = async text => {
    await input.fill(text)
    await input.press('Enter')
    await page.waitForFunction(() => !document.querySelector('.ai-panel input.el-input__inner').disabled
      && !document.querySelector('.ai-panel input.el-input__inner').value)
  }
  await send('帮我新增科室回归康复科，编码TESTKF，类型门诊科室')
  await page.getByText('已创建回归康复科，编码TESTKF', { exact: true }).waitFor()
  check(writes.length === 1 && writes[0].deptCode === 'TESTKF', 'Department creation failed')
  await page.locator('.ai-quick-btn').filter({ hasText: '自动办事' }).click()
  for (let i = 0; i < 22; i++) await send('普通问题' + i)
  const chat = requests.at(-1)
  check(chat.history.length === 40, 'Expected 40 effective chat messages')
  check(chat.history.some(m => m.content === '普通问题3'), 'History still limited to eight messages')
  check(!chat.history.some(m => m.content === '正在办理'), 'Progress polluted conversation')
  check(chat.taskRecords.some(r => r.includes('TESTKF') && r.includes('已完成')), 'Completed task was evicted by chat')
  await page.locator('.ai-quick-btn').filter({ hasText: '自动办事' }).click()
  await send('删除刚才新增的这个')
  check(requests.at(-1).kind === 'agent' && requests.at(-1).taskRecords.some(r => r.includes('TESTKF')), 'Agent lost prior task identity')
  check(requests.at(-1).history.length === 40, 'Agent missing conversation')
  for (let i = 0; i < 7; i++) await send('补充说明' + i)
  check(requests.at(-1).taskRecords.some(r => r.includes('TESTKF')), 'Clarifications evicted completed task')
  await page.locator('.ai-panel-actions .head-btn').first().click()
  await send('新增科室')
  check(requests.at(-1).taskRecords.length === 0 && !JSON.stringify(requests.at(-1).history).includes('TESTKF'), 'Clear retained old context')
  const bounds = await page.evaluate(async () => {
    const { conversationPayload } = await import('/src/components/AiAssistant/conversation.js')
    const items = Array.from({ length: 100 }, (_, i) => ({ role: 'user', content: String(i) + 'x'.repeat(5000) }))
    const result = conversationPayload(items)
    return { length: result.history.reduce((sum, m) => sum + m.content.length, 0), last: result.history.at(-1).content.slice(0, 2) }
  })
  check(bounds.length === 24000 && bounds.last === '99', 'History budget did not preserve newest messages')
  await page.context().clearCookies()
  return { chatRounds: 22, retainedMessages: 40, agentHasPriorTask: true, clarificationKeepsTask: true, clearResetsContext: true, writes: writes.length }
}
