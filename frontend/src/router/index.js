import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: () => import('@/layouts/TopSearchBarLayout.vue'),
      children: [
        {
          path: '',
          name: 'main',
          component: () => import('@/views/MainView.vue')
        },
        {
          path: 'sheet/search',
          name: 'sheetSearch',
          component: () => import('@/views/SheetSearchView.vue')
        },
        {
          path: 'sheet/detail',
          name: 'sheetDetail',
          component: () => import('@/views/SheetDetailView.vue')
        },
      ]
    },
    // -----------------------------------------------
    {
      path: "/battleroom",
      component: () => import('@/layouts/BattleLayout.vue'),
      children: [
        {
          path: '/wait',
          name: 'waitBattle',
          component: () => import('@/views/BattleWaitView.vue')
        },
        {
          path: '/battle',
          name: 'battle',
          component: () => import('@/views/BattleView.vue')
        },
      ]
    },
    
    // -----------------------------------------------
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/AdminView.vue')
    },
    // -----------------------------------------------
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('@/views/MyPageView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/test',
      name: 'naverLogin',
      component: () => import('@/views/components/NaverLogin.vue')
    },
    // -----------------------------------------------
    {
      path: '/sheet/upload',
      name: 'sheetUpload',
      component: () => import('@/views/SheetUploadView.vue')
    },
    {
      path: '/sheet/fullscreen',
      name: 'sheet/fullscreen',
      component: () => import('@/views/SheetFullScreenView.vue')
    },
     // -----------------------------------------------
    {
      path: '/payment',
      name: 'payment',
      component: () => import('@/views/PaymentView.vue')
    },
    // -----------------------------------------------
  ]
})

export default router
