<script setup>
import { useUserStore } from '@/stores/user'
import { ref, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const userStore = useUserStore();
const { userLogin, getUserInfo } = userStore;
const { isLogin, isLoginError } = storeToRefs(userStore);

const loginForm = ref({
  email: "",
  password: "",
})

const login = async () => {
    await userLogin(loginForm.value);
    if (isLogin.value) {
        const token = sessionStorage.getItem("accessToken");
        if (token) {
            await getUserInfo(token);
            router.replace({ name: "main" });
        }
    } else {
        alert("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
    }
}

// OAuth2 - Naver
const clientId = "iDUO1TY8oTZc0dDxSfU9";
const clientSecret = "pMvsdDtbPQ";

const naverLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/naver";
}

// 네이버 로그인 콜백 처리 함수
const handleNaverLoginCallback = async () => {
    try {
        const response = await axios.get('http://localhost:8080/api/user/me', {
            headers: {
                Authorization: `Bearer ${sessionStorage.getItem('accessToken')}`
            }
        });
        console.log('사용자 정보:', response.data);
        // 여기서 필요한 추가 처리를 할 수 있습니다.
        // 예: Vuex 또는 Pinia 스토어에 사용자 정보 저장
        // userStore.setUserInfo(response.data);

        // 로그인 성공 후 메인 페이지로 이동
        router.push({ name: "main" });
    } catch (error) {
        console.error('사용자 정보 가져오기 실패:', error);
        // 에러 처리: 예를 들어, 에러 메시지 표시
        alert('로그인 처리 중 오류가 발생했습니다.');
    }
}

onMounted(() => {
    // URL에서 code와 state 파라미터 확인
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');

    if (code && state) {
        // 네이버 로그인 콜백 처리
        handleNaverLoginCallback();
    }
});
</script>

<template>
  <form class="container" @submit.prevent="login">
    <div class="text-5xl mb-20">
      로그인
    </div>
    <label class="form-control w-full mb-5">
      <div class="label">
        <span class="label-text">이메일</span>
      </div>
      <input v-model="loginForm.email" type="email" class="input input-bordered w-full" required/>
    </label>

    <label class="form-control w-full mb-10">
      <div class="label">
        <span class="label-text">비밀번호</span>
      </div>
      <input v-model="loginForm.password" type="password" class="input input-bordered w-full" required />
    </label>

    <div class="button-container">
      <button type="submit" class="btn btn-primary">
        로그인
      </button>
      <button type="button" class="btn btn-secondary" @click="goToRegister">
        회원가입
      </button>
    </div>
    
    <div class="mt-5">
      <button type="button" class="btn btn-success" @click="naverLogin">
        네이버로 로그인
      </button>
    </div>
  </form>
</template>

<style scoped>
.container {
  width: 500px;
  margin: 50px auto;
  text-align: center;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>