import { createRouter, createWebHistory } from 'vue-router'

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
    path: "/pianosaurus",
    name: "pianoSaurus",
    component: () => import('@/views/play/PianoSaurusView.vue')
    },
    // -----------------------------------------------
    {
      path: "/room",
      component: () => import('@/layouts/RoomLayout.vue'),
      children: [
        {
          path: 'wait',
          name: 'wait',
          component: () => import('@/views/play/WaitView.vue')
        },
        {
          path: 'play',
          name: 'play',
          component: () => import('@/views/play/PlayingView.vue')
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
  ]
})

export default router
