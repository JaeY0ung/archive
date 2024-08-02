import { ref } from 'vue'
import { httpStatusCode } from "@/util/http-status"
import { useRouter } from "vue-router"
import { defineStore } from 'pinia'
import { jwtDecode } from "jwt-decode"
import { userConfirm, findById, tokenRegeneration, logout, findByEmail } from "@/api/user"
import firebase from 'firebase/app';
import 'firebase/messaging';
import { localAxios } from '@/util/http-common';

export const useUserStore = defineStore('user', () => {
  const router = useRouter();

  // 유저 정보 스토어에 저장.
  const userInfo = ref(null)

  // 유저 상태 스토어에 저장
  const isLogin = ref(false)
  const isLoginError = ref(false)
  const isValidToken = ref(false)

    // Firebase 초기화 설정 (Firebase 프로젝트 설정에서 가져온 구성 객체)
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
  
  // Foreground 메시지 핸들러
  messaging.onMessage((payload) => {
    console.log('Message received. ', payload);
    // 알림을 브라우저에 표시
    new Notification(payload.notification.title, {
      body: payload.notification.body,
      icon: payload.notification.icon
    });
  });
  
    async function requestFirebaseToken() {
      try {
        await Notification.requestPermission();
        console.log("알림 권한 요청을 보냈습니다.")
        const token = await messaging.getToken();
        return token;
      } catch (error) {
        console.error('Unable to get permission to notify.', error);
        return null;
      }
    }


  const userLogin = async (loginUser) => {
    await userConfirm(
      loginUser,
      async (response) => {
        console.log("loginUser: ", loginUser);
        let accessToken = "";
        // 응답 헤더에서 Authorization 토큰 추출
        const authHeader = response.headers['authorization'];
        if (authHeader && authHeader.startsWith('Bearer ')) {
          accessToken = authHeader.substring(7);
          // 세션 스토리지에 저장
          sessionStorage.setItem("accessToken", accessToken);
          console.log("accessToken을 세션 스토리에 저장합니다. = ", accessToken);


        } else {
          console.warn("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
        }
  
        // 로그인 완료 후 데이터 처리
        let { data } = response;
        console.log("로그인 완료 후 data ", data);
  
        // 로그인 상태 업데이트
        isLogin.value = true;
        isLoginError.value = false;
        isValidToken.value = true;

        // 유저 정보 가져오기
        await getUserInfo();
        console.log("user 정보: = ", userInfo.value);

        // Firebase 토큰 발급 및 저장
        await sendFirebaseTokenToServer(accessToken);

          // console.log("User ID:", userInfo.value.id);
          // console.log("Role:", userInfo.value.role);
          // console.log("Email:", userInfo.value.email);
          // console.log("Nickname:", userInfo.value.nickname);
          // console.log("User Image:", userInfo.value.userImg);
          // console.log("Birth Date:", userInfo.value.birthDate);
          // console.log("Gender:", userInfo.value.gender);
          // console.log("Cash:", userInfo.value.cash);
          // console.log("Single Score:", userInfo.value.singleScore);
          // console.log("Multi Score:", userInfo.value.multiScore);
          // console.log("Deleted At:", userInfo.value.deletedAt);
      },
      (error) => {
        console.log("loginUser: ", loginUser);
        console.log("로그인에 실패했습니다.");
        isLogin.value = false;
        isLoginError.value = true;
        isValidToken.value = false;
        console.error(error);
      }
    );
  };


  const sendFirebaseTokenToServer = async () => {
    const local = localAxios();
    const firebaseToken = await requestFirebaseToken();
    if (!firebaseToken) {
      console.error('Firebase token 요청이 정상적으로 처리되지 않았습니다.');
      return;
    }
    console.log("firebase token 요청 정상 처리: " + firebaseToken);

    // Firebase 토큰을 서버로 전송
    await local.post('/alert/save-firebaseToken', { firebaseToken, userId: userInfo.value.id }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  };

  const getUserInfo = async () => {
      await findByEmail(
          (response) => {
              if (response.status === httpStatusCode.OK) {
                // 스토어 정보 넣기
                userInfo.value = response.data
                console.log("스토어에 저장된 정보입니다.", userInfo)
              } else {
                  console.log("해당 유저 정보가 없습니다.")
              }
          },

      (error) => {
        console.log('유저 정보를 가져오는데 오류 발생', error)
      }
    )
  }

  const tokenRegenerate = async () => {
    await tokenRegeneration(
      JSON.stringify(userInfo.value),
      (response) => {
        if (response.status === httpStatusCode.CREATE) {
          let accessToken = response.data["access-token"]
          sessionStorage.setItem("accessToken", accessToken)
          isValidToken.value = true
        }
      },
      async (error) => {

      }
    )
  }

  const userLogout = async () => {
    await logout(
      (response) => {
        if (response.status === httpStatusCode.OK) {
          // 스토어 유저 정보 초기화하기
          userInfo.value = null

          // 스토어 유저 상태 변경하기
          isLogin.value = false
          isValidToken.value = false

          sessionStorage.removeItem("accessToken")
          console.log("로그아웃이 되었습니다.")
        } else {
          console.error("유저 정보가 없습니다.")
        }
      },
      (error) => {
        console.log(error);
        isLogin.value = false;
      }
    )
  }

  return {
    userLogin,
    userLogout,
    getUserInfo,

    userInfo,
    isLogin,
    isLoginError,


    isValidToken,
    tokenRegenerate,
  }
}, {persist : true})
