import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

const routes = [
    {
        path: '/',
        name: 'main',
        component: () => import('@/views/MainView.vue')
    },
    {
        path: '/sheet/search',
        name: 'sheetSearch',
        component: () => import('@/views/SheetSearchView.vue')
    },
    {
        path: '/sheet/:sheetId/detail',
        name: 'sheetDetail',
        component: () => import('@/views/SheetDetailView.vue')
    },
    {
        path: '/user/:nickName/profile',
        name: 'userProfile',
        component: () => import('@/views/UserProfileView.vue')
    },
    // {
    //     path: "/",
    //     children: [
            
    //     ]
    // },
    // -----------------------------------------------
    {
        path: "/room/multi/list",
        name: "multiRoomList",
        component: () => import('@/views/play/MultiRoomListView.vue')
    },
    // -----------------------------------------------
    {
        path: '/room/single/wait',
        name: 'singleWait',
        component: () => import('@/views/play/SingleWaitView.vue'),
    },
    {
        path: '/room/single/play/:sheetId',
        name: 'singlePlay',
        component: () => import('@/views/play/SinglePlayView.vue'),
    },
     // -----------------------------------------------
    {
        path: '/room/multi/:roomId/wait',
        name: 'multiWait',
        component: () => import('@/views/play/MultiWaitView.vue'),
    },
    {
        path: '/room/multi/play/:sheetId',
        name: 'multiPlay',
        component: () => import('@/views/play/MultiPlayView.vue'),
    },
    // {
    //     path: 'play',
    //     name: 'play',
    //     component: () => import('@/common/sheet/Sheet2.vue')
    // },
    // -----------------------------------------------
    {
        path: "/admin",
        name: "admin",
        component: () => import("@/views/AdminView.vue"),
        children: [
            {
                path: "sheet/manage",
                name: "manageSheet",
                component: () => import("@/views/AdminSheetManageView.vue"),
            },
        ],
    },
    // -----------------------------------------------
    {
        path: "/mypage",
        name: "mypage",
        component: () => import("@/views/MyPageView.vue"),
    },
    {
        path: "/login",
        name: "login",
        component: () => import("@/views/LoginView.vue"),
    },
    {
        path: "/register",
        name: "register",
        component: () => import("@/views/RegisterView.vue"),
    },
    {
        path: "/auth/register",
        name: "auth-register",
        component: () => import("@/views/user/OAuth2RegisterView.vue"),
    },
    {
        path: "/auth-success",
        name: "auth-success",
        component: () => import("@/views/user/OAuth2SuccessView.vue"),
    },
    // -----------------------------------------------
    {
        path: "/sheet/upload",
        name: "sheetUpload",
        component: () => import("@/views/SheetUploadView.vue"),
    },
    {
        path: "/sheet/fullscreen",
        name: "sheet/fullscreen",
        component: () => import("@/views/SheetFullScreenView.vue"),
    },
    // -----------------------------------------------
    {
        path: "/payment",
        name: "payment",
        component: () => import("@/views/PaymentView.vue"),
    },
    // -----------------------------------------------
    {
        path: "/order",
        name: "order",
        component: () => import("@/views/OrderView.vue"),
    },
    {
        path: "/payment",
        name: "payment",
        component: () => import("@/views/PaymentView.vue"),
    },
    // 음악 녹음 페이지
    {
        path: "/play/recording",
        name: "recording",
        component: () => import("@/views/RecordingVue"),
    },
    // 난이도 기여 페이지
    {
        path: "/difficulty/:sheetId",
        name: "sheetDifficultyRating",
        component: () => import("@/views/SheetDifficultyRatingView.vue"),
    },
    // -----------------------------------------------
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// 로그인이 필요한 라우트 목록
const authRequiredRoutes = ["mypage", "order", "sheetUpload", "pianoSaurus", "recording", "sheetDifficultyRating"];

// 전역 네비게이션 가드 수정
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();

    // '/login' 경로로 이동 관리
    if (to.path === "/login") {
        if (userStore.isLogin) {
            alert("이미 로그인이 되었습니다.");
            next({ name: "main" });
        } else {
            next();
        }
    }
    // 로그인이 필요한 라우트에 대한 체크
    else if (authRequiredRoutes.includes(to.name) && !userStore.isLogin) {
        alert("로그인이 필요합니다.");
        next({ name: "login" });
    } else {
        next();
    }
});

export default router;
