import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import PaymentResultHandler from "@/common/modal/PaymentResultHandler.vue";
import { showLoginRequestAlert } from "@/util/alert";


const routes = [
    {
        path: '/',
        name: 'main',
        component: () => import('@/views/MainView.vue')
    },
    {
        path: '/sheet/search',
        name: 'sheetSearch',
        component: () => import('@/views/sheet/SheetSearchView.vue')
    },
    {
        path: '/sheet/:sheetId/detail',
        name: 'sheetDetail',
        component: () => import('@/views/sheet/SheetDetailView.vue')
    },
    {
        path: '/user/:nickName/profile',
        name: 'userProfile',
        component: () => import('@/views/user/UserProfileView.vue')
    },

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
    //  -----------------------------------------------
    {
        path: '/room/multi/:roomId/wait',
        name: 'multiWait',
        component: () => import('@/views/play/MultiWaitView.vue'),
    },
    {
        path: '/room/multi/:roomId/play/:sheetId',
        name: 'multiPlay',
        component: () => import('@/views/play/MultiPlayView.vue'),
    },
    // -----------------------------------------------
    {
        path: "/admin",
        name: "admin",
        component: () => import("@/layouts/AdminLayout.vue"),
        children: [
            {
                path: "manage/sheet",
                name: "manageSheet",
                component: () => import("@/views/admin/AdminSheetManageView.vue"),
            },
            {
                path: "manage/song",
                name: "manageSong",
                component: () => import("@/views/admin/AdminSongManageView.vue"),
            },
            {
                path: "sheet/upload",
                name: "adminUploadSheet",
                component: () => import("@/views/admin/AdminUploadSheetView.vue"),
            },
        ],
    },
    // -----------------------------------------------
    {
        path: "/mypage",
        name: "mypage",
        component: () => import("@/views/user/MyPageView.vue"),
    },
    {
        path: "/login",
        name: "login",
        component: () => import("@/views/user/LoginView.vue"),
    },
    {
        path: "/register",
        name: "register",
        component: () => import("@/views/user/UserRegisterView.vue"),
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
        component: () => import("@/views/sheet/SheetUploadView.vue"),
    },
    {
        path: "/sheet/fullscreen",
        name: "sheet/fullscreen",
        component: () => import("@/views/sheet/SheetFullScreenView.vue"),
    },
    // -----------------------------------------------
    {
        path: "/payment",
        name: "payment",
        component: () => import("@/views/order-and-pay/PaymentView.vue"),
    },
    // -----------------------------------------------
    {
        path: "/order",
        name: "order",
        component: () => import("@/views/order-and-pay/OrderView.vue"),
    },
    {
        path: "/payment",
        name: "payment",
        component: () => import("@/views/order-and-pay/PaymentView.vue"),
    },
    {
        path: "/payment/result",
        name: "PaymentResult",
        component: PaymentResultHandler,
    },
    {
        path: '/pay/completed',
        name: 'PaymentSuccess',
        component: PaymentResultHandler
    },
    {
        path: '/payment/cancel',
        name: 'PaymentCancel',
        component: PaymentResultHandler
    },
    {
        path: '/payment/fail',
        name: 'PaymentFail',
        component: PaymentResultHandler
    },
    // 음악 녹음 페이지
    {
        path: "/play/recording",
        name: "recording",
        component: () => import("@/views/sheet/RecordingVue"),
    },
    // 난이도 기여 페이지
    {
        path: "/difficulty/:sheetId",
        name: "sheetDifficultyRating",
        component: () => import("@/views/sheet/SheetDifficultyRatingView.vue"),
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

    if (to.meta.title) {
        document.title = to.meta.title;
    } else {
        document.title = '악카이브: 악보 실시간 대결 서비스'; // 기본 타이틀
    }

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
        showLoginRequestAlert(router, "로그인이 필요합니다.");
        next({ name: "login" });
    } else {
        next();
    }
});

export default router;
