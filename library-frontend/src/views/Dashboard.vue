<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">数据概览</h1>
      <p class="dashboard-subtitle">实时监控图书馆运营状态</p>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="(stat, index) in statsData" :key="index">
        <div class="stat-card" :class="`stat-card-${stat.type}`" @click="handleStatClick(stat)">
          <div class="stat-content">
            <div class="stat-icon-container">
              <el-icon class="stat-icon" :size="32">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
          <div class="stat-trend" v-if="stat.trend">
            <el-icon class="trend-icon" :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
              <ArrowUp v-if="stat.trend > 0" />
              <ArrowDown v-else />
            </el-icon>
            <span class="trend-value">{{ Math.abs(stat.trend) }}%</span>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <h3>借阅趋势</h3>
              <div class="header-actions">
                <el-radio-group v-model="chartPeriod" size="small">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                  <el-radio-button label="year">本年</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder" v-if="loading">
              <el-icon size="64" color="#c0c4cc"><TrendCharts /></el-icon>
              <p>图表数据加载中...</p>
            </div>
            <div class="chart-content" v-else>
              <div class="trend-item" v-for="(count, date) in borrowTrend" :key="date">
                <span class="trend-date">{{ formatDate(date) }}</span>
                <span class="trend-count">{{ count }} 本</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <h3>欢迎回来</h3>
            </div>
          </template>
          <div class="welcome-content">
            <div class="user-avatar-container">
              <el-avatar :size="64" class="welcome-avatar">
                <el-icon size="32"><User /></el-icon>
              </el-avatar>
            </div>
            <h4 class="welcome-name">{{ userStore.userInfo.realName }}</h4>
            <el-tag 
              :type="userStore.userInfo.roleCode === 'ADMIN' ? 'danger' : 'primary'" 
              size="large"
              class="welcome-role"
            >
              {{ userStore.userInfo.roleCode === 'ADMIN' ? '管理员' : '读者' }}
            </el-tag>
            <div class="welcome-info">
              <p><el-icon><Clock /></el-icon> 上次登录: {{ lastLoginTime }}</p>
              <p><el-icon><Calendar /></el-icon> 注册时间: {{ registerTime }}</p>
            </div>
            <div class="welcome-actions">
              <el-button type="primary" class="action-btn" @click="router.push('/borrow')">
                <el-icon><Document /></el-icon>
                查看借阅记录
              </el-button>
              <el-button type="success" class="action-btn" @click="router.push('/book')">
                <el-icon><Reading /></el-icon>
                浏览图书
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="8">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <h3>快捷操作</h3>
            </div>
          </template>
          <div class="quick-actions">
            <div 
              v-for="action in quickActions" 
              :key="action.name"
              class="quick-action-item"
              @click="handleQuickAction(action)"
            >
              <div class="action-icon-container">
                <el-icon class="action-icon">
                  <component :is="action.icon" />
                </el-icon>
              </div>
              <span class="action-name">{{ action.name }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="recent-books-card">
          <template #header>
            <div class="card-header">
              <h3>最新图书</h3>
              <el-button type="text" size="small" @click="router.push('/book')">查看更多</el-button>
            </div>
          </template>
          <div class="book-list">
            <div v-for="book in recentBooks" :key="book.id" class="book-item">
              <div class="book-cover">
                <el-image :src="book.coverUrl" fit="cover">
                  <template #error>
                    <div class="book-cover-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="book-info">
                <h4 class="book-title">{{ book.bookName }}</h4>
                <p class="book-author">{{ book.author }}</p>
                <p class="book-category">{{ getCategoryName(book.categoryId) }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <el-card class="notices-card">
          <template #header>
            <div class="card-header">
              <h3>系统公告</h3>
              <el-button type="text" size="small">查看更多</el-button>
            </div>
          </template>
          <div class="notice-list">
            <div v-for="notice in notices" :key="notice.id" class="notice-item">
              <div class="notice-content">
                <h4 class="notice-title">{{ notice.title }}</h4>
                <p class="notice-desc">{{ notice.description }}</p>
                <p class="notice-time">{{ notice.time }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import request from '../utils/request'
import { 
  Reading, 
  User, 
  Document, 
  Warning, 
  ArrowUp, 
  ArrowDown,
  TrendCharts,
  Clock,
  Calendar,
  Plus,
  Search,
  Setting,
  Picture
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const chartPeriod = ref('week')
const lastLoginTime = ref('2023-12-01 14:30')
const registerTime = ref('2023-01-15 10:20')
const loading = ref(true)
const statistics = ref({})

const statsData = reactive([
  {
    type: 'primary',
    icon: 'Reading',
    label: '图书总数',
    value: '0',
    trend: 0,
    path: '/book'
  },
  {
    type: 'success',
    icon: 'User',
    label: '用户总数',
    value: '0',
    trend: 0,
    path: '/user'
  },
  {
    type: 'warning',
    icon: 'Document',
    label: '借阅总数',
    value: '0',
    trend: 0,
    path: '/borrow'
  },
  {
    type: 'danger',
    icon: 'Warning',
    label: '逾期未还',
    value: '0',
    trend: 0,
    path: '/borrow'
  }
])

const quickActions = [
  { name: '新增图书', icon: 'Plus', path: '/book' },
  { name: '借阅图书', icon: 'Document', path: '/borrow' },
  { name: '查询图书', icon: 'Search', path: '/book' },
  { name: '系统设置', icon: 'Setting', path: '/settings' }
]

const recentBooks = ref([])
const notices = ref([])
const borrowTrend = ref({})

const handleStatClick = (stat) => {
  if (stat.path) {
    router.push(stat.path)
  }
}

const handleQuickAction = (action) => {
  if (action.path) {
    router.push(action.path)
  }
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

const getCategoryName = (categoryId) => {
  // 这里可以根据categoryId获取分类名称，暂时返回ID
  return `分类${categoryId}`
}

const loadStatistics = async () => {
  try {
    const response = await request.get('/api/statistics/overview')
    const data = response.data
    statistics.value = data

    // 更新统计数据
    statsData[0].value = data.totalBooks?.toString() || '0'
    statsData[1].value = data.totalUsers?.toString() || '0'
    statsData[2].value = data.totalBorrows?.toString() || '0'
    statsData[3].value = data.overdueBorrows?.toString() || '0'

    // 更新借阅趋势
    borrowTrend.value = data.borrowTrend || {}

    // 更新最新图书
    recentBooks.value = data.recentBooks || []

    // 更新公告
    notices.value = [data.notices] || []

    loading.value = false
  } catch (error) {
    console.error('加载统计数据失败:', error)
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.dashboard-header {
  margin-bottom: var(--spacing-large);
}

.dashboard-title {
  font-size: var(--font-size-extra-large);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
  margin-bottom: var(--spacing-small);
}

.dashboard-subtitle {
  color: var(--text-secondary);
  font-size: var(--font-size-base);
  margin: 0;
}

.stats-row {
  margin-bottom: var(--spacing-large);
}

.stat-card {
  border-radius: var(--border-radius-large);
  padding: var(--spacing-large);
  background-color: var(--bg-color);
  box-shadow: var(--shadow-base);
  cursor: pointer;
  transition: var(--transition-base);
  position: relative;
  overflow: hidden;
  height: 120px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-light);
}

.stat-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-card-primary::before {
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
}

.stat-card-success::before {
  background: linear-gradient(90deg, var(--success-color), var(--success-light));
}

.stat-card-warning::before {
  background: linear-gradient(90deg, var(--warning-color), var(--warning-light));
}

.stat-card-danger::before {
  background: linear-gradient(90deg, var(--danger-color), var(--danger-light));
}

.stat-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-medium);
}

.stat-icon-container {
  width: 60px;
  height: 60px;
  border-radius: var(--border-radius-round);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card-primary .stat-icon-container {
  background-color: var(--primary-lighter);
  color: var(--primary-color);
}

.stat-card-success .stat-icon-container {
  background-color: var(--success-lighter);
  color: var(--success-color);
}

.stat-card-warning .stat-icon-container {
  background-color: var(--warning-lighter);
  color: var(--warning-color);
}

.stat-card-danger .stat-icon-container {
  background-color: var(--danger-lighter);
  color: var(--danger-color);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
  line-height: 1;
  margin-bottom: var(--spacing-small);
}

.stat-label {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
}

.stat-trend {
  position: absolute;
  top: var(--spacing-large);
  right: var(--spacing-large);
  display: flex;
  align-items: center;
  gap: var(--spacing-mini);
}

.trend-icon {
  font-size: 16px;
}

.trend-up {
  color: var(--success-color);
}

.trend-down {
  color: var(--danger-color);
}

.trend-value {
  font-size: var(--font-size-small);
  font-weight: var(--font-weight-primary);
}

.content-row {
  margin-bottom: var(--spacing-large);
}

.chart-card,
.welcome-card,
.quick-actions-card,
.recent-books-card,
.notices-card {
  border-radius: var(--border-radius-large);
  box-shadow: var(--shadow-base);
  height: 400px;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: var(--font-size-medium);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
}

.chart-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: var(--text-secondary);
}

.chart-content {
  width: 100%;
  padding: var(--spacing-medium);
}

.trend-item {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-small) 0;
  border-bottom: 1px solid var(--border-color-lighter);
}

.trend-item:last-child {
  border-bottom: none;
}

.trend-date {
  color: var(--text-secondary);
  font-size: var(--font-size-small);
}

.trend-count {
  color: var(--text-primary);
  font-weight: var(--font-weight-primary);
}

.welcome-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: var(--spacing-medium) 0;
}

.user-avatar-container {
  margin-bottom: var(--spacing-medium);
}

.welcome-avatar {
  background-color: var(--primary-color);
  color: #fff;
}

.welcome-name {
  margin: 0 0 var(--spacing-small) 0;
  font-size: var(--font-size-large);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
}

.welcome-role {
  margin-bottom: var(--spacing-large);
}

.welcome-info {
  width: 100%;
  margin-bottom: var(--spacing-large);
}

.welcome-info p {
  display: flex;
  align-items: center;
  gap: var(--spacing-small);
  margin: 0 0 var(--spacing-small) 0;
  font-size: var(--font-size-small);
  color: var(--text-secondary);
}

.welcome-actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-small);
}

