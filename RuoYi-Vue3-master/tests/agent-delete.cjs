// playwright-cli run-code --filename tests/agent-delete.cjs
// Actual department page; every backend request, including DELETE, is mocked.
async (page) => {
  let current = ''
  const check = (ok, message) => { if (!ok) throw new Error(`[${current}] ${message}`) }
  const results = []
  for (const scenario of ['detail-row', 'detail-auto', 'detail-duplicate', 'detail-wrong', 'detail-unknown', 'named-code', 'named', 'clarification', 'duplicate', 'duplicate-code', 'row', 'row-missing', 'referent', 'referent-other-detail', 'denied', 'stopped', 'allowed']) {
    current = scenario
    page.setDefaultTimeout(15000)
    await page.unrouteAll({ behavior: 'wait' })
    await page.context().clearCookies()
    let records = [
      { deptId: 11, deptName: scenario.includes('duplicate') ? '康复医疗' : '内科', deptCode: 'NK', deptType: '0' },
      { deptId: 22, deptName: '康复医疗', deptCode: 'KFYX', deptType: '0' }
    ]
    const requests = []
    const writes = []
    // Authorized deletions auto-confirm the system prompt, so the model only clicks 删除 once and then reports done.
    const actions = [{ type: 'click', target: '删除' }, { type: 'done' }]
    if (scenario === 'named-code') actions[0].row = 'KFYX'
    if (scenario === 'referent-other-detail') actions.unshift({ type: 'click', target: '关闭' })
    if (scenario.startsWith('detail-')) {
      if (scenario !== 'detail-auto' && scenario !== 'detail-unknown') actions[0].row = scenario === 'detail-wrong' ? 'NK' : '康复医疗（KFYX）'
      if (scenario === 'detail-duplicate') actions[0].row = 'KFYX'
      if (scenario !== 'detail-unknown') actions.unshift({ type: 'click', target: '关闭科室详情弹窗' })
    }
    if (scenario === 'clarification') actions.unshift({ type: 'click', target: '删除' })
    if (scenario === 'row') actions[0] = { type: 'click', target: '删除', row: 'KFYX' }
    if (scenario === 'row-missing') actions.splice(0, 1, { type: 'click', target: '删除', row: '骨科' }, { type: 'click', target: '删除', row: '康复医疗' })
    // Model-requested deletions (not in the user's command) still need the approval box, after which the system prompt is auto-confirmed too.
    if (['denied', 'stopped', 'allowed'].includes(scenario)) actions[0] = { type: 'click', target: '删除', row: '康复医疗' }
    await page.route('**/dev-api/**', async route => {
      const request = route.request()
      const url = await page.evaluate(value => new URL(value).pathname, request.url())
      let response = { code: 200, data: [], rows: [], total: 0, captchaEnabled: false }
      if (url.endsWith('/ai/agent')) {
        requests.push(request.postDataJSON())
        check(requests.length <= actions.length, 'Unexpected decision loop')
        const action = actions[requests.length - 1]
        response = { code: 200, action, say: action.type === 'done' ? '删除办理完成' : '正在办理删除' }
      } else if (url.endsWith('/his/dept/list')) {
        response.data = records
      } else if (request.method() === 'DELETE') {
        writes.push(url)
        await page.waitForTimeout(650)
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
    await page.locator('.dept-card').nth(1).waitFor()
    if (scenario === 'referent-other-detail') {
      await page.locator('.dept-card').first().getByRole('button', { name: '详情', exact: true }).click()
      await page.locator('.el-drawer__title').waitFor()
    }
    if (scenario.startsWith('detail-') && scenario !== 'detail-unknown') {
      await page.locator('.dept-card').nth(1).getByRole('button', { name: '详情', exact: true }).click()
      await page.locator('.el-drawer__title').waitFor()
    }
    await page.locator('.ai-bubble').click()
    const input = page.locator('.ai-panel input.el-input__inner')
    if (scenario.startsWith('detail-') || ['named-code', 'named', 'clarification', 'duplicate', 'duplicate-code', 'row', 'row-missing'].includes(scenario)) {
      // A deletion command must enter automatic execution without toggling the mode.
      const goal = scenario.startsWith('detail-') ? '删掉这个科室吧'
        : scenario === 'clarification' ? '删除一个科室'
        : scenario === 'duplicate-code' ? '删除康复医疗这个科室，编码KFYX'
        : scenario === 'row' ? '把编码是KFYX的科室删掉'
        : scenario === 'row-missing' ? '帮我把康复医疗删掉'
        : '删除康复医疗这个科室'
      await input.fill(goal)
      await input.press('Enter')
      if (scenario === 'detail-wrong') {
        const approval = page.locator('.ai-danger-confirm')
        await approval.waitFor()
        check(writes.length === 0, 'Reference authorized a different record')
        await approval.getByRole('button', { name: '取消', exact: true }).click()
        await page.waitForFunction(() => !document.querySelector('.ai-panel input.el-input__inner').disabled)
        check(writes.length === 0, 'Wrong record deleted after cancellation')
        results.push({ scenario, writes: 0, rejected: true })
        continue
      }
      if (scenario === 'detail-unknown') {
        await page.getByText(/请告诉我要操作哪一条/).waitFor()
        check(writes.length === 0, 'Unresolved reference deleted a record')
        results.push({ scenario, writes: 0, asked: true })
        continue
      }
      if (['clarification', 'duplicate'].includes(scenario)) {
        // The clarification must list the candidate records instead of a generic request.
        const asked = page.getByText(/都带「删除」（.*康复医疗.*），请告诉我要操作哪一条/)
        await asked.waitFor()
        check(writes.length === 0, 'Ambiguous target was deleted')
        if (scenario === 'duplicate') {
          check((await asked.innerText()).includes('2 条记录'), 'Candidate count missing')
          results.push({ scenario, writes: 0, asked: true })
          continue
        }
        await page.waitForFunction(() => !document.querySelector('.ai-panel input.el-input__inner').disabled)
        await input.fill('康复医疗，编码KFYX')
        await input.press('Enter')
      }
      await page.getByText('删除办理完成', { exact: true }).waitFor({ timeout: 15000 }).catch(async e => {
        const said = await page.locator('.ai-msg.assistant .ai-msg-bubble').allInnerTexts()
        throw new Error(`[${scenario}] no completion; assistant said: ${JSON.stringify(said)}; decisions=${JSON.stringify(requests.map(r => r.actions.at(-1)))}`)
      })
      check(await page.locator('.ai-danger-confirm').count() === 0, 'Explicit deletion required redundant approval')
      check(await page.getByText(/已确认删除「康复医疗（KFYX）」/).count() === 1, 'System confirm box was not auto-confirmed')
      if (scenario.startsWith('detail-')) {
        check(requests[0].hints.includes('当前详情记录=康复医疗（KFYX）'), 'Detail reference missing from model context')
        check(requests.at(-1).actions.some(action => action.ok === 'closed'), 'Detail was not closed before deletion')
        check(await page.locator('.ai-panel').isVisible(), 'Assistant closed instead of department details')
      }
      if (scenario === 'row-missing') {
        // A row the model invented is fed back to the model (with the real candidates), not resolved to the first button and not asked of the user.
        const last = requests[1].actions.at(-1)
        check(last.ok === 'missing-row' && last.target === '删除@骨科', 'Missing row not reported: ' + JSON.stringify(last))
        check(requests[1].pageContext.includes('「骨科」在当前页面不存在') && requests[1].pageContext.includes('康复医疗'), 'Missing row note lacks candidates')
      }
    } else if (scenario.startsWith('referent')) {
      // "刚创建的" is resolved from the previous structured task record without asking the user.
      const result = await page.evaluate(async () => {
        const { runAgent } = await import('/src/components/AiAssistant/agent.js')
        const router = document.querySelector('#app').__vue_app__.config.globalProperties.$router
        const messages = []
        const records = [{
          goal: '帮我新建一个科室', outcome: 'done', page: '/his/base/dept 科室管理',
          steps: [
            { type: 'click', target: '新增科室', value: '', ok: 'opened' },
            { type: 'input', target: '科室名称', value: '康复医疗', ok: 'true' },
            { type: 'input', target: '科室编码', value: 'KFYX', ok: 'true' },
            { type: 'click', target: '确 定', value: '', ok: 'submitted' }
          ]
        }]
        const outcome = await runAgent('删除刚创建的那个科室', { router, route: router.currentRoute.value, records, onSay: text => messages.push(text) })
        return { outcome: outcome.outcome, messages }
      })
      check(result.outcome === 'done', 'Referent deletion did not finish: ' + JSON.stringify(result))
      check(!result.messages.some(m => m.includes('请告诉我要操作哪一条') || m.includes('等待确认')), 'Referent deletion asked or required approval: ' + JSON.stringify(result.messages))
      check(String(requests[0].hints || '').includes('科室名称=康复医疗') && requests[0].hints.includes('科室编码=KFYX'), 'Referent hint missing: ' + requests[0].hints)
    } else {
      // A model-requested deletion outside the user's command still requires approval.
      await page.evaluate(async () => {
        const { runAgent } = await import('/src/components/AiAssistant/agent.js')
        const router = document.querySelector('#app').__vue_app__.config.globalProperties.$router
        window.deleteOutcome = null
        window.deleteMessages = []
        runAgent('查看康复医疗科室', { router, route: router.currentRoute.value, onSay: text => window.deleteMessages.push(text) })
          .then(result => { window.deleteOutcome = result })
      })
      const approval = page.locator('.ai-danger-confirm')
      await approval.waitFor()
      check(writes.length === 0, 'Deletion ran before approval')
      if (scenario === 'stopped') {
        await page.evaluate(async () => { (await import('/src/components/AiAssistant/agent.js')).agent.stop = true })
      } else {
        await approval.getByRole('button', { name: scenario === 'allowed' ? '允许执行' : '取消', exact: true }).click()
      }
      await page.waitForFunction(() => window.deleteOutcome !== null, null, { timeout: 15000 })
      const outcome = await page.evaluate(() => window.deleteOutcome.outcome)
      check(outcome === (scenario === 'allowed' ? 'done' : scenario), 'Wrong termination: ' + outcome)
      await approval.waitFor({ state: 'hidden' })
    }
    const submitted = scenario.startsWith('detail-') || scenario.startsWith('referent') || ['named-code', 'named', 'clarification', 'duplicate-code', 'row', 'row-missing', 'allowed'].includes(scenario)
    check(writes.length === (submitted ? 1 : 0), 'Unexpected deletion count: ' + writes.length)
    if (submitted) {
      check(writes[0].endsWith('/his/dept/22'), 'Wrong row deleted: ' + writes[0])
      check(requests.at(-1).actions.some(action => action.ok === 'submitted'), 'Deletion response was not verified')
      check(await page.locator('.dept-card .code').allTextContents().then(codes => codes.join() === 'NK'), 'List not refreshed')
      // One decision to click, one to report done (plus one clarification/retry round where applicable).
      check(requests.length === actions.length, 'Deletion took extra decisions: ' + requests.length)
    }
    results.push({ scenario, writes: writes.length, decisions: requests.length })
  }
  await page.context().clearCookies()
  return results
}
