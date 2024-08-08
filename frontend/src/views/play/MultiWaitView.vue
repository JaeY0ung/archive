<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, computed, onMounted, onBeforeUnmount, onUnmounted, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { usePlayStore } from '@/stores/play';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import UserCardForPlay from '@/common/UserCardForPlay.vue';
import MultiDefaultSheet from './MultiDefaultSheet.vue';
import { constructNow } from 'date-fns';

// 소켓 엔드포인트 연결을 위한 주소 설정
const { VUE_APP_REQUEST_URL } = process.env;

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const playStore = new usePlayStore();

var stompClient = null;

const isReady = ref("false");
isReady.value = userStore.userReady;
console.log("================================================")
console.log("isReady : " + isReady.value);
const opponentReady = ref("false");
const isInvited = ref("false");
const defaultProfileImage = require('@/assets/img/common/default_profile.png');
const roomId = ref(route.params.roomId);

const me = ref({
    img: defaultProfileImage,
    name: "악카이브1",
    score: "0",
    isEmpty: true
});

watch(() => isReady.value, (newVal, oldVal) => {
    userStore.userReady = isReady.value;
});

const opponent = ref({
    img: defaultProfileImage,
    name: "유저를 기다리는 중...", 
    score: "0",
    isEmpty: true
})

const canLeaveSite = ref(false);

function connect() {
    // local & https 
    var socket = new SockJS(VUE_APP_REQUEST_URL + '/archive-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        stompClient.subscribe(`/wait/socket/{route.params.roomId}`, function (chatMessage) {
            const userLogin = JSON.parse(chatMessage.body);
            if (userLogin.id == "profile" && opponent.value.isEmpty && user.nickname != userLogin.email) {
                stompClient.send(`/app/wait/${route.params.roomId}`, {}, JSON.stringify({ 'id': "profile", 'email': user.nickname }));
                opponent.value.name = userLogin.email;
                opponent.value.isEmpty = false;
                stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ 'sender': user.nickname, 'isReady': isReady.value }));
                isInvited.value = "true";
            }
        });

        stompClient.subscribe(`/wait/socket/ready/{route.params.roomId}`, function (readyStatus) {
            const playerReady = JSON.parse(readyStatus.body);
            if(playerReady.sender != user.nickname){
                opponentReady.value = playerReady.isReady;
            }
        })

        stompClient.subscribe(`/wait/socket/start/${route.params.roomId}`, function(socket){
            const message = JSON.parse(socket.body);
            if(message.type == "start" && message.content == "true"){
                router.push({name:'play'});
            }
            if(message.type == "exit"){
                opponent.value.name = "유저를 기다리는 중...";
                opponent.value.isEmpty = true;
                opponentReady.value = "false";
            }
        })

        stompClient.send(`/app/wait/${route.params.roomId}`, {}, JSON.stringify({ 'id': "profile", 'email': user.nickname }));
        stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ 'sender': user.nickname, 'isReady': isReady.value }));
    });
}

function disconnect() {
    console.log("disconnect되었다")
}

onBeforeUnmount(() => {
    sendExit();
    window.removeEventListener('beforeunload', unLoadEvent);
})

function sendExit(){
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ 'type': 'exit', 'sender': user.nickname, 'content': 'true' }));
}

onUnmounted(() => {
    console.log("onUnmounted 실행");
    sendExit();
})

window.addEventListener('beforeunload', sendExit);

const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose";
})

const goToBattle = () => {
    router.push({name:'play'});
    canLeaveSite.value = true;
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ 'type': 'start', 'sender': user.nickname, 'content': 'true' }));
}

// 로그인한 유저 정보 가져오기
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const user = userStore.userInfo;

function readyButton() {
    isReady.value = isReady.value == "false" ? "true" : "false";
    stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ 'sender': user.nickname, 'isReady': isReady.value }));
}

function unLoadEvent (event) {
    if (canLeaveSite.value) return;
    event.preventDefault();
    event.returnValue = '';
}

onMounted(() => {
    connect();
    playStore.fetchOnlineUsers(); // 초대 모달을 열기 전에 온라인 유저 목록을 가져옴
})

function quitButton () {
    router.push('/room/multi/list');
}

const inviteModalStatus = ref(false);
const selectedFriend = ref(null);

const openInviteModalStatus = () => {
    console.log("ONLINE ::::::: ",onlineUsers.value)
    inviteModalStatus.value = true;
}

const closeInviteModalStatus = () => {
    inviteModalStatus.value = false;
}


const toggleFriendSelection = (user) => {
    if (selectedFriend.value && selectedFriend.value.id === user.id) {
        selectedFriend.value = null;
    } else {
        selectedFriend.value = user;
    }
}

