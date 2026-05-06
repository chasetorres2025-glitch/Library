<template>
  <div class="ai-chat-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><ChatDotRound /></el-icon>
        智能书童
      </h2>
      <p class="page-subtitle">您的私人阅读顾问，随时为您解答阅读相关问题</p>
    </div>
    <AiChatPanel class="chat-container" />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useChatStore } from '../store/chat'
import AiChatPanel from '../components/AiChatPanel.vue'
import { ChatDotRound } from '@element-plus/icons-vue'

const chatStore = useChatStore()

onMounted(() => {
  if (!chatStore.hasSession) {
    chatStore.createSession()
  } else {
    chatStore.loadHistory()
  }
})
</script>

<style scoped>
.ai-chat-page {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.chat-container {
  flex: 1;
  min-height: 0;
}
</style>
