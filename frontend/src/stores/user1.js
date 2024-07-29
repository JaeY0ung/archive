import { ref } from "vue";
import { httpStatusCode } from "@/util/http-status";
import { useRouter } from "vue-router";
import { defineStore } from "pinia";
import jwtDecode from "jwt-decode";
import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user";

export const useUserStore = defineStore(
  "user",
  () => {
    const router = useRouter();

    const isLogin = ref(false);
    const isLoginError = ref(false);
    const userInfo = ref(null);
    const isValidToken = ref(false);

    const userLogin = async (loginUser) => {
      await userConfirm(
        loginUser,
        (response) => {
          console.log("loginUser: ");
          console.log(loginUser);
          //로그인 완료
          let { data } = response;
          console.log("로그인 완료 후 data ", data);

          // accessToken 토큰 저장
          let accessToken = data["data"]["accessToken"];
          sessionStorage.setItem("accessToken", accessToken);
          console.log("accessToke 저장 = ", accessToken);

          // refreshToken 확인
          let refreshToken = data["data"]["refreshToken"];
          sessionStorage.setItem("refreshToken", refreshToken);
          console.log("refreshToken 저장 = ", refreshToken);

          // 유저 정보 저장하기
          isLogin.value = true;
          isLoginError.value = false;
          isValidToken.value = true;
        },
        (error) => {
          console.log("loginUser: ");
          console.log(loginUser);
          console.log("로그인에 실패했습니다.");
          isLogin.value = false;
          isLoginError.value = true;
          isValidToken.value = false;
          console.error(error);
        }
      );
    };

    const tokenRegenerate = async () => {
      const refreshToken = sessionStorage.getItem("refreshToken");
      if (refreshToken) {
        await tokenRegeneration(
          refreshToken,
          (response) => {
            const newAccessToken = response.data.accessToken;
            sessionStorage.setItem("accessToken", newAccessToken);
            isValidToken.value = true;
          },
          async (error) => {
            // HttpStatus.UNAUTHORIZE(401) : RefreshToken 기간 만료 >> 다시 로그인!!!!
            if (error.response.status === httpStatusCode.UNAUTHORIZED) {
              await userLogout();
              alert("세션이 만료되었습니다. 다시 로그인해 주세요.");
            }
          }
        );
      } else {
        await userLogout();
      }
    };

    const userLogout = async () => {
      console.log("로그아웃 아이디 : " + (userInfo.value ? userInfo.value.username : "알 수 없음"));
      await logout(
        userInfo.value ? userInfo.value.username : "",
        (response) => {
          if (response.status === httpStatusCode.OK) {
            isLogin.value = false;
            userInfo.value = null;
            isValidToken.value = false;

            sessionStorage.removeItem("accessToken");
            sessionStorage.removeItem("refreshToken");
            alert("로그아웃 되었습니다");
            router.push({ name: "Login" });
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
      isLogin,
      isLoginError,
      userInfo,
      isValidToken,
      userLogin,
      tokenRegenerate,
      userLogout,
    };
  },
  { persist: true }
);
