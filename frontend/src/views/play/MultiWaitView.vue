<script setup>
import { useRoute, useRouter, onBeforeRouteLeave, onUnmounted } from 'vue-router';
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { useUserStore } from '@/stores/user';
import { usePlayStore } from '@/stores/play';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import UserCardForPlay from '@/common/UserCardForPlay.vue';
import MultiDefaultSheet from './MultiDefaultSheet.vue';
import { constructNow } from 'date-fns';
import SelectSheet from '@/common/sheet/SelectSheet.vue';
import SelectCategory from '@/common/sheet/SelectCategory.vue';
import { searchSheetsByFilter } from '@/api/sheet';

const isInCategoryView = ref(true);
// 소켓 엔드포인트 연결을 위한 주소 설정
const { VUE_APP_REQUEST_URL } = process.env;

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const playStore = new usePlayStore();

// 로그인한 유저 정보 가져오기
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const user = userStore.userInfo;

var stompClient = null;

const isReady = ref("false");
isReady.value = userStore.userReady;
const onClickStart = () => {
  if (!selectedSheetId.value) {
    alert("악보를 고르세요")
    return;
  }
    router.push({ name: "multiPlay", params: { sheetId: selectedSheetId.value } });
};

const opponentReady = ref("false");
const isInvited = ref(false);
const defaultProfileImage = require('@/assets/img/common/default_profile.png');
const roomId = ref(route.params.roomId);

const me = ref({
    userImg: null,
    name: "악카이브1",
    score: "0",
    isEmpty: true
});

watch(isReady, (newVal, oldVal) => {
    userStore.userReady = isReady.value;
});

const opponent = ref({
    userImg: null,
    nickname: "유저를 기다리는 중...", 
    score: "0",
    isEmpty: true
})

const canLeaveSite = ref(false);

function connect() {
    // local & https 
    const socket = new SockJS(VUE_APP_REQUEST_URL + '/archive-websocket', null, {
        withCredentials: true
    });
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // console.error(sessionStorage.getItem("accessToken"))

        stompClient.subscribe(`/wait/socket/{route.params.roomId}`, function (chatMessage) {
            const receiveUser = JSON.parse(chatMessage.body);
            // console.log("상대 유저 정보 구독 성공");
            // console.log(receiveUser);
            if (receiveUser.id == "profile" && opponent.value.isEmpty && user.nickname != receiveUser.nickname) {
                stompClient.send(`/app/wait/${route.params.roomId}`, { Authorization:`Bearer ${sessionStorage.getItem("accessToken")}` }, JSON.stringify({id : "profile", nickname: user.nickname}));
                opponent.value.nickname = receiveUser.nickname;
                opponent.value.isEmpty = false;
                stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender: user.nickname, isReady: isReady.value }));
                isInvited.value = true;
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
                selectedSheetId.value = message.sheetId;
                router.push({name:'multiPlay', params:{ sheetId: selectedSheetId.value }});
            }
            if(message.type == "exit"){
                opponent.value.name = "유저를 기다리는 중...";
                opponent.value.isEmpty = true;
                opponentReady.value = "false";
            }
        })

        // console.log("연결되었습니다.")

        stompClient.send(`/app/wait/${route.params.roomId}`, {}, JSON.stringify({ id : "profile", nickname : user.nickname }));
        stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender : user.nickname, isReady: isReady.value }));
    });
}

function disconnect() {
    // console.log("disconnect되었다")
}

onBeforeUnmount(() => {
    sendExit();
    window.removeEventListener('beforeunload', unLoadEvent);
})

function sendExit(){
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: 'exit', sender : user.nickname, content: 'true' }));
}

onUnmounted(() => {
    // console.log("onUnmounted 실행");
    sendExit();
})

window.addEventListener('beforeunload', sendExit);

const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose";
})

const goToBattle = () => {
    if(!selectedSheetId.value){
        alert("악보를 고르세요")
        return;
    }
    router.push({name:'multiPlay', params:{sheetId:selectedSheetId.value}});
    canLeaveSite.value = true;
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: 'start', sender: user.nickname, content: 'true', sheetId: selectedSheetId.value }));;
}

function readyButton() {
    isReady.value = isReady.value == "false" ? "true" : "false";
    stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender: user.nickname, isReady: isReady.value }));
}

