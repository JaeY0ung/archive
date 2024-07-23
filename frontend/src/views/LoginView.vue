<script setup>
import { useUserStore } from '@/stores/user'
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

</script>

<template>
    <form class="container" @submit.prevent="login">
        <div class="text-5xl mb-20">
            로그인
        </div>
        <label class="form-control w-full mb-5">
            <div class="label">
                <span class="label-text">아이디 (이메일)</span>
            </div>
            <input v-model="loginForm.email" type="text"  class="input input-bordered w-full" />
        </label>

        <label class="form-control w-full mb-10">
            <div class="label">
                <span class="label-text">비밀번호</span>
            </div>
            <input v-model="loginForm.password" type="password"  class="input input-bordered w-full" />
        </label>

        <div>
            <button class="btn btn-primary">
                로그인
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

</style>