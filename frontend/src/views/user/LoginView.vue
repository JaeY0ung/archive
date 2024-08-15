<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

// Google Fonts에서 Poppins 폰트 추가
const link = document.createElement("link");
link.href = "https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap";
link.rel = "stylesheet";
document.head.appendChild(link);

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
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
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
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div class="flex flex-col space-y-4">
                    <button type="submit" class="btn-primary btn-primary-login">로그인</button>
                    <button
                        type="button"
                        @click="goToRegister"
                        class="btn-primary btn-primary-register"
                    >
                        회원가입
                    </button>
                    <button
                        type="button"
                        @click="naverLogin"
                        class="btn-primary naver-login-btn flex items-center justify-center"
                    >
                        <img
                            src="@/assets/img/naver.png"
                            alt="네이버로 로그인"
                            class="naver-login-image"
                        />
                        <span class="ml-2">네이버로 로그인</span>
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<style scoped>
/* Primary button for login */
.btn-primary {
    width: 100%;
    height: 40px;
    padding: 12px 24px;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    font-family: "Poppins", sans-serif;
    cursor: pointer;
    text-align: center;
    color: white;
    background-color: #3FA2F6;
    transition: background-color 0.3s ease;
}

.btn-primary:hover {
    background-color: #2f5fb2;
}

.btn-primary-register {
    width: 100%;
    height: 40px;
    padding: 12px 24px;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    font-family: "Poppins", sans-serif;
    cursor: pointer;
    text-align: center;
    color: white;
    background-color: #3a73e5;
    transition: background-color 0.3s ease;
}

.btn-primary-register:hover {
    background-color: rgb(49, 74, 121);
}

.naver-login-btn {
    background-color: rgb(3, 199, 90);
}

.naver-login-btn:hover {
    background-color: rgb(28, 135, 76);
}

.naver-login-image {
    width: 24px;
    height: 24px;
    object-fit: contain;
}
</style>
