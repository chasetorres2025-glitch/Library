<template>
  <div class="login-container">
    <div class="login-card fade-in-up">
      <div class="login-header">
        <div class="logo-container">
          <el-icon class="logo-icon" size="48"><Reading /></el-icon>
        </div>
        <h2>图书管理系统</h2>
        <p class="login-subtitle">欢迎登录，开启您的阅读之旅</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名" 
              prefix-icon="User" 
              size="large"
              class="custom-input"
            />
          </div>
        </el-form-item>
        
        <el-form-item prop="password">
          <div class="input-wrapper">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码" 
              prefix-icon="Lock" 
              size="large"
              class="custom-input"
              @keyup.enter="handleLogin" 
              show-password
            />
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            class="login-button"
            size="large"
            :loading="loading"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <div class="tips">
          <p>默认管理员账号：admin / 123456</p>
        </div>
        <div class="login-links">
          <a href="#" class="forgot-password">忘记密码？</a>
          <a href="#" class="help">登录帮助</a>
        </div>
      </div>
    </div>
    
    <div class="decoration-circle decoration-circle-1"></div>
    <div class="decoration-circle decoration-circle-2"></div>
    <div class="decoration-circle decoration-circle-3"></div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  padding: var(--spacing-large);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%23ffffff" fill-opacity="0.1" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,133.3C960,128,1056,96,1152,90.7C1248,85,1344,107,1392,117.3L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
  background-size: cover;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  z-index: 0;
}

.decoration-circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation: float 6s ease-in-out infinite;
}

.decoration-circle-2 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  right: 10%;
  animation: float 8s ease-in-out infinite;
}

.decoration-circle-3 {
  width: 100px;
  height: 100px;
  top: 20%;
  right: 20%;
  animation: float 7s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.login-card {
  width: 100%;
  max-width: 420px;
  padding: var(--spacing-huge);
  border-radius: var(--border-radius-large);
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: var(--spacing-huge);
}

.logo-container {
  margin-bottom: var(--spacing-medium);
}

.logo-icon {
  color: var(--primary-color);
  background-color: var(--primary-lighter);
  padding: var(--spacing-medium);
  border-radius: 50%;
  box-shadow: var(--shadow-base);
}

.login-card h2 {
  color: var(--text-primary);
  font-size: var(--font-size-extra-large);
  font-weight: var(--font-weight-primary);
  margin-bottom: var(--spacing-small);
}

.login-subtitle {
  color: var(--text-secondary);
  font-size: var(--font-size-base);
  margin-top: 0;
}

.login-form {
  margin-bottom: var(--spacing-large);
}

.input-wrapper {
  width: 100%;
}

.custom-input {
  width: 100%;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-medium);
  font-weight: var(--font-weight-primary);
  border-radius: var(--border-radius-base);
  background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transition: var(--transition-base);
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.login-footer {
  text-align: center;
}

.tips {
  margin-bottom: var(--spacing-medium);
}

.tips p {
  color: var(--text-secondary);
  font-size: var(--font-size-small);
  padding: var(--spacing-base);
  background-color: var(--fill-lighter);
  border-radius: var(--border-radius-base);
  border-left: 3px solid var(--primary-color);
}

.login-links {
  display: flex;
  justify-content: center;
  gap: var(--spacing-large);
}

.login-links a {
  color: var(--primary-color);
  font-size: var(--font-size-small);
  text-decoration: none;
  transition: var(--transition-base);
}

.login-links a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    max-width: 100%;
    margin: var(--spacing-large);
  }
  
  .decoration-circle {
    display: none;
  }
}
</style>