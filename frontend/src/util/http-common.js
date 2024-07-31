import axios from "axios";
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia';

const { VUE_APP_REQUEST_URL } = process.env;
// const userStore = useUserStore();
// const { userLogin } = userStore;

function localAxios() {
    const instance = axios.create({
        baseURL: VUE_APP_REQUEST_URL,
        withCredentials: true,
    });

// 요청 인터셉터
    instance.interceptors.request.use(
        (config) => {
            const accessToken = sessionStorage.getItem("accessToken");
            console.log("요청 인터셉터를 처리합니다.", `Bearer ${accessToken}`);
            if (accessToken) {
                config.headers["Authorization"] = `Bearer ${accessToken}`;
            }
            return config;
        },
        (error) => {
            console.log("get information from interceptors ", response)
            return Promise.reject(error);
        }
    );


// 응답 인터셉터
    instance.interceptors.response.use(
        (response) => {
            console.log("응답 인터셉터 처리가 성공되었습니다. ", response);
            return response;
        },
        async (error) => {
            console.log("응답 인터셉터 처리가 실패되었습니다. ", error.response?.data);
            console.log("응답 상태 코드: ", error.response?.status);

            const originalRequest = error.config;

            if (error.response?.status === 401 && error.response?.data === 'AccessTokenExpired' && !originalRequest._retry) {
                originalRequest._retry = true;
                console.log("리프레시 토큰으로 다시 요청합니다.");

                try {
                    // 리프레시 토큰 다시 요청
                    // 리프레시 토큰으로 검증하므로 accessToken 불필요, 이후 삭제 예상
                    const accessToken = sessionStorage.getItem("accessToken");
                    const response = await axios.get(`${VUE_APP_REQUEST_URL}/auth/refresh`, {
                        withCredentials: true,
                        headers: {
                            Authorization: accessToken ? `Bearer ${accessToken}` : ''
                        }
                    });

                    console.log("리프레시 토큰 응답:", response.data);  // 전체 응답 로깅

                    // response.data가 객체인지 확인
                    const newAccessToken = response.headers['authorization'];

                    if (newAccessToken && newAccessToken.startsWith('Bearer ')) {
                        const token = newAccessToken.split(' ')[1];
                        console.log("새로운 accessToken:", token);

                        // 새 토큰 저장
                        sessionStorage.setItem('accessToken', token);

                        // Axios 인스턴스의 기본 헤더 업데이트
                        instance.defaults.headers.common['Authorization'] = newAccessToken;

                        // 원래 요청 재시도
                        originalRequest.headers['Authorization'] = newAccessToken;
                        return instance(originalRequest);
                    } else {
                        console.error("서버 응답이 예상된 형식이 아닙니다:", response.data);
                    }

                    throw new Error("유효한 새 액세스 토큰을 받지 못했습니다.");
                } catch (refreshError) {
                    console.error("토큰 갱신 실패:", refreshError);
                    // 로그아웃 처리
                    // await store.dispatch("user/userLogout");
                    // 로그인 페이지로 리다이렉트
                    // router.push('/login');
                    return Promise.reject(refreshError);
                }
            }

            // 다른 에러 처리
            return Promise.reject(error);
        }
    );

    return instance;

}

export {localAxios};
