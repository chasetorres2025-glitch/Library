<template>
  <div class="book-management">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">图书管理</h1>
        <p class="page-description">管理系统中的所有图书信息</p>
      </div>
      <div class="header-actions">
        <el-button type="success" @click="handleAdd" class="add-button">
          <el-icon><Plus /></el-icon>
          新增图书
        </el-button>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="图书名称">
          <el-input 
            v-model="queryForm.bookName" 
            placeholder="请输入图书名称" 
            clearable 
            prefix-icon="Search"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="作者">
          <el-input 
            v-model="queryForm.author" 
            placeholder="请输入作者" 
            clearable 
            prefix-icon="User"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input 
            v-model="queryForm.bookIsbn" 
            placeholder="请输入ISBN" 
            clearable 
            prefix-icon="Document"
            class="search-input"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select 
            v-model="queryForm.categoryId" 
            placeholder="请选择分类" 
            clearable 
            class="search-select"
          >
            <el-option 
              v-for="item in categories" 
              :key="item.id" 
              :label="item.categoryName" 
              :value="item.id" 
            />
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
          class="book-table"
          v-loading="loading"
          element-loading-text="加载中..."
          element-loading-background="rgba(255, 255, 255, 0.8)"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="bookIsbn" label="ISBN" width="150" />
          <el-table-column prop="bookName" label="图书名称" min-width="200">
            <template #default="{ row }">
              <div class="book-name-cell">
                <span class="book-name">{{ row.bookName }}</span>
                <el-tag v-if="row.isNew" type="danger" size="small" class="new-tag">新</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="作者" width="120" />
          <el-table-column prop="publisher" label="出版社" width="150" />
          <el-table-column prop="price" label="价格" width="100" align="right">
            <template #default="{ row }">
              <span class="price">¥{{ row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="categoryName" label="分类" width="120">
            <template #default="{ row }">
              <el-tag type="info" size="small">{{ row.categoryName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button type="primary" size="small" @click="handleEdit(row)" class="action-button">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(row.id)" class="action-button">
                  <el-icon><Delete /></el-icon>
                  删除
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
      :title="dialogTitle" 
      width="600px"
      class="book-dialog"
      :close-on-click-modal="false"
    >
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px"
        class="book-form"
      >
        <el-form-item label="ISBN" prop="bookIsbn">
          <el-input v-model="form.bookIsbn" :disabled="isEdit" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="form.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="form.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版时间" prop="publishTime">
          <el-date-picker 
            v-model="form.publishTime" 
            type="date" 
            placeholder="选择日期" 
            style="width: 100%" 
          />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option 
              v-for="item in categories" 
              :key="item.id" 
              :label="item.categoryName" 
              :value="item.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number 
            v-model="form.price" 
            :min="0" 
            :precision="2" 
            style="width: 100%" 
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入图书简介"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookList, addBook, updateBook, deleteBook } from '../api/book'
import { getAllCategories } from '../api/category'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const queryForm = reactive({
  current: 1,
  size: 10,
  bookName: '',
  author: '',
  bookIsbn: '',
  categoryId: null
})

const form = reactive({
  id: null,
  bookIsbn: '',
  bookName: '',
  author: '',
  publisher: '',
  publishTime: '',
  categoryId: null,
  price: 0,
  description: ''
})

const rules = {
  bookIsbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  bookName: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
  publishTime: [{ required: true, message: '请选择出版时间', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const tableData = ref([])
const total = ref(0)
const categories = ref([])

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getBookList(queryForm)
    tableData.value = res.data.records.map(item => ({
      ...item,
      isNew: Math.random() > 0.8 // 模拟部分图书为新书
    }))
    total.value = res.data.total
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.bookName = ''
  queryForm.author = ''
  queryForm.bookIsbn = ''
  queryForm.categoryId = null
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增图书'
  isEdit.value = false
  Object.assign(form, {
    id: null,
    bookIsbn: '',
    bookName: '',
    author: '',
    publisher: '',
    publishTime: '',
    categoryId: null,
    price: 0,
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑图书'
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true
    
    if (isEdit.value) {
      await updateBook(form)
      ElMessage.success('更新成功')
    } else {
      await addBook(form)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗？删除后不可恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteBook(id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(async () => {
  await handleQuery()
  const res = await getAllCategories()
  categories.value = res.data
})
</script>

<style scoped>
.book-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-large);
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: var(--font-size-extra-large);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
  margin: 0 0 var(--spacing-small) 0;
}

.page-description {
  color: var(--text-secondary);
  font-size: var(--font-size-base);
  margin: 0;
}

.header-actions {
  flex-shrink: 0;
}

.add-button {
  border-radius: var(--border-radius-base);
  font-weight: var(--font-weight-primary);
}

.search-card {
  margin-bottom: var(--spacing-large);
  border-radius: var(--border-radius-large);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-medium);
}

.search-input,
.search-select {
  width: 200px;
}

.search-actions {
  display: flex;
  gap: var(--spacing-small);
}

.query-button {
  background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
  border: none;
  font-weight: var(--font-weight-primary);
}

.reset-button {
  border-color: var(--border-base);
  color: var(--text-regular);
}

.table-card {
  border-radius: var(--border-radius-large);
  overflow: hidden;
}

.table-container {
  margin-bottom: var(--spacing-large);
}

.book-table {
  width: 100%;
  border-radius: var(--border-radius-base);
  overflow: hidden;
}

.book-name-cell {
  display: flex;
  align-items: center;
  gap: var(--spacing-small);
}

.book-name {
  flex: 1;
}

.new-tag {
  font-weight: var(--font-weight-primary);
}

.price {
  font-weight: var(--font-weight-primary);
  color: var(--success-color);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-small);
  justify-content: center;
}

.action-button {
  border-radius: var(--border-radius-small);
  font-weight: var(--font-weight-primary);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.book-dialog {
  border-radius: var(--border-radius-large);
  overflow: hidden;
}

.book-form {
  padding: var(--spacing-medium) 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-small);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: var(--spacing-medium);
  }
  
  .search-form {
    flex-direction: column;
  }
  
  .search-input,
  .search-select {
    width: 100%;
  }
  
  .search-actions {
    justify-content: center;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: var(--spacing-mini);
  }
}
</style>