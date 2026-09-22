import request from '@/utils/request'

// 与 AI 助手对话（返回 answer / navigate / steps），大模型响应较慢单独放宽超时
export function chatWithAssistant(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    timeout: 90000,
    data
  })
}

// 自动办事 Agent：每轮返回一个待执行动作 { say, action:{type,target,value}, done }
export function agentStep(data) {
  return request({
    url: '/ai/agent',
    method: 'post',
    timeout: 90000,
    data
  })
}
