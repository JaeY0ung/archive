<script setup>
import { useUserStore } from '@/stores/user'
// import Button from '@/common/ui/Button.vue';
// import Input from '@/common/ui/Input.vue'
import { ref } from 'vue';
import { storeToRefs } from 'pinia';

const userStore = useUserStore();
const { userLogin } = userStore;
const { isLogin, isLoginError } = storeToRefs(userStore);

const login = async () => {
	await userLogin(loginForm.value)
	let token = sessionStorage.getItem("accessToken")
	console.log("isLogin: " + isLogin.value + "\ntoken: " + token)
	if (isLogin.value) {
		getUserInfo(token)
		router.replace({ name: "main" });
    } else {
        alert("로그인 실패")
    }
}


const loginForm = ref({
    email: "",
    password: "",
})

// const changeEmail = (data) => {
//     loginForm.value.email = data;
// }

// const changePassword = (data) => {
//     loginForm.value.password = data;
// }

</script>

<template>
    <form class="login-form" @submit.prevent="login">
        <div class="align-middle">
            <label class="form-control w-full max-w-xs">
                <div class="label">
                    <span class="label-text">아이디 (이메일)</span>
                </div>
                <input v-model="loginForm.email" type="text"  class="input input-bordered w-full max-w-xs" />
            </label>

            <label class="form-control w-full max-w-xs">
                <div class="label">
                    <span class="label-text">비밀번호</span>
                </div>
                <input v-model="loginForm.password" type="password"  class="input input-bordered w-full max-w-xs" />
            </label>
        </div>

        <div class="flex justify-center">
            <button class="btn btn-primary" :class="color">
                로그인
            </button>
        </div>
    </form>
</template>

<style scoped>
.login-form {
    width: 50%;
    max-width: 400px;
    padding: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>