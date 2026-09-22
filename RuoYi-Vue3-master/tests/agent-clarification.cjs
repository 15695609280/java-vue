// playwright-cli run-code --filename tests/agent-clarification.cjs
// Uses the actual assistant and business pages; all API writes are intercepted.
async (page) => {
  const check = (ok, message) => { if (!ok) throw new Error(message) }
  const realModel = page.url().endsWith('#real-model')
  const results = []
  for (const scenario of ['purchase', 'admission']) {
    await page.unrouteAll({ behavior: 'wait' })
    await page.context().clearCookies()
    const purchase = scenario === 'purchase'
    const goal = purchase ? '给我新增一个采购单' : '给我新增一个住院登记'
    let question = purchase
      ? '请告知：1）供应商选哪家？2）采购哪些药品，各多少数量、采购单价？'
      : '请提供患者姓名、科室、病区、床位和主治医生。预缴押金保持3000元吗？'
    const reply = purchase
      ? '供应商选回归供应商，采购回归药品10盒，单价12.5元'
      : '患者回归患者，内科，内科一病区，01-01床，回归医生，押金5000'
    const actions = [
      { type: 'click', target: purchase ? '新建采购单' : '住院登记' },
      { type: 'ask', value: question },
      ...(purchase ? [
        { type: 'select', target: '供应商', value: '回归供应商' },
        { type: 'select', target: '药品@1', value: '回归药品' },
        { type: 'input', target: '数量@1', value: '10' },
        { type: 'input', target: '采购单价@1', value: '12.5' },
        { type: 'click', target: '创建采购单' }
      ] : [
        { type: 'select', target: '患者', value: '回归患者' },
        { type: 'select', target: '科室', value: '内科' },
        { type: 'select', target: '病区', value: '内科一病区' },
        { type: 'select', target: '床位', value: '01-01床' },
        { type: 'select', target: '主治医生', value: '回归医生' },
        { type: 'input', target: '预缴押金', value: '5000' },
        { type: 'click', target: '确认登记' }
      ]),
      { type: 'done' }
    ]
    const requests = []
    const responses = []
    const writes = []
    await page.route('**/dev-api/**', async route => {
      const req = route.request()
      const url = req.url().split('?')[0]
      let response = { code: 200, data: [], rows: [], total: 0, captchaEnabled: false }
      if (url.endsWith('/ai/agent')) {
        requests.push(req.postDataJSON())
        if (realModel) {
          check(requests.length <= 24, 'Unexpected real model loop')
          const upstream = await route.fetch({ url: 'http://127.0.0.1:8089/step', timeout: 90000 })
          response = await upstream.json()
          responses.push(response)
          return route.fulfill({ json: response })
        }
        check(requests.length <= actions.length, 'Unexpected agent loop')
        const action = actions[requests.length - 1]
        response = { code: 200, action, done: true, say: action.type === 'done' ? '回归创建完成' : '请补充所需信息' }
      } else if (req.method() === 'POST' && (url.endsWith('/his/purchase/create') || url.endsWith('/his/admission/admit'))) {
        writes.push(req.postDataJSON())
        await page.waitForTimeout(800)
        response = { code: 200, msg: '操作成功' }
      } else if (url.endsWith('/his/supplier/list')) {
        response.data = [{ supplierId: 71, supplierName: '回归供应商' }]
      } else if (url.endsWith('/his/drug/list')) {
        response.data = [{ drugId: 81, drugName: '回归药品', specification: '10片/盒', stock: 100, price: 12.5 }]
      } else if (url.endsWith('/his/patient/list')) {
        response.data = [{ patientId: 901, patientName: '回归患者', patientNo: 'TEST901' }]
      } else if (url.endsWith('/his/dept/list') || url.endsWith('/his/department/list')) {
        response.data = [{ deptId: 11, deptName: '内科' }]
      } else if (url.endsWith('/his/ward/list')) {
        response.data = [{ wardId: 21, wardName: '内科一病区', location: '1楼' }]
      } else if (url.includes('/his/bed/free/')) {
        response.data = [{ bedId: 31, bedNo: '01-01', pricePerDay: 100 }]
      } else if (url.endsWith('/his/doctor/list')) {
        response.data = [{ doctorId: 41, doctorName: '回归医生' }]
      }
      await route.fulfill({ json: response })
    })
    await page.goto('http://localhost:5174/login')
    await page.getByPlaceholder('账号', { exact: true }).waitFor()
    await page.evaluate(async scenario => {
      const app = document.querySelector('#app').__vue_app__
      const router = app.config.globalProperties.$router
      const { h } = await import('/node_modules/.vite/deps/vue.js')
      const { default: BusinessPage } = scenario === 'purchase'
        ? await import('/src/views/his/pharmacy/purchase/index.vue')
        : await import('/src/views/his/inpatient/admission/index.vue')
      app._context.directives.hasPermi = {}
      app.config.globalProperties.$pinia._s.get('user').roles = ['admin']
      document.cookie = 'Admin-Token=regression-only; path=/'
      const path = scenario === 'purchase' ? '/his/pharmacy/purchase' : '/his/inpatient/admission'
      router.addRoute({ path, meta: { title: scenario }, component: { render: () => h('main', { class: 'app-main' }, h(BusinessPage)) } })
      await router.push(path)
    }, scenario)
    await page.locator('.ai-bubble').click()
    const input = page.locator('.ai-panel input.el-input__inner')
    await input.fill(goal)
    await input.press('Enter')
    if (realModel) {
      await page.getByPlaceholder('补充所需信息，继续办理', { exact: true }).waitFor({ timeout: 180000 })
      question = await page.locator('.ai-msg.assistant .ai-msg-bubble').last().innerText()
      check(question.includes(purchase ? '供应商' : '患者'), 'Missing specific question: ' + question)
    } else {
      await page.getByText(question, { exact: true }).waitFor({ timeout: 15000 })
    }
    await page.waitForFunction(() => !document.querySelector('.ai-panel input.el-input__inner').disabled)
    check(writes.length === 0, 'Missing information caused an unwanted write')
    check(await input.getAttribute('placeholder') === '补充所需信息，继续办理', 'Missing waiting state')
    if (!realModel) check(requests[1].pageContext.includes('弹窗尚未填写的必填项'), 'Required fields not sent to model')
    const resumeIndex = requests.length
    await input.click()
    await page.waitForTimeout(100)
    check(await input.evaluate(el => document.activeElement === el), 'Dialog stole assistant input focus')
    await input.pressSequentially(reply)
    await input.press('Enter')
    if (realModel) {
      for (let n = 0; n < 720; n++) {
        if (requests.length > resumeIndex && ['done', 'ask', 'fail'].includes(responses.at(-1)?.action?.type)
          && !(await input.isDisabled())) break
        await page.waitForTimeout(250)
      }
      check(responses.at(-1)?.action?.type === 'done', 'Real model did not finish: ' + JSON.stringify(responses.at(-1)))
    } else {
      await page.getByText('回归创建完成', { exact: true }).waitFor({ timeout: 30000 })
    }
    check(writes.length === 1, 'Expected exactly one creation: ' + JSON.stringify(writes))
    check(requests[resumeIndex].goal.includes(question) && requests[resumeIndex].goal.includes(reply), 'Clarification context lost')
    if (!realModel) check(requests[resumeIndex].actions[0]?.ok === 'opened', 'Previous actions lost after clarification')
    check(requests.at(-1).actions.some(action => action.ok === 'submitted'), 'Creation was not verified')
    if (purchase) {
      check(writes[0].supplierId === 71, 'Wrong supplier')
      const items = writes[0].itemList
      check(items.length === 1 && items[0].drugId === 81 && items[0].quantity === 10 && items[0].price === 12.5, 'Wrong purchase details: ' + JSON.stringify(items))
    } else {
      const data = writes[0]
      check(data.patientId === 901 && data.deptId === 11 && data.wardId === 21 && data.bedId === 31 && data.doctorId === 41 && data.deposit === 5000, 'Wrong admission fields')
    }
    results.push({ scenario, realModel, questionShown: true, resumed: true, writes: writes.length, decisions: requests.length })
  }
  await page.context().clearCookies()
  return results
}
