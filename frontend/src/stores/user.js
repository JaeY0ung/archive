import { ref } from 'vue'
import { httpStatusCode } from "@/util/http-status"
import { useRouter } from "vue-router"
import { defineStore } from 'pinia'
import { jwtDecode } from "jwt-decode"
import { userConfirm, findById, tokenRegeneration, logout, findByEmail } from "@/api/user"

export const useUserStore = defineStore('user', () => {
  const router = useRouter();

  // 유저 정보 스토어에 저장
  const userInfo = ref(null)

  // 유저 상태 스토어에 저장
  const isLogin = ref(false)
  const isLoginError = ref(false)
  const isValidToken = ref(false)

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
  
        // TODO: 유저 정보 저장하기 (UserStore에 저장)

  
        // 로그인 상태 업데이트
        isLogin.value = true;
        isLoginError.value = false;
        isValidToken.value = true;

          // 유저 정보 가져오기
          await getUserInfo(accessToken);
          console.log("user 정보: = ", userInfo.value);
          // console.log(userInfo.value.email);

          console.log("User ID:", userInfo.value.id);
          console.log("Role:", userInfo.value.role);
          console.log("Email:", userInfo.value.email);
          console.log("Nickname:", userInfo.value.nickname);
          console.log("User Image:", userInfo.value.userImg);
          console.log("Birth Date:", userInfo.value.birthDate);
          console.log("Gender:", userInfo.value.gender);
          console.log("Cash:", userInfo.value.cash);
          console.log("Single Score:", userInfo.value.singleScore);
          console.log("Multi Score:", userInfo.value.multiScore);
          console.log("Deleted At:", userInfo.value.deletedAt);
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

  const getUserInfo = async (token) => {
    let decodeToken = jwtDecode(token)
    console.log('token: ', token)
    console.log('decodeToken: ', decodeToken);
      await findByEmail(
          (response) => {
              if (response.status === httpStatusCode.OK) {
                  userInfo.value = response.data
              } else {
                  console.log("해당 유저 정보가 없습니다.")
              }
          },

      (error) => {
        console.log('유저 정보를 가져오는데 오류 발생')
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
