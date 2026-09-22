import assert from 'node:assert/strict'
import { test } from 'node:test'
import { createAgentWorkflow } from '../src/components/AiAssistant/workflow.js'

test('observe fresh state after each verified action, stop at the step bound', async () => {
  let writes = 0
  const observations = []
  const graph = createAgentWorkflow({
    maxSteps: 3, shouldStop: () => false,
    observe: () => { observations.push(writes); return writes },
    decide: () => ({ action: { type: 'click' } }),
    execute: () => ++writes,
    verify: (action, result) => { assert.equal(result, writes); return { halt: false } }
  })
  await graph.invoke({ iteration: 0, halt: false })
  assert.equal(writes, 3)
  assert.deepEqual(observations, [0, 1, 2])
})

for (const stopAt of ['decide', 'execute']) {
  test(`stop during ${stopAt} prevents further actions and preserves executed results`, async () => {
    let stopped = false
    let writes = 0
    let verified = 0
    const graph = createAgentWorkflow({
      maxSteps: 5, shouldStop: () => stopped, observe: () => 'page',
      decide: () => { stopped = stopAt === 'decide'; return { action: { type: 'click' } } },
      execute: () => { writes++; stopped = true; return 'submitted' },
      verify: (action, result) => { verified++; assert.equal(result, 'submitted'); return { halt: true } }
    })
    await graph.invoke({ iteration: 0, halt: false })
    assert.equal(writes, stopAt === 'execute' ? 1 : 0)
    assert.equal(verified, writes)
  })
}

test('clarification halts without executing an action', async () => {
  const graph = createAgentWorkflow({
    maxSteps: 5, shouldStop: () => false, observe: () => 'page',
    decide: () => ({ halt: true }),
    execute: () => assert.fail('clarification executed a write'),
    verify: () => assert.fail('no action to verify')
  })
  await graph.invoke({ iteration: 0, halt: false })
})

test('failed business action is never retried by the graph', async () => {
  let attempts = 0
  const graph = createAgentWorkflow({
    maxSteps: 5, shouldStop: () => false, observe: () => 'page',
    decide: () => ({ action: { type: 'click' } }),
    execute: () => { attempts++; throw new Error('unknown write result') },
    verify: () => ({ halt: false })
  })
  await assert.rejects(graph.invoke({ iteration: 0, halt: false }), /unknown write result/)
  assert.equal(attempts, 1)
})
