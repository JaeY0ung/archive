import { ref } from 'vue'
import { useRouter } from "vue-router"
import { defineStore } from 'pinia'
import { jwtDecode } from "jwt-decode"
import {
  userConfirm,
  findById,
  tokenRegeneration,
  logout,
} from "@/api/user"
import { httpStatusCode } from "@/components/util/http-status"

export const useUserStore = defineStore('user', () => {
  const lat = ref(37.5); // 37.501228095438144
  const lng = ref(0); // 127.03957077888003
  const router = useRouter();

  const isLogin = ref(false)
  const isLoginError = ref(false)
  const userInfo = ref(null)
  const isValidToken = ref(false)

  const getLatLng = async () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => { // GeoLocation을 이용해서 접속 위치를 얻어옵니다
        lat.value = position.coords.latitude
        lng.value = position.coords.longitude;
        console.log("[사용자의 위치 가져옴] ", lat.value, " ", lng.value);
      });
    }
}

  const userLogin = async (loginUser) => {
    await userConfirm(
      loginUser,
      (response) => {
        if (response.status === httpStatusCode.CREATE) {
          let { data } = response
          let accessToken = data["access-token"]
          let refreshToken = data["refresh-token"]
          isLogin.value = true
          isLoginError.value = false
          isValidToken.value = true
          sessionStorage.setItem("accessToken", accessToken)
          sessionStorage.setItem("refreshToken", refreshToken)
          alert(`${loginUser.username}님 환영합니다`)
        }
      },
      (error) => {
        console.log("로그인 실패!!!!")
        isLogin.value = false
        isLoginError.value = true
        isValidToken.value = false
        console.error(error)
      }
    )
  }

  const getUserInfo = async (token) => {
    let decodeToken = jwtDecode(token)
    console.log('token: ', token)
    console.log('decodeToken: ', decodeToken);
    await findById(
      decodeToken.username,
      (response) => {
        if (response.status === httpStatusCode.OK) {
          userInfo.value = response.data.userInfo
        } else {
          console.log("유저 정보 없음!!!!")
        }
      },
      async (error) => {
        console.error("g[토큰 만료되어 사용 불가능.] : ",)
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
          console.error("유저 정보 없음!!!!")
        }
      },
      (error) => {
        console.log(error);
        isLogin.value = false;
      }
    )
  }

  return {
    lat, lng,
    isLogin,
    isLoginError,
    userInfo,
    isValidToken,
    getLatLng,
    userLogin,
    getUserInfo,
    tokenRegenerate,
    userLogout,
  }
}, {persist : true})
