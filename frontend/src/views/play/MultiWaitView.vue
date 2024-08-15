<script setup>
import { useRoute, useRouter, onBeforeRouteLeave, } from 'vue-router';
import { ref, computed, onMounted, watch, onBeforeUnmount, onUnmounted } from 'vue';
import { useUserStore } from '@/stores/user';
import { usePlayStore } from '@/stores/play';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import UserCardForPlay from '@/common/UserCardForPlay.vue';
import SelectSheet from '@/common/sheet/SelectSheet.vue';
import SelectCategory from '@/common/sheet/SelectCategory.vue';
import { searchSheetsByFilter } from '@/api/sheet';

const isInCategoryView = ref(true);
const { VUE_APP_REQUEST_URL } = process.env; // 소켓 엔드포인트 연결을 위한 주소 설정
const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const playStore = new usePlayStore();
const sheets = ref([]);
const selectedSheetId = ref();
const currentMode = computed(() => playStore.getMode);
const onlineUsers = computed(() => playStore.getOnlineUsers);
const isQuitting = ref(false);
const isPopstate = ref(false);
const isReloading = ref(false);
const isReady = ref("false");
const opponentReady = ref("false");
const isInvited = ref(false);
const defaultProfileImage = require('@/assets/img/common/default_profile.png');
const roomId = ref(route.params.roomId);
var stompClient = null;

// 로그인한 유저 정보 가져오기
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const user = userStore.userInfo;


// isReady.value = userStore.userReady;
const onClickStart = () => {
    if (!selectedSheetId.value) {
        alert("악보를 고르세요")
        return;
    }
    router.push({ name: "multiPlay", params: { roomId: route.params.roomId, sheetId: selectedSheetId.value } });
};


const me = ref({
    userImg: null,
    nickname: "악카이브1",
    score: "0",
    isEmpty: true
});

watch(isReady, (newVal, oldVal) => {
    userStore.userReady = isReady.value;
});