const isFriendSelected = (user) => {
    return selectedFriend.value && selectedFriend.value.id === user.id;
}

const currentMode = computed(() => playStore.getMode);
const onlineUsers = computed(() => playStore.getOnlineUsers);

const inviteSelectedFriends = async () => {
    if (selectedFriend.value) {
        // 친구 초대 알림 보내기
         console.log("초대 알림 방 ID: " + roomId.value)
        await playStore.sendInviteAlert(selectedFriend.value.id, roomId.value);
        console.log("Invite selected friend:", selectedFriend.value);
    }
    closeInviteModalStatus();
}

console.log("isReady : " , isReady.value);
</script>

<template>
    <div class="flex w-full flex-col rounded-xl shadow-xl opacity-[0.8] mb-[10px] bg-red-400">
        <div class="flex w-full h-[70%] rounded-tl-xl rounded-tr-xl bg-blue-300 justify-center">
            <MultiDefaultSheet />
        </div>
        <div class="flex flex-grow w-full h-[35%] rounded-bl-xl rounded-br-xl bg-yellow-100 justify-center">
            <div class="player-card">
                <UserCardForPlay :user="user" @onClickStart="onClickStart" />
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="isReady == 'false' && route.name != 'multiPlay'">대기중</button>
                <button class="btn text-white" style="background-color: red;" @click=readyButton v-if="isReady == 'true' && route.name != 'play'">준비완료</button>
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="route.name == 'play'">게임중</button>

            </div>
            <div class="button-div">
                <button class="btn btn-primary w-24" style="background-color: gray;" v-if="route.name == 'multiDefault' && (isReady == 'false' || opponentReady == 'false')">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" v-if="route.name == 'multiDefault' && isReady == 'true' && opponentReady == 'true'" @click="goToBattle">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" @click="quitButton">
                    나가기
                </button>
            </div>
            <div class="player-card">
                <UserCardForPlay :user="user"/>
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="isReady == 'false' && route.name != 'play'">대기중</button>
                <button class="btn text-white" style="background-color: red;" @click=readyButton v-if="isReady == 'true' && route.name != 'play'">준비완료</button>
                <button class="btn text-white" style="background-color: gray;" @click=readyButton v-if="route.name == 'play'">게임중</button>
                <button class="btn text-white" style="background-color: gray;" @click="openInviteModalStatus" v-if="isInvited == 'false'">친구 초대하기</button>
            </div>
        </div>


        <div v-if="inviteModalStatus" class="invite-modal">
            <div class="modal-content">
                <h2 class="modal-title">친구 초대하기</h2>
                <ul>
                    <li v-for="user in onlineUsers" :class="{ selected: isFriendSelected(user) }" @click="toggleFriendSelection(user)">
                        <img :src="user.userImg ? user.userImg : defaultProfileImage" alt="User Image" />
                        <span>{{ user.nickname }}</span>
                        <span>{{ user.singleScore }}</span>
                    </li>
                </ul>
                <div class="modal-button">
                    <button @click="inviteSelectedFriends" >선택한 친구 초대하기</button>
                    <button @click="closeInviteModalStatus">닫기</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

.down {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 25vh;
}

.button-div {
    display: flex;
    justify-content: center;
    align-items: center;
}

.player-card {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 20px;
    background-color: #fff;
    border: 1px solid #ccc;
    border-radius: 10px;
    width: 40%;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.player-img {
    width: 100px;
    height: 100px; 
    background-color: white;
    border-radius: 50%;
    margin-bottom: 10px;
}

.player-info-text {
    text-align: center;
    margin-bottom: 10px;
}

button {
    border: 1px solid black;
    width: 100px;
    height: 40px;
    margin: 5px;
    background-color: #2196f3;
    color: white;
    border-radius: 5px;
    cursor: pointer;
}

.invite-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-title{
    padding-bottom: 20px;
}

.modal-button {
    display: flex;
    justify-content: space-between;
}

.modal-content {
    background: white;
    padding: 20px;
    border-radius: 10px;
    width: 50vw;
    text-align: center;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.modal-content ul {
    list-style: none;
    padding: 0;
}

.modal-content li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
}

.modal-content li.selected {
    background-color: #ccc;
}

.modal-content li img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 10px;
}

.modal-content .status {
    padding: 5px 10px;
    border-radius: 5px;
}

.modal-content .status.online {
    background-color: green;
    color: white;
}

.modal-content .status.busy {
    background-color: red;
    color: white;
}

.modal-content .status.offline {
    background-color: gray;
    color: white;
}
</style>