.action-btn {
  width: 100%;
  justify-content: flex-start;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-medium);
  padding: var(--spacing-medium) 0;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-small);
  padding: var(--spacing-medium);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  transition: var(--transition-base);
}

.quick-action-item:hover {
  background-color: var(--fill-lighter);
}

.action-icon-container {
  width: 50px;
  height: 50px;
  border-radius: var(--border-radius-round);
  background-color: var(--primary-lighter);
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon {
  font-size: 24px;
}

.action-name {
  font-size: var(--font-size-small);
  color: var(--text-regular);
}

.book-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-medium);
  padding: var(--spacing-small) 0;
}

.book-item {
  display: flex;
  gap: var(--spacing-medium);
  padding: var(--spacing-small);
  border-radius: var(--border-radius-base);
  transition: var(--transition-base);
}

.book-item:hover {
  background-color: var(--fill-lighter);
}

.book-cover {
  width: 50px;
  height: 70px;
  border-radius: var(--border-radius-small);
  overflow: hidden;
  flex-shrink: 0;
}

.book-cover-error {
  width: 100%;
  height: 100%;
  background-color: var(--fill-lighter);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-placeholder);
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.book-title {
  margin: 0 0 var(--spacing-mini) 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
}

.book-author,
.book-category {
  margin: 0;
  font-size: var(--font-size-small);
  color: var(--text-secondary);
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-medium);
  padding: var(--spacing-small) 0;
}

.notice-item {
  padding: var(--spacing-small);
  border-radius: var(--border-radius-base);
  border-left: 3px solid var(--primary-color);
  background-color: var(--fill-lighter);
}

.notice-title {
  margin: 0 0 var(--spacing-mini) 0;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
}

.notice-desc {
  margin: 0 0 var(--spacing-mini) 0;
  font-size: var(--font-size-small);
  color: var(--text-regular);
}

.notice-time {
  margin: 0;
  font-size: var(--font-size-extra-small);
  color: var(--text-placeholder);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row .el-col {
    margin-bottom: var(--spacing-medium);
  }
  
  .content-row .el-col {
    margin-bottom: var(--spacing-medium);
  }
  
  .chart-card,
  .welcome-card,
  .quick-actions-card,
  .recent-books-card,
  .notices-card {
    height: auto;
    margin-bottom: var(--spacing-medium);
  }
}
</style>