import { ref } from 'vue'
import { httpStatusCode } from "@/util/http-status"
import { useRouter } from "vue-router"
import { defineStore } from 'pinia'
import { jwtDecode } from "jwt-decode"
import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user"

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
      (response) => {
        console.log("loginUser: ", loginUser);
        
        // 응답 헤더에서 Authorization 토큰 추출
        const authHeader = response.headers['authorization'];
        if (authHeader && authHeader.startsWith('Bearer ')) {
          const accessToken = authHeader.substring(7);
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
    await findById(
      decodeToken.username,
      (response) => {
        if (response.status === httpStatusCode.OK) {
          userInfo.value = response.data.userInfo
          console.log("유저 정보 가져오기 성공")
          console.log(userInfo)
        } else {
          console.log("해당 유저 정보가 없습니다.")
        }
      },
      async (error) => {
        console.error("토큰이 만료되어 사용 불가능합니다.",)
        console.error(error.response.status, error.response.statusText)
        isValidToken.value = false

        await tokenRegenerate()
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
        // HttpStatus.UNAUTHORIZE(401) : RefreshToken 기간 만료 >> 다시 로그인!!!!
        if (error.response.status === httpStatusCode.UNAUTHORIZED) {
          // 다시 로그인 전 DB에 저장된 RefreshToken 제거.
          await logout(
            userInfo.value.username,
            (response) => {
              if (response.status === httpStatusCode.OK) {
                console.log("리프레시 토큰 제거 성공")
              } else {
                console.log("리프레시 토큰 제거 실패")
              }
              alert("RefreshToken 기간 만료!!! 다시 로그인해 주세요.")
              isLogin.value = false
              userInfo.value = null
              isValidToken.value = false
              router.push({ name: "login" })
            },
            (error) => {
              console.error(error)
              isLogin.value = false
              userInfo.value = null
            }
          )
        }
      }
    )
  }

  const userLogout = async () => {
    console.log("로그아웃 아이디 : " + userInfo.value.username)
    await logout(
      userInfo.value.username,
      (response) => {
        if (response.status === httpStatusCode.OK) {
          isLogin.value = false
          userInfo.value = null
          isValidToken.value = false

          sessionStorage.removeItem("accessToken")
          sessionStorage.removeItem("refreshToken")
          alert("로그아웃 되었습니다")
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
    isLogin,
    isLoginError,
    userInfo,
    isValidToken,
    userLogin,
    getUserInfo,
    tokenRegenerate,
    userLogout,
  }
}, {persist : true})
