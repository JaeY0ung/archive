import axios from "axios";
import store from "../stores";
import {useUserStore} from "@/stores/user.js"; // Vuex 스토어가 있는 위치

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 쿠키 전송 허용
});

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem("accessToken");
    if (accessToken) {
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    if (error.response.status === 401 && error.response.data === 'AccessTokenExpired') {
      try {
        // 401 에러 발생 시 리프레시 토큰을 사용하여 액세스 토큰 갱신 시도
          const response = await axios.get('http://localhost:8080/auth/refresh', {withCredentials: true});
          const newAccessToken = response.data.accessToken;

        // 새 토큰을 저장하고, 원래 요청 재시도
          sessionStorage.setItem('accessToken', newAccessToken);
          error.config.headers['Authorization'] = `Bearer ${newAccessToken}`
        return axios(error.config);
      } catch (refreshError) {
        // TODO: 토큰 갱신 실패 시 로그아웃 처리
        // store.dispatch("user/userLogout");
        //   useUserStore.userLogout();

        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default api;
