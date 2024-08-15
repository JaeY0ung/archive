import { ref, watch } from "vue";
import { defineStore } from "pinia";
import { httpStatusCode } from "@/util/http-status";
import { useRouter } from "vue-router";
import { userConfirm, findById, logout, findByEmail } from "@/api/user";
import firebase from "firebase/app";
import "firebase/messaging";
import { localAxios } from "@/util/http-common";

export const useUserStore = defineStore(
    "user",
    () => {
        const router = useRouter();

        // 유저 정보 및 상태
        const userInfo = ref(null);
        const isLogin = ref(false);
        const isLoginError = ref(false);
        const isValidToken = ref(false);
        const userReady = ref("false");
        const opponentUser = ref({
            nickname: "",
            userImg: null,
        });

        // 세션 타임아웃 관련
        const lastActivityTime = ref(null);
        const SESSION_TIMEOUT = 30 * 60 * 1000; // 30분

        // Firebase 설정
        const firebaseConfig = {
            apiKey: process.env.VUE_APP_FIREBASE_API_KEY,
            authDomain: process.env.VUE_APP_FIREBASE_AUTH_DOMAIN,
            projectId: process.env.VUE_APP_FIREBASE_PROJECT_ID,
            storageBucket: process.env.VUE_APP_FIREBASE_STORAGE_BUCKET,
            messagingSenderId: process.env.VUE_APP_FIREBASE_MESSAGING_SENDER_ID,
            appId: process.env.VUE_APP_FIREBASE_APP_ID,
            measurementId: process.env.VUE_APP_FIREBASE_MEASUREMENT_ID,
        };

        // Firebase 초기화
        if (!firebase.apps.length) {
            firebase.initializeApp(firebaseConfig);
        }

        const messaging = firebase.messaging();

        // 서비스 워커 등록 코드 추가
        if ("serviceWorker" in navigator) {
            navigator.serviceWorker.register("/firebase-messaging-sw.js").catch((err) => {
                console.error("Service Worker 등록 실패:", err);
            });
        }

        // Foreground 메시지 핸들러
        messaging.onMessage((payload) => {
            const notificationTitle = payload.notification.title;
            const notificationBody = payload.notification.body;
            const alertType = payload.data.alertTypeId
                ? parseInt(payload.data.alertTypeId, 10)
                : null;
            const roomId = payload.data.roomId ? parseInt(payload.data.roomId, 10) : null;

            // readStatus 기본값 설정
            const readStatus = payload.data.readStatus === "true";

            if (window && window.showNotification) {
                window.showNotification(
                    notificationTitle,
                    notificationBody,
                    alertType,
                    roomId,
                    readStatus
                );
            }
        });

        // Firebase 토큰 요청
        async function requestFirebaseToken() {
            try {
                await Notification.requestPermission();
                const token = await messaging.getToken();
                return token;
            } catch (error) {
                console.error("브라우저에 대한 알림 권한이 없습니다.");
                return null;
            }
        }


        // 사용자 활동 시간 갱신
        const updateLastActivityTime = () => {
            lastActivityTime.value = Date.now();
        };

        // 세션 타임아웃 체크
        const checkSessionTimeout = async () => {
            if (isLogin.value && lastActivityTime.value) {
                const currentTime = Date.now();
                if (currentTime - lastActivityTime.value > SESSION_TIMEOUT) {
                    await userLogout();
                    router.push({ name: "login" });
                }
            }
        };

        // 주기적으로 세션 타임아웃 체크
        setInterval(checkSessionTimeout, 60000); // 1분마다 체크

        // 사용자 활동 감지
        if (typeof window !== "undefined") {
            ["mousedown", "keydown", "scroll", "touchstart"].forEach((eventType) => {
                window.addEventListener(eventType, updateLastActivityTime);
            });
        }

        // userInfo가 변경될 때마다 lastActivityTime 갱신
        watch(userInfo, () => {
            if (userInfo.value) {
                updateLastActivityTime();
            }
        });

        
        // 유저 로그인
        const userLogin = async (loginUser) => {
            await userConfirm(
                loginUser,

                async (response) => {
                    let accessToken = "";
                    const authHeader = response.headers["authorization"];
                    if (authHeader && authHeader.startsWith("Bearer ")) {
                        accessToken = authHeader.substring(7);
                        sessionStorage.setItem("accessToken", accessToken);
                    } else {
                        console.warn("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
                    }

                    isLogin.value = true;
                    isLoginError.value = false;
                    isValidToken.value = true;
                    await getUserInfo();
                    await sendFirebaseTokenToServer(accessToken);
                    updateLastActivityTime();
                },
                (err) => {
                    console.error("로그인에 실패했습니다.", err);
                    isLogin.value = false;
                    isLoginError.value = true;
                    isValidToken.value = false;
                }
            );
        };

        const OAuth2userLogin = async () => {
            try {
                // 로그인 상태 설정
                isLogin.value = true;
                isLoginError.value = false;
                isValidToken.value = true;

                // 사용자 정보 가져오기
                await getUserInfo();

                // Firebase 토큰을 서버로 전송
                const accessToken = sessionStorage.getItem("accessToken");
                await sendFirebaseTokenToServer(accessToken);

                // 마지막 활동 시간 업데이트
                updateLastActivityTime();

            } catch (err) {
                console.error("OAuth2 로그인 중 오류 발생:", err);
                isLogin.value = false;
                isLoginError.value = true;
                isValidToken.value = false;
            }
        };

        // Firebase 토큰을 서버로 전송
        const sendFirebaseTokenToServer = async (accessToken) => {
            const local = localAxios();
            const firebaseToken = await requestFirebaseToken();
            if (!firebaseToken) {
                console.error("Firebase token 요청이 정상적으로 처리되지 않았습니다.");
                return;
            }

            await local.post(
                "/alert/save-firebaseToken",
                { firebaseToken, userId: userInfo.value.id },
                {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            );
        };

        // 유저 정보 가져오기
        const getUserInfo = async () => {
            await findByEmail(
                (response) => {
                    if (response.status === httpStatusCode.OK) {
                        userInfo.value = response.data;
                    } else {
                        console.error("해당 유저 정보가 없습니다.");
                    }
                },
                (err) => {
                    console.error("유저 정보를 가져오는데 오류 발생", err);
                }
            );
        };

        // 유저 로그아웃
        const userLogout = async () => {
            await logout(
                (response) => {
                    if (response.status === httpStatusCode.OK) {
                        router.push({ name: "login" });
                        isLogin.value = false;
                        isValidToken.value = false;
                        userInfo.value = '';
                        sessionStorage.removeItem("accessToken");
                        lastActivityTime.value = null;
                    } else {
                        console.error("유저 정보가 없습니다.");
                    }
                },
                (error) => {
                    console.error(error);
                    isLogin.value = false;
                }
            );
        };

        return {
            userLogin,
            OAuth2userLogin,
            userLogout,
            getUserInfo,
            userInfo,

            isLogin,
            isLoginError,
            isValidToken,
            
            userReady,
            opponentUser,
        };
    },
    { persist: true }
);
