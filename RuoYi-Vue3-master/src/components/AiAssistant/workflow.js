import { Annotation, StateGraph, START, END } from '@langchain/langgraph/web'

const State = Annotation.Root({
  iteration: Annotation(),
  observation: Annotation(),
  action: Annotation(),
  result: Annotation(),
  halt: Annotation()
})

// Keep business writes in one node with no automatic retries. Only the caller's
// read-only model request may retry; verification decides whether to continue.
export function createAgentWorkflow({ observe, decide, execute, verify, shouldStop, maxSteps, onPhase }) {
  const node = (phase, run) => async state => {
    if (phase !== 'verify' && shouldStop()) return { halt: true }
    onPhase?.(phase)
    return run(state)
  }
  const next = phase => state => state.halt || shouldStop() ? END : phase
  return new StateGraph(State)
    .addNode('observe', node('observe', async state => ({
      observation: await observe(state.iteration), iteration: state.iteration + 1
    })))
    .addNode('decide', node('decide', state => decide(state.observation, state.iteration)))
    .addNode('execute', node('execute', async state => ({ result: await execute(state.action) })))
    .addNode('verify', node('verify', state => verify(state.action, state.result)))
    .addEdge(START, 'observe')
    .addConditionalEdges('observe', next('decide'), ['decide', END])
    .addConditionalEdges('decide', next('execute'), ['execute', END])
    .addConditionalEdges('execute', state => state.halt ? END : 'verify', ['verify', END])
    .addConditionalEdges('verify', state => state.iteration >= maxSteps ? END : next('observe')(state), ['observe', END])
    .compile()
}
