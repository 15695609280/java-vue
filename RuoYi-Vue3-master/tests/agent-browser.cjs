// Run with a Vite dev server on 5174 and an open playwright-cli session:
// playwright-cli run-code --filename tests/agent-browser.cjs
// All API requests are mocked; no patient records are created.
async (page) => {
  const check = (ok, message) => { if (!ok) throw new Error(message) }
  const scenarios = ['success', 'continuation', 'validation', 'server-error', 'network-error', 'stopped']
  const results = []
  for (const scenario of scenarios) {
    await page.unrouteAll({ behavior: 'wait' })
    let writes = 0
    let queries = []
    let payload
    let step = 0
    const actions = [
      { type: 'click', target: '住院登记' },
      { type: 'select', target: '患者', value: '回归患者' },
      { type: 'select', target: '科室', value: '内科' },
      { type: 'select', target: '病区', value: '内科一病区' },
      { type: 'select', target: '床位', value: '01-01床' },
      { type: 'select', target: '主治医生', value: '回归医生' },
      { type: 'input', target: '预缴押金', value: '5000' },
      { type: 'input', target: '入院诊断', value: '自动化回归测试' },
      { type: 'click', target: '确认登记' }
    ]
    if (scenario === 'continuation') actions.push({ type: 'click', target: '搜索' })
    await page.route('**/dev-api/**', async route => {
      const req = route.request()
      const url = await page.evaluate(value => {
        const parsed = new URL(value)
        return { pathname: parsed.pathname, patientName: parsed.searchParams.get('patientName') }
      }, req.url())
      let response = { code: 200, data: [], total: 0 }
      if (url.pathname.endsWith('/ai/agent')) {
        const body = req.postDataJSON()
        queries.push(body)
        if (scenario === 'stopped') {
          await page.evaluate(async () => { (await import('/src/components/AiAssistant/agent.js')).agent.stop = true })
        }
        const action = scenario === 'validation'
          ? [actions[0], actions[8], { type: 'ask' }][step++]
          : actions[step++] || { type: scenario === 'server-error' ? 'fail' : 'done' }
        response = { code: 200, action, done: ['done', 'ask', 'fail'].includes(action.type), say: '回归检查' }
      } else if (url.pathname.endsWith('/his/admission/admit')) {
        writes++
        payload = req.postDataJSON()
        await page.waitForTimeout(1800)
        if (scenario === 'network-error') return route.abort('failed')
        response = { code: scenario === 'server-error' ? 500 : 200, msg: scenario === 'server-error' ? '床位已占用' : '操作成功' }
      } else if (url.pathname.endsWith('/his/patient/list')) {
        await page.waitForTimeout(1100)
        response.data = url.patientName === '回归患者'
          ? [{ patientId: 901, patientName: '回归患者', patientNo: 'TEST901' }] : []
      } else if (url.pathname.endsWith('/his/dept/list')) {
        response.data = [{ deptId: 11, deptName: '内科' }]
      } else if (url.pathname.endsWith('/his/department/list')) {
        response.data = [{ deptId: 11, deptName: '内科' }]
      } else if (url.pathname.endsWith('/his/ward/list')) {
        response.data = [{ wardId: 21, wardName: '内科一病区', location: '1楼' }]
      } else if (url.pathname.includes('/his/bed/free/')) {
        response.data = [{ bedId: 31, bedNo: '01-01', pricePerDay: 100 }]
      } else if (url.pathname.endsWith('/his/doctor/list')) {
        response.data = [{ doctorId: 41, doctorName: '回归医生' }]
      }
      await route.fulfill({ json: response })
    })
    await page.goto('http://localhost:5174/login')
    await page.waitForFunction(() => document.querySelector('#app')?.__vue_app__)
    await page.evaluate(async () => {
      const { createApp, h } = await import('/node_modules/.vite/deps/vue.js')
      const { default: Admission } = await import('/src/views/his/inpatient/admission/index.vue')
      const old = document.querySelector('#app').__vue_app__
      document.querySelector('#app').style.display = 'none'
      const host = document.createElement('main')
      host.className = 'app-main'
      document.body.append(host)
      const app = createApp({ render: () => h(Admission) })
      Object.assign(app._context, old._context)
      app._context.directives = { ...old._context.directives, hasPermi: {} }
      app.mount(host)
      window.runRegression = async () => {
        const { runAgent } = await import('/src/components/AiAssistant/agent.js')
        return runAgent('为回归患者办理住院登记', {
          route: { path: '/his/inpatient/admission', meta: { title: '住院登记' } }, onSay: () => {}
        })
      }
    })
    await page.getByRole('button', { name: '住院登记', exact: true }).waitFor()
    const result = await page.evaluate(() => window.runRegression())
    if (['success', 'continuation', 'server-error', 'network-error'].includes(scenario)) {
      check(writes === 1, `${scenario}: expected one write, got ${writes}: ${JSON.stringify(result.record.steps)}`)
      check(payload.patientId === 901 && payload.deptId === 11 && payload.wardId === 21 && payload.bedId === 31 && payload.doctorId === 41 && payload.deposit === 5000, 'Wrong submitted fields: ' + JSON.stringify(payload))
      const last = queries.at(-1)
      const submitted = result.record.steps.find(item => item.target === '确认登记')
      const expected = scenario === 'network-error' ? 'submit-unknown' : scenario === 'server-error' ? 'submitted-open' : 'submitted'
      check(submitted.ok === expected, 'Wrong submit feedback: ' + JSON.stringify(submitted))
      if (scenario === 'network-error') {
        check(result.outcome === 'paused' && queries.length === actions.length, 'Uncertain submission must pause without retrying')
      } else if (scenario === 'continuation') {
        check(result.record.steps.at(-1).target === '搜索' && result.record.steps.at(-1).ok === 'true', 'Remaining task was not executed')
        check(result.outcome === 'done', 'Continuation did not complete')
      } else {
        check(last.pageContext.includes(scenario === 'success' ? '住院登记成功' : '床位已占用'), 'Missing outcome in snapshot')
        check(result.outcome === (scenario === 'success' ? 'done' : 'failed'), 'Incorrect task outcome')
      }
      const queryValue = await page.locator('.app-main > .app-container > .el-form .el-select').first().innerText()
      check(!queryValue.includes('内科'), 'Background query field was changed')
    } else if (scenario === 'validation') {
      check(writes === 0, 'Validation failure must not write')
      check(queries.at(-1).pageContext.includes('请选择患者'), 'Missing validation errors')
      check(result.outcome === 'asked', 'Should request missing information')
    } else {
      check(result.stopped && result.record.steps.length === 0, 'Stopped agent executed a late response')
    }
    results.push({ scenario, outcome: result.outcome, writes, decisions: queries.length })
  }
  return results
}
