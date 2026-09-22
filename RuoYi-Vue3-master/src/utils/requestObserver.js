const listeners = new Set()

// Listeners are short lived: the assistant subscribes only while executing a click.
export function observeRequests(listener) {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export function notifyRequest(event) {
  for (const listener of listeners) {
    try {
      listener(event)
    } catch (error) {
      console.error('Request observer failed', error)
    }
  }
}
