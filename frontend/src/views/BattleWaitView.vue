<script setup>

import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user"
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";

const canLeaveSite = ref(false);

const userStore = useUserStore();

const router = useRouter();

let eventSource;

let score = ref(0);
let email = ref("");

const user = userStore

// 웹소켓 연결
// const connect = () => {
//     const socket = new WebSocket('ws://localhost:8081/wait')

//     socket.onopen = () => {
//         console.log('Socket opened!');
//     }

//     socket.onmessage = (event) => {
//         const data = JSON.parse(event.data);
//         score.value = data;
//         console.log(score.value);
//     };

//     socket.onclose = () => {
//         console.log('Socket closed');
//     }
// }

// // 화면이 로딩될 때 바로 웹소켓 연결이 이루어진다.
// onMounted(() => {
//     connect();
// })

// 로그인한 자신의 정보 가져오기.
// session storage에서 access토큰 가져오기.

// const accessToken = sessionStorage.getItem("accessToken");

// userStore.getUserInfo(accessToken);

// const decodeToken = jwtDecode(accessToken);

// email = decodeToken.username;


// pk가 있다면, pk를 이용해서 유저 닉네임 가져오기.
// axios({
//     url: `localhost:8080/users/${decodeToken.username}`,
//     method: 'GET',
//     headers: {
//         'Authorization': `Bearer ${accessToken}`
//     }
// })
// .then((response) => {
//     console.log(response.data)
// })

onBeforeRouteLeave((to, from, next) => {
    console.log('이동할 라우트:', to);
    console.log('현재 라우트:', from);

    if(to.name == "battle" && from.name == "waitBattle"){
        next();
    }else{
        if(confirm('방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. wait')){
            next();
        }else{
            next(false);
        }
    }
        
})




</script>

<template>
    <div>
        <h1 style="color: white;">대기 페이지</h1>
    </div>
</template>

<style scoped>

</style>