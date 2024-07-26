<script setup>
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
import { ref, computed, onMounted } from 'vue';
import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user";
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const userStore = new useUserStore();

var stompClient = null;

const me = ref({
    img: "이미지1",
    name: "악카이브1",
    score: "0",
    isEmpty: true
})  

const opponent = ref({
    img: "이미지2",
    name: "유저를 기다리는 중...", 
    score: "0",
    isEmpty: true
})


// 웹소켓 생성 후, 구독하기.
function connect() {

        console.log("Connecting")

        var socket = new SockJS('http://localhost:8080/archive-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            //구독
            stompClient.subscribe('/wait/socket', function (chatMessage) {
                console.log("구독한 곳으로부터 정보 받아오는 과정")
                const userLogin = JSON.parse(chatMessage.body);
                // 정보 뿌리기
                // 상대방 프로필 표시

                if (opponent.value.isEmpty && decodeToken.username != userLogin.email) {
                    stompClient.send("/app/wait", {},   
                    JSON.stringify({
                        'id': "",
                        'email' : decodeToken.username
                    }));
                    opponent.value.name = userLogin.email;
                    opponent.value.isEmpty = true;
                    console.log("userLogin" + userLogin);
                }


 
            });


            // 자신의 유저 정보를 Controller에 보낸다.
            console.log("들어왓습니다~~~~~~~~")
            stompClient.send("/app/wait", {},   
            JSON.stringify({
                'id': "",
                'email' : decodeToken.username
            }));
            

        });
    }



const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose"
})

const goToBattle = () => {
    router.push({name:'battle'})
}
// 로그인한 자신의 정보 가져오기.
// session storage에서 access토큰 가져오기.
// 닉네임 정보를 가져오려 했으나, 아직 pk를 가져올 수 없어서 decodeToken의 이메일 정보를 대신 넣었다.
const accessToken = sessionStorage.getItem("accessToken");


userStore.getUserInfo(accessToken);

const decodeToken = jwtDecode(accessToken);


const email = decodeToken.username;

me.value.name = email;




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

onMounted(() => {
    connect()
})

</script>

<template>
    <div class="container">
        <div class="up">
            <RouterView/>
        </div>

        <div class="down">
            <!-- 아래 -->
            <div class="player-card">
                <div class="player-img">{{ me.img }}</div>
                <div class="player-info-text">
                    <div>{{ me.name }}</div>
                    <div>현재 스코어 : {{ me.score }}</div>
                </div>
            </div>

            <div class="button-div">
                <button class="btn btn-primary w-24" v-if="route.name == 'waitBattle'" @click="goToBattle">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" v-if="route.name == 'battle'">
                    나가기
                </button>
            </div>

            <div class="player-card">
                <div class="player-img">{{ opponent.img }}</div>
                <div class="player-info-text">
                    <div>{{ opponent.name }}</div>
                    <div>현재 스코어 : {{ opponent.score }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    margin: 10px auto; /*  상하 좌우 */
    width: 70vw;
}
.up {
    background-color: rgb(194, 161, 161);
    height: 70vh;
    margin-bottom: 50px;
}

.down {
    display: flex;
    justify-content: center;
}

.button-div {
    width: 20vw;
    display: flex;
    justify-content: center;
    align-items: end;
}

.player-card {
    display: flex;
    width: 40vw;
    border: 3px solid rgb(51, 117, 71);
    border-radius: 15px;
}

.player-img {
    width: 100px;
    height: 100px; 
    background-color: white;
    border-radius: 15px;
    
}
.player-info-text {
    width: 200px;
    margin-left: 10px;
}
</style>