<template>
  <div class="tag-manage-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><CollectionTag /></el-icon>
        图书标签管理
      </h2>
    </div>

    <el-card class="tag-card">
      <template #header>
        <div class="card-header">
          <span>标签列表</span>
          <el-button type="primary" @click="showAddDialog = true">新增标签</el-button>
        </div>
      </template>

      <el-table :data="tagList" v-loading="loading" style="width: 100%">
        <el-table-column prop="tagName" label="标签名称" width="150" />
        <el-table-column prop="tagType" label="标签类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTagTypeColor(row.tagType)">{{ getTagTypeText(row.tagType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="usageCount" label="使用次数" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editTag(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteTag(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="tag-card">
      <template #header>
        <div class="card-header">
          <span>为图书打标签</span>
        </div>
      </template>
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="选择图书">
          <el-select v-model="assignForm.bookId" placeholder="请选择图书" style="width: 300px">
            <el-option
              v-for="book in bookList"
              :key="book.id"
              :label="book.bookName"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择标签">
          <el-select v-model="assignForm.tagId" placeholder="请选择标签" style="width: 300px">
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="`${tag.tagName} (${getTagTypeText(tag.tagType)})`"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权重">
          <el-slider v-model="assignForm.weight" :max="1" :step="0.1" show-input style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleAssign">打标签</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showAddDialog" :title="isEdit ? '编辑标签' : '新增标签'" width="500px">
      <el-form :model="tagForm" label-width="100px">
        <el-form-item label="标签名称">
          <el-input v-model="tagForm.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签类型">
          <el-select v-model="tagForm.tagType" placeholder="请选择标签类型" style="width: 100%">
            <el-option label="主题" value="subject" />
            <el-option label="难度" value="difficulty" />
            <el-option label="场景" value="scene" />
            <el-option label="情感" value="emotion" />
            <el-option label="风格" value="style" />
            <el-option label="目标读者" value="target" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="tagForm.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTag">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CollectionTag } from '@element-plus/icons-vue'
import { getBookTagList, addBookTag, updateBookTag, deleteBookTag, assignTagToBook } from '../api/bookTag'
import { getBookList } from '../api/book'

const loading = ref(false)
const tagList = ref([])
const bookList = ref([])
const showAddDialog = ref(false)
const isEdit = ref(false)
const currentTagId = ref(null)

const tagForm = ref({
  tagName: '',
  tagType: 'subject',
  description: ''
})

const assignForm = ref({
  bookId: null,
  tagId: null,
  weight: 1.0
})

const tagTypeMap = {
  subject: '主题',
  difficulty: '难度',
  scene: '场景',
  emotion: '情感',
  style: '风格',
  target: '目标读者'
}

const tagTypeColorMap = {
  subject: 'primary',
  difficulty: 'success',
  scene: 'warning',
  emotion: 'danger',
  style: 'info',
  target: ''
}

const getTagTypeText = (type) => tagTypeMap[type] || type
const getTagTypeColor = (type) => tagTypeColorMap[type] || ''

const loadTags = async () => {
  loading.value = true
  try {
    const res = await getBookTagList()
    tagList.value = res.data || []
  } finally {
    loading.value = false
  }
}

const loadBooks = async () => {
  try {
    const res = await getBookList({ current: 1, size: 1000 })
    bookList.value = res.data.records || []
  } catch (e) {
    console.error('加载图书失败', e)
  }
}

const editTag = (row) => {
  isEdit.value = true
  currentTagId.value = row.id
  tagForm.value = {
    tagName: row.tagName,
    tagType: row.tagType,
    description: row.description
  }
  showAddDialog.value = true
}

const saveTag = async () => {
  try {
    if (isEdit.value) {
      await updateBookTag(currentTagId.value, tagForm.value)
      ElMessage.success('更新成功')
    } else {
      await addBookTag(tagForm.value)
      ElMessage.success('添加成功')
    }
    showAddDialog.value = false
    resetTagForm()
    await loadTags()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteTag = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该标签吗？', '提示', { type: 'warning' })
    await deleteBookTag(row.id)
    ElMessage.success('删除成功')
    await loadTags()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleAssign = async () => {
  if (!assignForm.value.bookId || !assignForm.value.tagId) {
    ElMessage.warning('请选择图书和标签')
    return
  }
  try {
    await assignTagToBook({
      bookId: assignForm.value.bookId,
      tagId: assignForm.value.tagId,
      weight: assignForm.value.weight
    })
    ElMessage.success('打标签成功')
    assignForm.value = { bookId: null, tagId: null, weight: 1.0 }
    await loadTags()
  } catch (e) {
    ElMessage.error('打标签失败')
  }
}

const resetTagForm = () => {
  isEdit.value = false
  currentTagId.value = null
  tagForm.value = { tagName: '', tagType: 'subject', description: '' }
}

onMounted(() => {
  loadTags()
  loadBooks()
})
</script>

<style scoped>
.tag-manage-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.tag-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
