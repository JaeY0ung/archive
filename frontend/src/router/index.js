import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
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
          path: 'sheet/:sheetId/detail',
          name: 'sheetDetail',
          component: () => import('@/views/SheetDetailView.vue')
        },
        {
          path: 'user/:nickName/profile',
          name: 'userProfile',
          component: () => import('@/views/UserProfileView.vue')
        },
      ]
    },
    // -----------------------------------------------
    {
      path: "/battleroom",
      component: () => import('@/layouts/BattleLayout.vue'),
      children: [
        {
          path: 'wait',
          name: 'waitBattle',
          component: () => import('@/views/BattleWaitView.vue')
        },
        {
          path: 'battle',
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
      path: '/auth/register',
      name: 'auth-register',
      component: () => import('@/views/user/OAuth2RegisterView.vue')
    },
    {
      path: '/auth-success',
      name: 'auth-success',
      component: () => import('@/views/user/OAuth2SuccessView.vue')
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
    // 난이도 기여 페이지
    {
      path: '/difficulty/:sheetId',
      name: 'sheetDifficultyRating',
      component: () => import('@/views/SheetDifficultyRatingView.vue')
    },
    // -----------------------------------------------
  ]
})

// 전역 네비게이션 가드 추가
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // '/login' 경로로 이동 관리
  if (to.path === '/login') {
    if (userStore.isLogin) {
      alert('이미 로그인이 되었습니다.')
      next({ name: 'main' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
