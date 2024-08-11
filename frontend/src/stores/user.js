import { ref, watch } from "vue";
import { defineStore } from "pinia";
import { httpStatusCode } from "@/util/http-status";
import { useRouter } from "vue-router";
import jwtDecode from "jwt-decode"; // 잘못된 import 수정
import { userConfirm, findById, tokenRegeneration, logout, findByEmail } from "@/api/user";
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
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.register('/firebase-messaging-sw.js')
                .catch((err) => {
                    console.error('Service Worker 등록 실패:', err);
                });
        }

        // Foreground 메시지 핸들러
        messaging.onMessage((payload) => {
            // console.log("Message received. ", payload);
            const notificationTitle = payload.notification.title;
            const notificationBody = payload.notification.body;
            const alertType = payload.data.alertTypeId ? parseInt(payload.data.alertTypeId, 10) : null;
            const roomId = payload.data.roomId ? parseInt(payload.data.roomId, 10) : null;

            // readStatus 기본값 설정
            // console.log("readStatus play.js에서 기본값 설정 전: " + readStatus);
            const readStatus = payload.data.readStatus === "true";
            console.log("readStatus play.js에서 기본값 설정 후: " + readStatus);

            if (window && window.showNotification) {
                // window.showNotification(notificationTitle, notificationBody, alertType, roomId);
                window.showNotification(notificationTitle, notificationBody, alertType, roomId, readStatus);
            }
        });

        // Firebase 토큰 요청
        async function requestFirebaseToken() {
            try {
                await Notification.requestPermission();
                // console.log("알림 권한 요청을 보냈습니다.");
                const token = await messaging.getToken();
                return token;
            } catch (error) {
                console.error("Unable to get permission to notify.", error);
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
                    router.push({ name: 'login' });
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
                    // console.log("loginUser: ", loginUser);
                    let accessToken = "";
                    const authHeader = response.headers["authorization"];
                    if (authHeader && authHeader.startsWith("Bearer ")) {
                        accessToken = authHeader.substring(7);
                        sessionStorage.setItem("accessToken", accessToken);
                        // console.log("accessToken을 세션 스토리에 저장합니다. = ", accessToken);
                    } else {
                        console.warn("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
                    }

                    let { data } = response;
                    // console.log("로그인 완료 후 data ", data);

                    isLogin.value = true;
                    isLoginError.value = false;
                    isValidToken.value = true;

                    await getUserInfo();
                    // console.log("user 정보: = ", userInfo.value);

                    await sendFirebaseTokenToServer(accessToken);

                    updateLastActivityTime();
                },
                (err) => {
                    // console.log("loginUser: ", loginUser);
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
                // console.log("user 정보: = ", userInfo.value);

                // Firebase 토큰을 서버로 전송
                const accessToken = sessionStorage.getItem("accessToken")
                await sendFirebaseTokenToServer(accessToken);

                // 마지막 활동 시간 업데이트
                updateLastActivityTime();

                // console.log("OAuth2 로그인 성공");
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
            // console.log("firebase token 요청 정상 처리: " + firebaseToken);

            await local.post(
                "/alert/save-firebaseToken",
                { firebaseToken, userId: userInfo.value.id },
                {
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${accessToken}`
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
                        // console.log("스토어에 저장된 정보입니다.", userInfo);
                    } else {
                        console.log("해당 유저 정보가 없습니다.");
                    }
                },
                (err) => {
                    console.log("유저 정보를 가져오는데 오류 발생", err);
                }
            );
        };

        // 토큰 재발급
        const tokenRegenerate = async () => {
            await tokenRegeneration(
                JSON.stringify(userInfo.value),
                (response) => {
                    if (response.status === httpStatusCode.CREATE) {
                        let accessToken = response.data["access-token"];
                        sessionStorage.setItem("accessToken", accessToken);
                        isValidToken.value = true;
                    }
                },
                async (error) => {
                    // 에러 처리
                }
            );
        };

        // 유저 로그아웃
        const userLogout = async () => {
            await logout(
                (response) => {
                    if (response.status === httpStatusCode.OK) {
                        userInfo.value = null;
                        isLogin.value = false;
                        isValidToken.value = false;
                        sessionStorage.removeItem("accessToken");
                        lastActivityTime.value = null;
                        router.push({ name: 'login' });
                        // console.log("로그아웃 되었습니다.");
                    } else {
                        console.error("유저 정보가 없습니다.");
                    }
                },
                (error) => {
                    console.log(error);
                    isLogin.value = false;
                }
            );
        };

        return {
            userLogin,
            userLogout,
            getUserInfo,
            userInfo,
            isLogin,
            isLoginError,
            isValidToken,
            tokenRegenerate,
            userReady,
            opponentUser,
            OAuth2userLogin
        };
    },
    { persist: true }
);
