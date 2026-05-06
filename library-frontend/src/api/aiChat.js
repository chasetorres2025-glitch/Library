import request from '../utils/request'

export function createChatSession() {
  return request({
    url: '/api/ai/chat/session',
    method: 'post'
  })
}

export function sendChatMessage(data) {
  return request({
    url: '/api/ai/chat/message',
    method: 'post',
    data
  })
}

export function getChatHistory(sessionId) {
  return request({
    url: '/api/ai/chat/history',
    method: 'get',
    params: { sessionId }
  })
}
