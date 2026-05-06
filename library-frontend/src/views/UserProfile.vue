<template>
  <div class="profile-page">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><UserFilled /></el-icon>
        我的阅读画像
      </h2>
    </div>

    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>阅读统计</span>
        </div>
      </template>
      <ProfileStats :stats="statistics" />
    </el-card>

    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>阅读偏好</span>
        </div>
      </template>
      <div v-if="preferences.length > 0" class="preferences">
        <div v-for="(pref, index) in preferences" :key="index" class="preference-item">
          <span class="pref-label">{{ pref.name }}</span>
          <el-progress :percentage="pref.percentage" :color="colors[index % colors.length]" />
        </div>
      </div>
      <el-empty v-else description="暂无阅读偏好数据" />
    </el-card>

    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>技能标签</span>
          <el-button type="primary" size="small" @click="showTagDialog = true">编辑标签</el-button>
        </div>
      </template>
      <div class="skill-tags">
        <el-tag
          v-for="tag in skillTags"
          :key="tag"
          class="skill-tag"
          effect="dark"
          type="success"
        >
          {{ tag }}
        </el-tag>
        <el-tag v-if="skillTags.length === 0" type="info">暂无技能标签</el-tag>
      </div>
    </el-card>

    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>阅读水平与建议</span>
        </div>
      </template>
      <div class="level-section">
        <div class="level-item">
          <span class="level-label">当前水平：</span>
          <el-tag :type="levelType" size="large">{{ levelText }}</el-tag>
        </div>
        <div class="suggestion-item">
          <span class="suggestion-label">推荐建议：</span>
          <span class="suggestion-text">{{ statistics.suggestion || '暂无建议' }}</span>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="showTagDialog" title="编辑技能标签" width="500px">
      <el-select
        v-model="editTags"
        multiple
        filterable
        allow-create
        default-first-option
        placeholder="请选择或输入技能标签"
        style="width: 100%"
      >
        <el-option
          v-for="tag in defaultTags"
          :key="tag"
          :label="tag"
          :value="tag"
        />
      </el-select>
      <template #footer>
        <el-button @click="showTagDialog = false">取消</el-button>
        <el-button type="primary" @click="saveTags">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProfileStatistics, updateUserProfile } from '../api/profile'
import ProfileStats from '../components/ProfileStats.vue'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const statistics = ref({})
const showTagDialog = ref(false)
const editTags = ref([])

const defaultTags = ['Python', 'Java', '数据分析', '项目管理', 'Web开发', '机器学习', '管理学', '文学']

const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#8e44ad']

const preferences = computed(() => {
  try {
    const cats = statistics.value.preferredCategories
    if (!cats) return []
    const parsed = JSON.parse(cats)
    if (Array.isArray(parsed)) {
      return parsed.map((name, idx) => ({
        name,
        percentage: 100 - idx * 20
      }))
    }
    return []
  } catch (e) {
    return []
  }
})

const skillTags = computed(() => {
  try {
    const tags = statistics.value.skillTags
    if (!tags) return []
    const parsed = JSON.parse(tags)
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    return []
  }
})

const levelText = computed(() => {
  const level = statistics.value.readingLevel
  if (level === 'advanced') return '高级'
  if (level === 'intermediate') return '进阶'
  return '入门'
})

const levelType = computed(() => {
  const level = statistics.value.readingLevel
  if (level === 'advanced') return 'danger'
  if (level === 'intermediate') return 'warning'
  return 'success'
})

const loadProfile = async () => {
  loading.value = true
  try {
    const res = await getProfileStatistics()
    statistics.value = res.data
  } finally {
    loading.value = false
  }
}

const saveTags = async () => {
  try {
    await updateUserProfile({
      skillTags: JSON.stringify(editTags.value)
    })
    ElMessage.success('保存成功')
    showTagDialog.value = false
    await loadProfile()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadProfile()
  editTags.value = [...skillTags.value]
})
</script>

<style scoped>
.profile-page {
  max-width: 900px;
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

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.preferences {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preference-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pref-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 14px;
  color: #606266;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  font-size: 14px;
  padding: 6px 14px;
}

.level-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.level-item,
.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.level-label,
.suggestion-label {
  font-weight: bold;
  color: #606266;
  width: 90px;
  flex-shrink: 0;
}

.suggestion-text {
  color: #303133;
}
</style>
