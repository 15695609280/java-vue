// Keep task evidence separate so progress updates and casual chat cannot evict it.
export function conversationPayload(messages) {
  const valid = messages.filter(m => !m.error && ['user', 'assistant'].includes(m.role))
  let budget = 24000
  const history = []
  for (const m of [...valid].reverse()) {
    if ((m.progress && !m.taskRecord) || !m.content?.trim()) continue
    const content = m.content.slice(0, Math.min(4000, budget))
    history.unshift({ role: m.role, content })
    budget -= content.length
    if (history.length >= 40 || budget <= 0) break
  }
  const taskRecords = valid.filter(m => m.taskRecord && m.taskOutcome !== 'asked' && m.apiContent)
    .slice(-6).map(m => m.apiContent.slice(0, 2000))
  return { history, taskRecords }
}
