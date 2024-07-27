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

const goToRegister = () => {
  router.push({ name: 'register' });
}

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
const naverLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/naver"
}


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