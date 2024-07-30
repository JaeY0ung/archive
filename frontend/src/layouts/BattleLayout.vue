<script setup>
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router';

const route = useRoute();
const router = useRouter();
import { ref, computed, onMounted, onBeforeUnmount, onUnmounted, watch } from 'vue';
import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user";
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const userStore = new useUserStore();

var stompClient = null;

// 준비하기를 동적으로 변화시켜주기 위한 불리언 변수
const isReady = ref("false");
const opponentReady = ref("false");

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

// 사이트를 벗어날 때 체크해주는 불리언 변수
const canLeaveSite = ref(false);

// 웹소켓 생성 후, 구독하기.
function connect() {


        var socket = new SockJS('http://localhost:8081/archive-websocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            //구독
            stompClient.subscribe('/wait/socket', function (chatMessage) {
                const userLogin = JSON.parse(chatMessage.body);
                // 정보 뿌리기
                // 상대방 프로필 표시

                if (userLogin.id == "profile" && opponent.value.isEmpty && decodeToken.username != userLogin.email) {
                    
                    stompClient.send("/app/wait", {},   
                    JSON.stringify({
                        'id': "profile",
                        'email' : decodeToken.username
                    }));
                    
                    opponent.value.name = userLogin.email;
                    opponent.value.isEmpty = false;

                    // 자신의 ready 정보를 Controller에 보낸다.
                    stompClient.send("/app/wait/ready", {},   
                    JSON.stringify({
                        'sender': decodeToken.username,
                        'isReady' : isReady.value
                    }));
                
                }
 
            });

            // 준비 상태를 받기 위한 또 다른 구독.
            stompClient.subscribe('/wait/socket/ready', function (readyStatus){
                const playerReady = JSON.parse(readyStatus.body);
            

                // ready 정보를 받아서 적용하기 위한 조건문
                if(playerReady.sender != decodeToken.username){
                    // 상대방이 ready를 누르면, type이 ready인 json 데이터가 온다.
                    // 그럼 이 조건문에 도달할 것이고, 여기서 준비하기 버튼을 바꿔주면 된다.
                    opponentReady.value = playerReady.isReady;
                }
            
            })

            stompClient.subscribe('/wait/socket/start', function(socket){
                console.log("시작을 위한 구독 성공")
                
                const message = JSON.parse(socket.body);

                // 게임 시작을 알리는 데이터가 온 경우
                if(message.type == "start"){
                    if(message.content == "true"){
                        router.push({name:'battle'})

                        // window.addEventListener('beforeunload', unLoadEvent);
                    }
                }

                // 상대방이 나갔음을 알리는 데이터가 온 경우
                if(message.type == "exit"){
                    opponent.value.name = "유저를 기다리는 중...";
                    opponent.value.isEmpty = true;
                    opponentReady.value = "false";
                }


            })


            
            // 자신의 유저 정보를 Controller에 보낸다.
            stompClient.send("/app/wait", {},   
            JSON.stringify({
                'id': "profile",
                'email' : decodeToken.username
            }));

            
            // 자신의 ready 정보를 Controller에 보낸다.
            stompClient.send("/app/wait/ready", {},   
            JSON.stringify({
                'sender': decodeToken.username,
                'isReady' : isReady.value
            }));
            

        });
    }

function disconnect() {
    console.log("disconnect되었다")
}

onBeforeUnmount(() =>{


    sendExit();

    window.removeEventListener('beforeunload', unLoadEvent);
})

function sendExit(){

    stompClient.send('/app/wait/start', {},
        JSON.stringify({
            'type': 'exit',
            'sender': decodeToken.username,
            'content': 'true'
        })
    )

}

onUnmounted(() => {
    console.log("onUnmounted 실행");

    sendExit();

})

window.addEventListener('beforeunload', sendExit);

const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose"
})

const goToBattle = () => {
    router.push({name:'battle'})

    // window.addEventListener('beforeunload', unLoadEvent);

    canLeaveSite.value = true;

    console.log("goToBattle 메서드 실행")

    // send
    stompClient.send("/app/wait/start", {}, 
        JSON.stringify({
            'type': 'start',
            'sender': decodeToken.username,
            'content': 'true'
        })
    )

}
// 로그인한 자신의 정보 가져오기.
// session storage에서 access토큰 가져오기.
// 닉네임 정보를 가져오려 했으나, 아직 pk를 가져올 수 없어서 decodeToken의 이메일 정보를 대신 넣었다.
const accessToken = sessionStorage.getItem("accessToken");


userStore.getUserInfo(accessToken);

const decodeToken = jwtDecode(accessToken);


const email = decodeToken.username;

me.value.name = email;

function readyButton() {
    // 이곳에서 할 것.
    if(isReady.value == "false"){
        isReady.value = "true";
    }else{
        isReady.value = "false";
    }

    // send를 보내어서 상대방에게 나의 준비 변화를 알릴 것.
    // 준비 상태를 보내기 위한 다른 경로
    stompClient.send("/app/wait/ready", {},   
        JSON.stringify({
            'sender': decodeToken.username,
            'isReady' : isReady.value
        }));
}



function unLoadEvent (event) {
      if (canLeaveSite.value) return;

      event.preventDefault();
      event.returnValue = '';
    }

onMounted(() => {
    // 페이지가 mounted 될 때 웹소켓 연결
    connect()

    // 페이지가 mounted 될 때 페이지를 벗어나면 경고창을 보여주는 unLoadEvent 생성
    // window.addEventListener('beforeunload', unLoadEvent);

})

function quitButton () {
    router.push('waitBattle');
}

const inviteModalStatus = ref(false);

const openInviteModalStatus = () => {
    inviteModalStatus.value = true;
}
const closeInviteModalStatus = () => {
    inviteModalStatus.value = false;
}
</script>

<template>
    <div class="container">

        <div class="w-[400px] h-[400px] bg-white fixed left-20 top-20" v-if="inviteModalStatus">
            친구 초대
        </div>
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
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="isReady == 'false' && route.name != 'battle'">대기중</button>
                <button class="btn text-white" style="background-color: red;" @click=readyButton v-if="isReady == 'true' && route.name != 'battle'">준비완료</button>
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="route.name == 'battle'">게임중</button>
                
            </div>
            
            <div class="button-div">
                <button class="btn btn-primary w-24" style="background-color: gray;" v-if="route.name == 'waitBattle' && (isReady == 'false' || opponentReady == 'false')">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" v-if="route.name == 'waitBattle' && isReady == 'true' && opponentReady == 'true'" @click="goToBattle">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" v-if="route.name == 'battle'" @click="quitButton">
                    나가기
                </button>
            </div>
            
            <div class="player-card">
                <div class="player-img">{{ opponent.img }}</div>
                <div class="player-info-text">
                    <div>{{ opponent.name }}</div>
                    <div>현재 스코어 : {{ opponent.score }}</div>
                </div>
                <button class="btn text-white" style="background-color: gray;" v-if="opponentReady == 'false' && route.name != 'battle'">대기중</button>
                <button class="btn text-white" style="background-color: red;" v-if="opponentReady == 'true' && route.name != 'battle'">준비완료</button>
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="route.name == 'battle'">게임중</button>
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

button {
    border: 1px solid black;
    width: 30%;
    height: 100%;
}

</style>