<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
      <div class="logo" :class="{ 'logo-collapsed': isCollapse }">
        <el-icon class="logo-icon" size="24"><Reading /></el-icon>
        <span v-if="!isCollapse" class="logo-text">图书管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        :collapse="isCollapse"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard" class="menu-item">
          <el-icon><HomeFilled /></el-icon>
          <template #title>
            <span>首页</span>
          </template>
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/user" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>
            <span>用户管理</span>
          </template>
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/category" class="menu-item">
          <el-icon><Menu /></el-icon>
          <template #title>
            <span>图书分类</span>
          </template>
        </el-menu-item>
        
        <el-menu-item v-if="userStore.isAdmin" index="/book" class="menu-item">
          <el-icon><Reading /></el-icon>
          <template #title>
            <span>图书管理</span>
          </template>
        </el-menu-item>
        
        <el-menu-item index="/borrow" class="menu-item">
          <el-icon><Document /></el-icon>
          <template #title>
            <span>借阅管理</span>
          </template>
        </el-menu-item>
        
        <el-menu-item index="/my-borrow" class="menu-item">
          <el-icon><Notebook /></el-icon>
          <template #title>
            <span>我的借阅</span>
          </template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-button 
            type="text" 
            @click="toggleCollapse" 
            class="collapse-btn"
            :icon="isCollapse ? Expand : Fold"
          />
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteName">{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown class="user-dropdown" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo.realName }}</span>
              <el-tag 
                :type="userStore.userInfo.roleCode === 'ADMIN' ? 'danger' : 'primary'" 
                size="small"
                class="role-tag"
              >
                {{ userStore.userInfo.roleCode === 'ADMIN' ? '管理员' : '读者' }}
              </el-tag>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessageBox } from 'element-plus'
import { 
  HomeFilled, 
  User, 
  Menu, 
  Reading, 
  Document, 
  Notebook,
  Fold,
  Expand,
  ArrowDown,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const currentRouteName = computed(() => {
  const routeMap = {
    '/user': '用户管理',
    '/category': '图书分类',
    '/book': '图书管理',
    '/borrow': '借阅管理',
    '/my-borrow': '我的借阅'
  }
  return routeMap[route.path] || ''
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background-color: var(--bg-color-page);
}

.sidebar {
  background-color: var(--bg-color);
  border-right: 1px solid var(--border-lighter);
  box-shadow: var(--shadow-base);
  transition: var(--transition-base);
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--spacing-medium);
  border-bottom: 1px solid var(--border-lighter);
  background: linear-gradient(135deg, var(--primary-colorer), var(--bg-color));
  transition: var(--transition-base);
}

.logo-collapsed {
  padding: 0;
}

.logo-icon {
  color: var(--primary-color);
  margin-right: var(--spacing-small);
  flex-shrink: 0;
}

.logo-text {
  font-size: var(--font-size-medium);
  font-weight: var(--font-weight-primary);
  color: var(--primary-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-menu {
  border-right: none;
  padding: var(--spacing-small) 0;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  margin: 0 var(--spacing-small) var(--spacing-mini);
  border-radius: var(--border-radius-base);
  transition: var(--transition-base);
}

.menu-item:hover {
  background-color: var(--primary-lighter) !important;
  color: var(--primary-color) !important;
}

.menu-item.is-active {
  background-color: var(--primary-color) !important;
  color: #fff !important;
}

.menu-item i {
  color: inherit !important;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-lighter);
  box-shadow: var(--shadow-base);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-large);
  height: 60px;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-medium);
}

.collapse-btn {
  font-size: 18px;
  color: var(--text-regular);
}

.breadcrumb {
  font-size: var(--font-size-base);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-small);
  padding: var(--spacing-small) var(--spacing-medium);
  border-radius: var(--border-radius-round);
  transition: var(--transition-base);
}

.user-info:hover {
  background-color: var(--fill-lighter);
}

.user-avatar {
  background-color: var(--primary-color);
  color: #fff;
}

.user-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-primary);
  color: var(--text-primary);
}

.role-tag {
  font-weight: var(--font-weight-primary);
}

.dropdown-icon {
  font-size: 12px;
  color: var(--text-secondary);
  transition: var(--transition-base);
}

.main-content {
  flex: 1;
  padding: var(--spacing-large);
  overflow-y: auto;
  background-color: var(--bg-color-page);
}

/* 页面过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
  }
  
  .sidebar.show {
    transform: translateX(0);
  }
  
  .main-container {
    margin-left: 0;
  }
  
  .breadcrumb {
    display: none;
  }
  
  .user-name {
    display: none;
  }
}
</style>