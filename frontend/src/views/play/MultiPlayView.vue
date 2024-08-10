<script setup>
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { useMusicStore } from "@/stores/sheet";
import { watch } from "vue";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { computed } from "vue";
import { ref } from "vue";

const musicStore = useMusicStore();
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
var stompClient = null;
const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();

// user의 정보를 accessToken을 이용해 가져온다.
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;

// pinia에 저장되어 있는 상대방의 정보를 가져온다.
const opponentUser = userStore.opponentUser;

//
const opponentF1Score = ref(0);
const opponentJaccardScore = ref(0);

function connect() {
    // local & https
    const socket = new SockJS(
        VUE_APP_REQUEST_URL + "/archive-websocket",
        null,
        {
            withCredentials: true,
        }
    );
    stompClient = Stomp.over(socket);
    stompClient.connect(
        {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`,
        },
        function (frame) {

          stompClient.subscribe(`/play/socket/${route.params.roomId}`, function(scoreSocket){
            const scoreData = JSON.parse(scoreSocket.body);
            if(scoreData.nickname != loginUser.nickname){
              opponentF1Score.value = scoreData.f1Score;
              opponentJaccardScore.value = scoreData.jaccardScore;
            }
          });

          stompClient.subscribe(`/play/start/socket/${route.params.roomId}`, function(Socket){
            const message = JSON.parse(socket.body);
            if(loginUser.nickname != message){
              startRecording();
            }
          })

        }
    );
}

// 플레이 점수를 가져온다.
const myF1Score = computed(() => {
    if (musicStore.f1.length === 0) return 0;
    const averageScore =
        musicStore.f1.reduce((acc, score) => acc + score, 0) /
        musicStore.f1.length;
    return Math.floor(averageScore * 100);
});

const myJaccardScore = computed(() => {
    if (musicStore.jaccard.length === 0) return 0;
    const averageScore =
        musicStore.jaccard.reduce((acc, score) => acc + score, 0) /
        musicStore.jaccard.length;
    return Math.floor(averageScore * 100);
});

// jaccardScore의 값이 변경될 때마다 실행될 watch
watch(myJaccardScore, (newScore, oldScore) => {
    // 소켓 send 메서드 실행;
    stompClient.send(`/app/play/${route.params.roomId}`, {}, JSON.stringify({nickname: loginUser.nickname, f1Score: myF1Score, jaccardScore: myJaccardScore}));
});

// Sheet.vue에서 녹음 버튼을 클릭했을 때, 호출되는 메서드
const onStartRecordingEmit = () => {
  stompClient.send(`/app/play/start/${route.params.roomId}`, {}, JSON.stringify(loginUser.nickname));
  startRecording();
}

const onClickQuit = () => {
    router.push("/room/multi/list");
};

console.log("route.params.sheetId = " + route.params.sheetId);
</script>
<template>
    <div class="container">
        <div class="up">
            <Sheet :sheetId="route.params.sheetId" height="95" @startRecordingEmit="onStartRecordingEmit"/>
        </div>
        <div class="down">
            <!-- 본인 프로필을 표시하는 컴포넌트 -->
            <UserCardForPlay :user="loginUser" @onClickStart="onClickStart" :f1Score="myF1Score" :jaccardScore="myJaccardScore" />
            <button class="btn btn-primary w-24" @click="onClickQuit">
                나가기
            </button>
            <!-- 상대방 프로필을 표시하는 컴포넌트 -->
            <UserCardForPlay
                :user="opponentUser"
                @onClickStart="onClickStart"
                :f1Score="opponentF1Score"
                :jaccardScore="opponentJaccardScore"
            />
        </div>
    </div>
</template>
<style scoped>
.container {
    margin: 10px auto;
    width: 90vw;
    height: 90vh;
    background-color: #f0f0f0;
    border-radius: 15px;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    opacity: 0.8;
}
.up {
    background-color: #fff;
    height: 72%;
    margin-bottom: 20px;
    padding: 20px;
    border-radius: 15px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.down {
    display: flex;
    justify-content: center;
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
