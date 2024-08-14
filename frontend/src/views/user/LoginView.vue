<template>
    <div class="w-full h-full flex justify-center items-center">
        <div class="w-[400px] bg-white bg-opacity-50 rounded-2xl p-8 shadow-lg">
            <h1 class="text-4xl font-bold mb-8 text-center text-gray-600">로그인</h1>
            <form @submit.prevent="login" class="space-y-6">
                <div class="space-y-2">
                    <label for="email" class="block text-sm font-medium text-gray-700"
                        >이메일</label
                    >
                    <input
                        v-model="loginForm.email"
                        id="email"
                        type="email"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
                    />
                </div>
                <div class="space-y-2">
                    <label for="password" class="block text-sm font-medium text-gray-700"
                        >비밀번호</label
                    >
                    <input
                        v-model="loginForm.password"
                        id="password"
                        type="password"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-purple-500"
                    />
                </div>
                <div class="flex flex-col space-y-4">
                    <button
                        type="submit"
                        class="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
                    >
                        로그인
                    </button>
                    <button
                        type="button"
                        @click="goToRegister"
                        class="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-purple-600 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
                    >
                        회원가입
                    </button>
                    <button
                        type="button"
                        @click="naverLogin"
                        class="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-500 hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 flex items-center justify-center"
                    >
                        네이버로 로그인
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

const { VUE_APP_REQUEST_URL } = process.env;

const router = useRouter();
const userStore = useUserStore();
const { userLogin } = userStore;
const { isLogin } = storeToRefs(userStore);

const loginForm = ref({
    email: "",
    password: "",
});

const goToRegister = () => {
    router.push({ name: "register" });
};

const login = async () => {
    await userLogin(loginForm.value);
    if (isLogin.value) {
        router.push({ name: "main" });
    } else {
        alert("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
    }
};

const naverLogin = () => {
    window.location.href = `${VUE_APP_REQUEST_URL}/oauth2/authorization/naver`;
};
</script>

<style scoped>
.naver-login-btn {
    display: inline-flex;
    align-items: center;
    background-color: #03c75a;
    color: white;
    padding: 0;
    border: none;
    border-radius: 2px;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial,
        sans-serif;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    text-decoration: none;
}
.naver-login-btn:hover {
    background-color: #02b150;
}
.naver-logo {
    background-color: #03c75a;
    padding: 10px 12px;
}
.naver-text {
    padding: 10px 12px;
    background-color: #03c75a;
}
</style>