function unLoadEvent (event) {
    if (canLeaveSite.value) return;
    event.preventDefault();
    event.returnValue = '';
}

// 새고로침할 때 경고창을 띄워줄 메서드
const handleBeforeUnload = (event) => {
  event.preventDefault()
  event.returnValue = ''
}

onMounted(() => {
    // 페이지가 불러와질 때, 웹소켓을 연결
    connect();
    // 새로고침할 때 경고창을 띄워줄 메서드를 window에 적용
    window.addEventListener('beforeunload', handleBeforeUnload)
    playStore.fetchOnlineUsers(); // 초대 모달을 열기 전에 온라인 유저 목록을 가져옴
})

onUnmounted(() => {
  //페이지를 빠져나갈 때, 해당 메서드를 window에서 제거
  window.removeEventListener('beforeunload', handleBeforeUnload)
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


const sheets = ref([]);
const selectedSheetId = ref();

const getPopularsheets = async () => {
	searchSheetsByFilter(
		{ sort: "POPULAR" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getNewsheets = async () => {
	searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getRandomsheets = async () => {
	searchSheetsByFilter(
		{ sort: "RANDOM" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getUserLevelsheets = async () => {
	searchSheetsByFilter(
		{ levels: [1] },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getSheetsByCategory = (sort) => {
  isInCategoryView.value = false
  if (sort == "RANDOM") getRandomsheets();
  else if (sort == "POPULAR") getPopularsheets();
  else if (sort == "LATEST") getNewsheets();
  else if (sort == "LEVEL") getUserLevelsheets();
}

const setSheetId = (sheetId) => {
  selectedSheetId.value = sheetId;
}

// 나가기 버튼 클릭 또는 뒤로가기 등 플레이 페이지가 아닌 다른 곳으로 갈 때 경고창을 띄운다.
onBeforeRouteLeave(async (to, from, next) => {
    if(to.name == 'multiPlay'){
        next();
    } else{
        const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
        if(answer){
            window.location.href = "http://localhost:3000/room/multi/list"
            await playStore.exitRoom(route.params.roomId);
        }else{
            next(false);
        }
    }
});



</script>

<template>
    <div class="flex w-full flex-col rounded-xl shadow-xl opacity-[0.8] mb-[10px] bg-red-400">
        <div class="flex w-full h-[70%] rounded-tl-xl rounded-tr-xl bg-blue-300 justify-center">
            <SelectCategory v-if="isInCategoryView" @send-sheet-category="getSheetsByCategory" />
            <SelectSheet v-else :sheets="sheets" @send-go-to-back="isInCategoryView=true" @send-sheet-id="setSheetId"/>
        </div>
        <div class="flex flex-grow w-full h-[35%] rounded-bl-xl rounded-br-xl bg-yellow-100 justify-center">
            <div class="player-card">
                <UserCardForPlay :user="user" @onClickStart="onClickStart" />
                <button class="btn text-white" style="background-color: gray;" v-if="isReady == 'false' && route.name == 'multiWait'" @click="readyButton">대기중</button>
                <button class="btn text-white" style="background-color: red;"  v-if="isReady == 'true' && route.name == 'multiWait'" @click="readyButton">준비완료</button>
                <!-- <button class="btn text-white" style="background-color: gray;" v-if="route.name == 'multiPlay'">게임중</button> -->
            </div>

            <div class="button-div">
                <button class="btn btn-primary w-24" style="background-color: gray;" v-if="isReady == 'false' || opponentReady == 'false'">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" v-if="isReady == 'true' && opponentReady == 'true'" @click="goToBattle">
                    시작하기
                </button>
                <button class="btn btn-primary w-24" @click="quitButton">
                    나가기
                </button>
            </div>

            <div class="player-card">
                <UserCardForPlay :user="opponent"/>
                <button class="btn text-white" style="background-color: gray;" v-if="opponentReady == 'false' && route.name == 'multiWait'">대기중</button>
                <button class="btn text-white" style="background-color: red;"  v-if="opponentReady == 'true' && route.name == 'multiWait'">준비완료</button>
                <!-- <button class="btn text-white" style="background-color: gray;" v-if="route.name == 'multiPlay'">게임중</button> -->
                <button class="btn text-white" style="background-color: gray;" v-if="isInvited == false" @click="openInviteModalStatus">친구 초대하기</button>
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