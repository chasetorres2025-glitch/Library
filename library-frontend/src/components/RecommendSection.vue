<template>
  <div class="recommend-section">
    <div class="section-header">
      <h3 class="section-title">{{ title }}</h3>
      <el-button type="text" size="small" @click="$emit('more')">查看更多</el-button>
    </div>
    <div class="books-row">
      <div
        v-for="book in books"
        :key="book.bookId"
        class="book-card"
        @click="goToDetail(book.bookId)"
      >
        <div class="book-cover-wrapper">
          <el-image
            v-if="book.coverUrl"
            :src="book.coverUrl"
            fit="cover"
            class="book-cover"
          />
          <div v-else class="book-cover-placeholder">
            <el-icon><Reading /></el-icon>
          </div>
        </div>
        <div class="book-name">{{ book.bookName }}</div>
        <div class="book-author">{{ book.author }}</div>
        <div v-if="book.reason" class="book-reason">{{ book.reason }}</div>
      </div>
      <el-empty v-if="!loading && books.length === 0" description="暂无推荐" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()

const props = defineProps({
  title: {
    type: String,
    default: '推荐'
  },
  books: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

defineEmits(['more'])

const goToDetail = (bookId) => {
  router.push(`/book/detail/${bookId}`)
}
</script>

<style scoped>
.recommend-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.books-row {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.book-card {
  flex: 0 0 160px;
  cursor: pointer;
  transition: transform 0.2s;
}

.book-card:hover {
  transform: translateY(-4px);
}

.book-cover-wrapper {
  width: 160px;
  height: 220px;
  border-radius: 8px;
  overflow: hidden;
  background: #f0f2f5;
  margin-bottom: 8px;
}

.book-cover {
  width: 100%;
  height: 100%;
}

.book-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #c0c4cc;
}

.book-name {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-author {
  font-size: 12px;
  color: #606266;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.book-reason {
  font-size: 12px;
  color: #409eff;
  margin-top: 4px;
}
</style>
