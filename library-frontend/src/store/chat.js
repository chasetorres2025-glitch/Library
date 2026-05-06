import { defineStore } from 'pinia'
import { createChatSession, sendChatMessage, getChatHistory } from '../api/aiChat'

export const useChatStore = defineStore('chat', {
  state: () => ({
    sessionId: localStorage.getItem('chatSessionId') || '',
    messages: [],
    loading: false
  }),

  getters: {
    hasSession: (state) => !!state.sessionId
  },

  actions: {
    async createSession() {
      const res = await createChatSession()
      this.sessionId = res.data.sessionId
      localStorage.setItem('chatSessionId', this.sessionId)
      this.messages = [{
        role: 'assistant',
        content: res.data.welcomeMessage,
        recommendations: [],
        suggestions: []
      }]
      return res.data
    },

    async sendMessage(message) {
      if (!this.sessionId) {
        await this.createSession()
      }
      this.messages.push({
        role: 'user',
        content: message
      })
      this.loading = true
      try {
        const res = await sendChatMessage({
          sessionId: this.sessionId,
          message
        })
        this.messages.push({
          role: 'assistant',
          content: res.data.content,
          intentType: res.data.intentType,
          intentConfidence: res.data.intentConfidence,
          recommendations: res.data.recommendations || [],
          suggestions: res.data.suggestions || []
        })
        return res.data
      } finally {
        this.loading = false
      }
    },

    async loadHistory() {
      if (!this.sessionId) return
      const res = await getChatHistory(this.sessionId)
      this.messages = res.data.map(item => ({
        role: item.role,
        content: item.content,
        intentType: item.intentType,
        recommendations: item.recommendResult ? JSON.parse(item.recommendResult) : []
      }))
    },

    clearSession() {
      this.sessionId = ''
      this.messages = []
      localStorage.removeItem('chatSessionId')
    }
  }
})
