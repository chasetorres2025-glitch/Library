<template>
  <div class="page-container my-borrow">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的借阅</h1>
        <p class="page-description">查看您的图书借阅记录</p>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select 
            v-model="queryForm.status" 
            placeholder="请选择状态" 
            clearable 
            class="search-select"
          >
            <el-option label="未还" :value="0" />
            <el-option label="已还" :value="1" />
            <el-option label="逾期" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item class="search-actions">
          <el-button type="primary" @click="handleQuery" class="query-button">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset" class="reset-button">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-container">
        <el-table 
          :data="tableData" 
          border 
          class="data-table"
          v-loading="loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="bookId" label="图书ID" width="100" align="center" />
          <el-table-column prop="bookName" label="图书名称" min-width="200">
            <template #default="{ row }">
              <div class="book-name-cell">
                <span class="book-name">{{ row.bookName || '加载中...' }}</span>
                <el-tag v-if="row.status === 2" type="danger" size="small" class="overdue-tag">逾期</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="borrowTime" label="借阅时间" width="180" />
          <el-table-column prop="dueTime" label="应还时间" width="180">
            <template #default="{ row }">
              <span :class="{ 'overdue-text': isOverdue(row.dueTime, row.status) }">
                {{ row.dueTime }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="returnTime" label="归还时间" width="180" />
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag 
                :type="getStatusType(row.status)" 
                class="status-tag"
              >
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  v-if="row.status === 0" 
                  type="primary" 
                  size="small" 
                  @click="handleReturn(row.id)" 
                  class="action-button"
                >
                  <el-icon><Check /></el-icon>
                  归还
                </el-button>
                <el-button 
                  v-else 
                  type="success" 
                  size="small" 
                  @click="handleBorrowAgain(row.bookId)" 
                  class="action-button"
                >
                  <el-icon><Plus /></el-icon>
                  再借
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryForm.current"
          v-model:page-size="queryForm.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleQuery"
          @current-change="handleQuery"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBorrows, borrowBook, returnBook } from '../api/borrow'
import { useUserStore } from '../store/user'
import { Search, Refresh, Check, Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()

const loading = ref(false)
const formRef = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  userId: userStore.userInfo.userId,
  status: null
})

const tableData = ref([])
const total = ref(0)

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '未还', 1: '已还', 2: '逾期' }
  return map[status] || '未知'
}

const isOverdue = (dueTime, status) => {
  if (status !== 0) return false
  return new Date(dueTime) < new Date()
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getMyBorrows(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.status = null
  handleQuery()
}

const handleReturn = async (id) => {
  try {
    await ElMessageBox.confirm('确定要归还该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await returnBook(id, userStore.userInfo.userId)
    ElMessage.success('归还成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('归还失败:', error)
    }
  }
}

const handleBorrowAgain = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要再次借阅该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await borrowBook({
      userId: userStore.userInfo.userId,
      bookId: bookId,
      operatorId: userStore.userInfo.userId
    })
    
    ElMessage.success('借阅成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('借阅失败:', error)
    }
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.book-name-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-small);
}

.book-name {
  flex: 1;
}

.overdue-tag {
  font-weight: var(--font-weight-primary);
}

.overdue-text {
  color: var(--danger-color);
  font-weight: var(--font-weight-primary);
}
</style>