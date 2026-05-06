<template>
  <div class="chat-panel">
    <div class="chat-messages" ref="messagesContainer">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message-row', msg.role === 'user' ? 'user-row' : 'assistant-row']"
      >
        <div :class="['message-bubble', msg.role === 'user' ? 'user-bubble' : 'assistant-bubble']">
          <div class="message-content">{{ msg.content }}</div>

          <div v-if="msg.recommendations && msg.recommendations.length > 0" class="recommendations">
            <div v-for="book in msg.recommendations" :key="book.bookId" class="book-card">
              <el-image
                v-if="book.coverUrl"
                :src="book.coverUrl"
                fit="cover"
                class="book-cover"
              />
              <div v-else class="book-cover-placeholder">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="book-info">
                <div class="book-name">《{{ book.bookName }}》</div>
                <div class="book-author">{{ book.author }}</div>
                <div class="book-reason">{{ book.reason }}</div>
                <div class="book-actions">
                  <el-button type="primary" size="small" @click="goToBookDetail(book.bookId)">查看详情</el-button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="msg.suggestions && msg.suggestions.length > 0" class="suggestions">
            <el-tag
              v-for="(suggestion, sIdx) in msg.suggestions"
              :key="sIdx"
              class="suggestion-tag"
              type="info"
              effect="plain"
              @click="sendQuickMessage(suggestion)"
            >
              {{ suggestion }}
            </el-tag>
          </div>
        </div>
      </div>

      <div v-if="loading" class="message-row assistant-row">
        <div class="message-bubble assistant-bubble">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>正在思考中...</span>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <div class="quick-actions">
        <el-button
          v-for="action in quickActions"
          :key="action.id"
          size="small"
          @click="sendQuickMessage(action.label)"
        >
          {{ action.icon }} {{ action.label }}
        </el-button>
      </div>
      <div class="input-row">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您的问题，例如：我想学Python入门..."
          @keyup.enter.prevent="handleSend"
          class="chat-textarea"
        />
        <el-button
          type="primary"
          :icon="Promotion"
          @click="handleSend"
          :loading="loading"
          class="send-btn"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '../store/chat'
import { Promotion, Reading, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const chatStore = useChatStore()

const props = defineProps({
  quickActions: {
    type: Array,
    default: () => [
      { id: 'skill', label: '技能学习', icon: '📚' },
      { id: 'problem', label: '问题求助', icon: '💡' },
      { id: 'literature', label: '文学推荐', icon: '📖' },
      { id: 'casual', label: '随便看看', icon: '🎲' }
    ]
  }
})

const inputMessage = ref('')
const messagesContainer = ref(null)

const messages = computed(() => chatStore.messages)
const loading = computed(() => chatStore.loading)

const handleSend = async () => {
  const text = inputMessage.value.trim()
  if (!text || loading.value) return
  inputMessage.value = ''
  await chatStore.sendMessage(text)
  scrollToBottom()
}

const sendQuickMessage = async (text) => {
  if (loading.value) return
  await chatStore.sendMessage(text)
  scrollToBottom()
}

const goToBookDetail = (bookId) => {
  router.push(`/book/detail/${bookId}`)
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

watch(() => chatStore.messages.length, scrollToBottom)
</script>

<style scoped>
.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-row {
  display: flex;
  width: 100%;
}

.user-row {
  justify-content: flex-end;
}

.assistant-row {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  word-break: break-word;
}

.user-bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.assistant-bubble {
  background: #f5f7fa;
  color: #303133;
  border-bottom-left-radius: 4px;
}

.recommendations {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.book-card {
  display: flex;
  gap: 12px;
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
  flex-shrink: 0;
}

.book-cover-placeholder {
  width: 60px;
  height: 80px;
  border-radius: 4px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.book-name {
  font-weight: bold;
  font-size: 14px;
}

.book-author {
  font-size: 12px;
  color: #606266;
}

.book-reason {
  font-size: 12px;
  color: #909399;
}

.book-actions {
  margin-top: auto;
}

.suggestions {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.suggestion-tag {
  cursor: pointer;
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
  border-radius: 0 0 8px 8px;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.input-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.chat-textarea {
  flex: 1;
}

.send-btn {
  height: 52px;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  margin-right: 8px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
