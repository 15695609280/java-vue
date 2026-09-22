// playwright-cli run-code --filename tests/agent-close.cjs
// Real department drawer and LangGraph runtime; all backend requests are mocked.
async (page) => {
  const check = (ok, message) => { if (!ok) throw new Error(message) }
  const realModel = page.url().endsWith('#real-model')
  const results = []
  for (const scenario of realModel ? ['关闭'] : ['关闭', '关闭抽屉', '关闭科室详情弹窗', '取消', 'close', 'stacked', 'blocked', 'aria', 'direct']) {
    await page.unrouteAll({ behavior: 'wait' })
    await page.context().clearCookies()
    let records = [
      { deptId: 11, deptName: '康复医疗', deptCode: 'KFYX', deptType: '0' },
      { deptId: 22, deptName: '测试', deptCode: 'TEST001', deptType: '0' }
    ]
    const requests = []
    const writes = []
    const close = { type: 'click', target: ['stacked', 'blocked', 'aria'].includes(scenario) ? '关闭' : scenario }
    // The explicit deletion auto-confirms the system prompt, so no separate 确定 decision is needed.
    const actions = scenario === 'blocked' ? [close, close, close] : [close,
      { type: 'click', target: '删除' }, { type: 'done' }]
    if (scenario === 'stacked') actions.unshift(close)
    if (scenario === 'aria') actions.unshift({ type: 'click', target: '刷新统计' })
    await page.route('**/dev-api/**', async route => {
      const request = route.request()
      const url = await page.evaluate(value => new URL(value).pathname, request.url())
      let response = { code: 200, data: [], rows: [], total: 0, captchaEnabled: false }
      if (url.endsWith('/ai/agent')) {
        requests.push(request.postDataJSON())
        if (realModel) {
          check(requests.length <= 16, 'Real model did not complete within 16 decisions')
          const decision = await page.request.post('http://127.0.0.1:8089/step', { data: request.postDataJSON(), timeout: 90000 })
          return route.fulfill({ json: await decision.json() })
        }
        check(requests.length <= actions.length, 'Unexpected loop: ' + scenario)
        response = { code: 200, action: actions[requests.length - 1], say: '关闭后继续办理' }
      } else if (url.endsWith('/his/dept/list')) response.data = records
      else if (request.method() === 'DELETE') {
        writes.push(url)
        await page.waitForTimeout(600)
        records = records.filter(record => !url.endsWith('/' + record.deptId))
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
    await page.locator('.dept-card').first().getByRole('button', { name: '详情', exact: true }).click()
    await page.locator('.el-drawer__close-btn').waitFor()
    await page.locator('.ai-bubble').click()
    if (scenario === 'direct') {
      const result = await page.evaluate(async () => {
        const { performActions } = await import('/src/components/AiAssistant/guide.js')
        return performActions({ steps: [{ target: '关闭' }] })
      })
      check(result.results[0].ok && await page.locator('.ai-panel').isVisible(), 'Direct close failed')
      check(writes.length === 0 && requests.length === 0, 'Direct close started unwanted task')
      results.push({ scenario, closed: true })
      continue
    }
    const result = await page.evaluate(async scenario => {
      const { runAgent, agent } = await import('/src/components/AiAssistant/agent.js')
      const { watch } = await import('/node_modules/.vite/deps/vue.js')
      const { ElMessageBox } = await import('/node_modules/.vite/deps/element-plus.js')
      if (scenario === 'stacked') {
        ElMessageBox.alert('顶层提示', '回归提示').catch(() => {})
        await new Promise(resolve => setTimeout(resolve, 350))
      }
      if (scenario === 'blocked') {
        document.querySelector('.el-drawer__close-btn').addEventListener('click', event => event.stopImmediatePropagation(), true)
      }
      if (scenario === 'aria') {
        const button = document.createElement('button')
        button.setAttribute('aria-label', '刷新统计')
        button.style.cssText = 'width:32px;height:32px'
        button.addEventListener('click', () => { window.ariaButtonClicked = true })
        document.querySelector('.el-drawer__body').append(button)
      }
      const phases = []
      const unwatch = watch(() => agent.phase, phase => phases.push(phase), { flush: 'sync' })
      const router = document.querySelector('#app').__vue_app__.config.globalProperties.$router
      const messages = []
      const result = await runAgent('删除测试这个科室，编码TEST001', {
        router, route: router.currentRoute.value, onSay: text => messages.push(text)
      })
      unwatch()
      return { ...result, phases, messages, ariaButtonClicked: window.ariaButtonClicked }
    }, scenario)
    check(!result.error, 'Workflow failed: ' + JSON.stringify(result))
    check(requests[0].pageContext.includes('弹窗按钮：关闭'), 'Icon close missing from snapshot')
    check(!result.messages.some(message => message.includes('等待确认')), 'Dismissal triggered danger confirmation')
    check(result.phases.slice(0, 4).join() === 'observe,decide,execute,verify', 'State graph phases missing')
    check(await page.locator('.ai-panel').isVisible(), 'Assistant was closed instead of business drawer')
    if (scenario === 'blocked') {
      check(result.outcome === 'paused' && writes.length === 0, 'Blocked close should pause without deleting')
      check(result.record.steps.every(step => step.ok === 'close-blocked'), 'Blocked close reported success')
      check(requests[1].pageContext.includes('不算成功'), 'Close failure not fed back to model')
    } else {
      check(result.outcome === 'done', 'Workflow did not complete')
      check(writes.length === 1 && writes[0].endsWith('/his/dept/22'), 'Wrong deletion or duplicate write')
      check(result.record.steps.filter(step => step.ok === 'closed').length === (scenario === 'stacked' ? 2 : 1), 'Close not verified')
      check(result.record.steps.at(-1).ok === 'submitted', 'Deletion not verified')
      check(!requests.at(-1).pageContext.includes('打开的弹窗'), 'Stale dialog context')
      if (scenario === 'aria') check(result.ariaButtonClicked, 'Accessible icon name not actionable')
    }
    results.push({ scenario, realModel, outcome: result.outcome, writes: writes.length, decisions: requests.length })
  }
  await page.context().clearCookies()
  return results
}