const opponent = ref({
    userImg: null,
    nickname: "상대를 기다리는 중...",
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
    // stompClient.debug = null;   
    stompClient.connect({
        "Content-Type": "application/json",
        "Authorization": `Bearer ${accessToken}`
    }, function (frame) {
        // console.error(sessionStorage.getItem("accessToken"))

        stompClient.subscribe(`/wait/socket/${route.params.roomId}`, function (chatMessage) {
            const receiveUser = JSON.parse(chatMessage.body);
            if (receiveUser.id == "profile" && opponent.value.isEmpty && user.nickname != receiveUser.nickname) {
                stompClient.send(`/app/wait/${route.params.roomId}`, {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${accessToken}`
                }, JSON.stringify({ id: "profile", nickname: user.nickname, userImg: user.userImg }));

                // 상대방의 유저 정보를 저장.
                opponent.value.nickname = receiveUser.nickname;
                opponent.value.isEmpty = false;
                opponent.value.userImg = receiveUser.userImg;
                userStore.opponentUser.nickname = receiveUser.nickname;
                userStore.opponentUser.userImg = receiveUser.userImg;
                stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender: user.nickname, isReady: isReady.value }));
                isInvited.value = true;
            }
        });

        stompClient.subscribe(`/wait/socket/ready/${route.params.roomId}`, function (readyStatus) {
            const playerReady = JSON.parse(readyStatus.body);
            if (playerReady.sender != user.nickname) {
                opponentReady.value = playerReady.isReady;
            }
        })

        stompClient.subscribe(`/wait/socket/start/${route.params.roomId}`, function (socket) {
            const message = JSON.parse(socket.body);
            if (message.type == "start" && message.content == "true") {
                selectedSheetId.value = message.sheetId;
                router.push({ name: 'multiPlay', params: { roomId: route.params.roomId, sheetId: selectedSheetId.value } });
            }
            if (message.type == "exit") {
                opponent.value = {
                    userImg: defaultProfileImage,
                    nickname: "상대를 기다리는 중....",
                    score: "0",
                    isEmpty: true
                }
                opponentReady.value = "false";
            }
        })

        stompClient.send(`/app/wait/${route.params.roomId}`, {}, JSON.stringify({ id: "profile", nickname: user.nickname, userImg: user.userImg }));
        stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender: user.nickname, isReady: isReady.value }));
    });
}


function sendExit() {
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: "exit", sender: user.nickname, content: 'true', sheetId: 0 }));
    userStore.userReady = "false";
}



const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose";
})

const goToBattle = () => {
    if (!selectedSheetId.value) {
        alert("악보를 고르세요")
        return;
    }
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: 'start', sender: user.nickname, content: 'true', sheetId: selectedSheetId.value }));;
    router.push({ name: 'multiPlay', params: { roomId: route.params.roomId, sheetId: selectedSheetId.value } });
    canLeaveSite.value = true;
}

function readyButton() {
    isReady.value = isReady.value == "false" ? "true" : "false";
    stompClient.send(`/app/wait/ready/${route.params.roomId}`, {}, JSON.stringify({ sender: user.nickname, isReady: isReady.value }));
}





const inviteModalStatus = ref(false);
const selectedFriend = ref(null);

const openInviteModalStatus = () => {
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



const inviteSelectedFriends = async () => {
    if (selectedFriend.value) {
        // 친구 초대 알림 보내기
        await playStore.sendInviteAlert(selectedFriend.value.id, roomId.value);
    }
    closeInviteModalStatus();
}

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

function quitButton() {
    isQuitting.value = true;
    router.push('/room/multi/list');
}

const handleBeforeUnload = async () => {

    if (isQuitting.value || isPopstate.value || isReloading.value) {
    } else {
        userStore.opponentUser.nickname = "";
        userStore.opponentUser.userImg = null;
        await playStore.exitRoom(route.params.roomId);
        sendExit();
    }
};


onMounted(() => {
    // isReady.value = userStore.userReady;
    isReady.value = "false";
    connect();
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정
    window.addEventListener('popstate', () => {
        isPopstate.value = true;
    });
    window.addEventListener('beforeunload', handleBeforeUnload);
    playStore.fetchOnlineUsers(); // 초대 모달을 열기 전에 온라인 유저 목록을 가져옴

});

onBeforeUnmount(() => {
    window.removeEventListener('beforeunload', handleBeforeUnload);
    window.removeEventListener('popstate', () => {
        isPopstate.value = true;
    });
});

onBeforeRouteLeave(async (to, from, next) => {

    if (to.name == from.name) {
        isReloading.value = true;
    }

    if (to.name == 'multiPlay') {
        playStore.changeStatus(route.params.roomId, "playing");
        next();
    } else {
        const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
        if (answer) {
            sendExit();
            await playStore.exitRoom(route.params.roomId);
            userStore.opponentUser.nickname = "";
            userStore.opponentUser.userImg = null;
            next();
        } else {
            // isExiting.value = false;
            next(false);
        }
    }
});



</script>

<template>
    <div
        class="flex flex-col flex-grow w-full items-center justify-center h-[calc(100vh-80px)] overflow-hidden py-4 rounded-xl">
        <div
            class="flex flex-col w-full max-w-[95%] h-full max-h-[95%] rounded-xl shadow-xl relative bg-[#C0DBFE]">

            <!-- 위 -->
            <div class="relative w-full h-[65vh] rounded-t-xl  shadow-2xl overflow-hidden">
                <div class="absolute inset-0 bg-cover bg-center opacity-70"></div>
                <SelectCategory v-if="isInCategoryView" class="absolute inset-0 overflow-auto opacity-80"
                    @send-sheet-category="getSheetsByCategory" />
                <SelectSheet v-else class="absolute inset-0 overflow-auto" :sheets="sheets"
                    @send-go-to-back="isInCategoryView = true" @send-sheet-id="setSheetId" />
            </div>
            <!-- 아래 -->
            <div class="flex flex-grow  w-full  rounded-bl-xl rounded-br-xl justify-between px-4 inset-0 bg-cover bg-center opacity-70">
                <!-- 로그인 유저(나) -->
                <div class="w-[35%] h-full flex gap-2 justify-center items-center">
                
                    <UserCardForPlay :user="user" @onClickStart="onClickStart" class="custom-shadow  h-[90%] w-[40vw]" />
                    <button
                        class=" h-[90%] w-[40%] custom-shadow bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        v-if="isReady == 'false' && route.name == 'multiWait'" @click="readyButton">
                        대기중</button>
                    <button class=" h-[90%] w-[40%] custom-shadow bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        v-if="isReady == 'true' && route.name == 'multiWait'" @click="readyButton">준비완료</button>
                </div>
                <!-- 시작하기 + 나가기 -->
                <div class="button-div w-[20%] h-full flex flex-col items-centeer justify-center gap-2">
                    <button  class="custom-shadow_ver3 w-full h-[40%] text-[#4A90E2] flex justify-center items-center text-2xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer" v-if="isReady == 'false' || opponentReady == 'false'">
                        시작하기
                    </button>
                    <button class="custom-shadow_ver2 w-full h-[40%] text-[#f3f7fd] flex justify-center items-center text-2xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        v-if="isReady == 'true' && opponentReady == 'true'" @click="goToBattle">
                        시작하기
                    </button>
                    <button class="custom-shadow_ver2 w-full h-[40%] text-[#f3f7fd] flex justify-center items-center text-2xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        @click="quitButton">
                        나가기
                    </button>
                </div>

                <!-- 상대 유저 -->
                <div class="player-card w-[35%] flex justify-center gap-2 items-center">
                    <UserCardForPlay :user="opponent" class="custom-shadow  h-[90%] w-[40vw]" />

                    <!-- 친구 초대하기 버튼: 대결 상대가 없고, 초대되지 않았을 때만 나타남 -->
                    <button
                        class=" h-[90%] w-[40%] custom-shadow bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer "
                        v-if="!isInvited" @click="openInviteModalStatus">
                        상대 초대
                    </button>

                    <!-- 대기중 버튼: 대결 상대가 있고, 아직 준비가 안 된 상태일 때 나타남 -->
                    <button class=" h-[90%] w-[40%] custom-shadow bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer" 
                    v-if="isInvited && opponentReady == 'false' && route.name == 'multiWait'">
                        대기중
                    </button>

                    <!-- 준비완료 버튼: 대결 상대가 있고, 준비가 완료된 상태일 때 나타남 -->
                    <button class=" h-[90%] w-[40%] custom-shadow bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                    v-if="isInvited && opponentReady == 'true' && route.name == 'multiWait'">
                        준비완료
                    </button>
                </div>
            </div>


            <div v-if="inviteModalStatus" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-60">
                <div class="bg-white -2xl shadow-lg p-6 w-[400px]">
                <h2 class="text-2xl font-bold text-[#333333] mb-6">친구 초대하기</h2>
                <ul>
                    <li
                    v-for="user in onlineUsers"
                    :key="user.id"
                    @click="toggleFriendSelection(user)"
                    :class="{
                        'bg-blue-100 border-blue-500': isFriendSelected(user),
                        'bg-gray-50 hover:bg-blue-50': !isFriendSelected(user)
                    }"
                    class="flex items-center justify-between p-4 mb-2 border rounded-lg cursor-pointer transition-all duration-200"
                    >
                    <div class="flex items-center">
                        <img :src="user.userImg ? `data:image/jpeg;base64,${user.userImg}` : defaultProfileImage" alt="User Image" class="w-12 h-12 rounded-full mr-4 object-cover" />
                        <span class="text-lg font-medium text-[#333333]">{{ user.nickname }}</span>
                    </div>
                    <div class="text-sm text-gray-500">{{ user.singleScore }}</div>
                    </li>
                </ul>
                <div class="mt-6 flex justify-end gap-4">
                    <button @click="inviteSelectedFriends" class="bg-[#4A90E2] text-white font-bold py-2 px-4 rounded-xl shadow hover:bg-blue-600 transition-colors duration-200">
                    초대하기
                    </button>
                    <button @click="closeInviteModalStatus" class="bg-gray-200 text-[#333333] font-bold py-2 px-4 rounded-xl shadow hover:bg-gray-300 transition-colors duration-200">
                    닫기
                    </button>
                </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.custom-shadow {
    @apply rounded-xl bg-white;
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
}

.custom-shadow_ver2 {
    @apply rounded-xl bg-[#4A90E2];
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
}

.custom-shadow_ver3 {
    @apply rounded-xl bg-gray-100;
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
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

.modal-title {
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