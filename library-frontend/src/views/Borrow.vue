<template>
  <div class="page-container borrow-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">借阅管理</h1>
        <p class="page-description">管理所有图书借阅记录</p>
      </div>
      <div class="header-actions">
        <el-button type="success" @click="handleBorrow" class="success-button">
          <el-icon><Plus /></el-icon>
          借阅图书
        </el-button>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input 
            v-model="queryForm.userId" 
            placeholder="请输入用户ID" 
            clearable 
            prefix-icon="User"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="图书ID">
          <el-input 
            v-model="queryForm.bookId" 
            placeholder="请输入图书ID" 
            clearable 
            prefix-icon="Reading"
            class="search-input"
          />
        </el-form-item>
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
          <el-table-column prop="userId" label="用户ID" width="100" align="center" />
          <el-table-column prop="bookId" label="图书ID" width="100" align="center" />
          <el-table-column prop="borrowTime" label="借阅时间" width="180" />
          <el-table-column prop="dueTime" label="应还时间" width="180" />
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

    <el-dialog 
      v-model="dialogVisible" 
      title="借阅图书" 
      width="500px"
      class="form-dialog"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="80px"
        class="data-form"
      >
        <el-form-item label="用户" prop="userId">
          <el-select 
            v-model="form.userId" 
            placeholder="请选择用户" 
            filterable
            remote
            :remote-method="fetchUserList"
            :loading="userLoading"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="user in userList" 
              :key="user.id" 
              :label="`${user.username} (${user.id})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图书" prop="bookId">
          <el-select 
            v-model="form.bookId" 
            placeholder="请选择图书" 
            filterable
            remote
            :remote-method="fetchBookList"
            :loading="bookLoading"
            clearable
            style="width: 100%"
          >
            <el-option 
              v-for="book in bookList" 
              :key="book.id" 
              :label="`${book.bookName} (${book.id})`"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBorrowList, borrowBook, returnBook } from '../api/borrow'
import { getUserList } from '../api/user'
import { getBookList } from '../api/book'
import { useUserStore } from '../store/user'
import { Plus, Search, Refresh, Check } from '@element-plus/icons-vue'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const formRef = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  userId: '',
  bookId: '',
  status: null
})

const form = reactive({
  userId: '',
  bookId: ''
})

const rules = {
  userId: [{ required: true, message: '请选择用户', trigger: 'change' }],
  bookId: [{ required: true, message: '请选择图书', trigger: 'change' }]
}

const tableData = ref([])
const total = ref(0)

const userList = ref([])
const bookList = ref([])
const userLoading = ref(false)
const bookLoading = ref(false)

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '未还', 1: '已还', 2: '逾期' }
  return map[status] || '未知'
}

const fetchUserList = async (query) => {
  userLoading.value = true
  try {
    const res = await getUserList({ current: 1, size: 100, keyword: query })
    userList.value = res.data.records || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    userLoading.value = false
  }
}

const fetchBookList = async (query) => {
  bookLoading.value = true
  try {
    const res = await getBookList({ current: 1, size: 100, keyword: query })
    bookList.value = res.data.records || []
  } catch (error) {
    console.error('获取图书列表失败:', error)
  } finally {
    bookLoading.value = false
  }
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getBorrowList(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.userId = ''
  queryForm.bookId = ''
  queryForm.status = null
  handleQuery()
}

const handleBorrow = async () => {
  Object.assign(form, {
    userId: '',
    bookId: ''
  })
  await fetchUserList('')
  await fetchBookList('')
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    await borrowBook({
      userId: parseInt(form.userId),
      bookId: parseInt(form.bookId),
      operatorId: userStore.userInfo.userId
    })
    
    ElMessage.success('借阅成功')
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    console.error('借阅失败:', error)
  } finally {
    submitting.value = false
  }
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

onMounted(() => {
  handleQuery()
})
</script>