// Run using playwright-cli run-code --filename tests/agent-navigation.cjs.
async (page) => {
  await page.unrouteAll({ behavior: 'wait' })
  await page.route('**/dev-api/**', route => route.fulfill({ json: { code: 200, data: [], rows: [], total: 0, captchaEnabled: false } }))
  await page.context().clearCookies()
  await page.goto('http://localhost:5174/login')
  await page.waitForFunction(() => document.querySelector('#app')?.__vue_app__)
  await page.getByPlaceholder('账号', { exact: true }).waitFor()
  await page.evaluate(async () => {
    const app = document.querySelector('#app').__vue_app__
    const router = app.config.globalProperties.$router
    window.regressionRouter = router
    window.regressionStores = app.config.globalProperties.$pinia._s
    const { h } = await import('/node_modules/.vite/deps/vue.js')
    document.cookie = 'Admin-Token=regression-only; path=/'
    window.regressionStores.get('user').roles = ['admin']
    for (const name of ['first', 'second']) {
      router.addRoute({ path: '/regression-' + name, component: { render: () => h('main', { class: 'app-main' }, name) } })
    }
    await router.push('/regression-first')
  })
  await page.locator('.ai-bubble').click()
  await page.locator('.ai-panel input.el-input__inner').fill('navigation draft')
  await page.evaluate(async () => {
    window.assistantBeforeNavigation = document.querySelector('.ai-panel')
    let instance = document.querySelector('.ai-bubble').__vueParentComponent
    while (instance && !instance.setupState.agent) instance = instance.parent
    window.regressionAgent = instance.setupState.agent
    await window.regressionRouter.push('/regression-second')
  })
  const persisted = await page.evaluate(() => document.querySelectorAll('.ai-panel').length === 1
    && document.querySelector('.ai-panel') === window.assistantBeforeNavigation)
  if (!persisted || await page.locator('.ai-panel input.el-input__inner').inputValue() !== 'navigation draft') {
    throw new Error('Assistant was recreated during navigation')
  }
  await page.evaluate(async () => {
    window.regressionAgent.running = true
    window.regressionStores.get('lock').isLock = true
    await window.regressionRouter.push('/lock')
  })
  await page.waitForFunction(() => !document.querySelector('.ai-panel'))
  const stopped = await page.evaluate(() => window.regressionAgent.stop)
  if (!stopped) throw new Error('Locking the screen did not stop automation')
  await page.evaluate(async () => {
    window.regressionAgent.running = false
    window.regressionAgent.stop = false
    window.regressionStores.get('lock').isLock = false
    document.cookie = 'Admin-Token=; Max-Age=0; path=/'
    await window.regressionRouter.push('/login')
  })
  return { navigationPreservesAssistant: true, lockStopsAutomation: true }
}
