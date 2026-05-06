<template>
  <div class="stats-container">
    <el-card class="stat-card" shadow="hover">
      <div class="stat-value">{{ stats.totalBorrowed || 0 }}</div>
      <div class="stat-label">总借阅</div>
    </el-card>
    <el-card class="stat-card" shadow="hover">
      <div class="stat-value">{{ stats.readingNow || 0 }}</div>
      <div class="stat-label">阅读中</div>
    </el-card>
    <el-card class="stat-card" shadow="hover">
      <div class="stat-value">{{ stats.completed || 0 }}</div>
      <div class="stat-label">已完成</div>
    </el-card>
    <el-card class="stat-card" shadow="hover">
      <div class="stat-value">{{ stats.favorited || 0 }}</div>
      <div class="stat-label">收藏书</div>
    </el-card>
  </div>
</template>

<script setup>
const props = defineProps({
  stats: {
    type: Object,
    default: () => ({})
  }
})
</script>

<style scoped>
.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  text-align: center;
  padding: 8px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

@media (max-width: 768px) {
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
