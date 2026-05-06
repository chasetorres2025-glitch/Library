import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { title: '用户管理', requiresAdmin: true }
      },
      {
        path: '/category',
        name: 'Category',
        component: () => import('../views/Category.vue'),
        meta: { title: '图书分类', requiresAdmin: true }
      },
      {
        path: '/book',
        name: 'Book',
        component: () => import('../views/Book.vue'),
        meta: { title: '图书管理', requiresAdmin: true }
      },
      {
        path: '/borrow',
        name: 'Borrow',
        component: () => import('../views/Borrow.vue'),
        meta: { title: '借阅管理' }
      },
      {
        path: '/my-borrow',
        name: 'MyBorrow',
        component: () => import('../views/MyBorrow.vue'),
        meta: { title: '我的借阅' }
      },
      {
        path: '/ai-chat',
        name: 'AiChat',
        component: () => import('../views/AiChat.vue'),
        meta: { title: '智能助手' }
      },
      {
        path: '/profile',
        name: 'UserProfile',
        component: () => import('../views/UserProfile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: '/book-tag',
        name: 'BookTagManage',
        component: () => import('../views/BookTagManage.vue'),
        meta: { title: '标签管理', requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/dashboard')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
