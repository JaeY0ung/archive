<script setup>
import { onBeforeRouteLeave, useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { useMusicStore } from "@/stores/sheet";
import { onBeforeUnmount, watch } from "vue";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { computed } from "vue";
import { ref } from "vue";
import { onMounted } from "vue";
import { localAxios } from "@/util/http-common";
import { usePlayStore } from "@/stores/play";

const playStore = usePlayStore();
const { VUE_APP_REQUEST_URL } = process.env; // 소켓 엔드포인트 연결을 위한 주소 설정
const musicStore = useMusicStore();
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
var stompClient = null;
const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const isQuitting = ref(false);
const isPopstate = ref(false);
const isReloading = ref(false);
let multiResultId = 0;
let multi_result_id = -2;
const local = localAxios();
let isLastSender = false;
let isLastRecieved = false;
let isRequested = false;
let opponentScore = 0;
const myF1Score = ref(0);
const myJaccardScore = ref(0);
let isResultSender = false;
let isPlayBehind = false;


// sheet.js에서 올바른 경로로 보낼 수 있도록 mode를 지정해준다.
// back에서 single 처럼 경로를 지정해주는 MultiPlayController의 메서드가 필요함.
musicStore.playMode = "multi";

// user의 정보를 accessToken을 이용해 가져온다.
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;

// pinia에 저장되어 있는 상대방의 정보를 가져온다.
const opponentUser = userStore.opponentUser;
opponentUser.userImg = opponentUser.userImg.split(',')[1];
console.log("======================")
console.log(loginUser.userImg);
console.log(opponentUser.userImg);

const opponentF1Score = ref(0);
const opponentJaccardScore = ref(0);

// Sheet.vue에서 녹음 버튼을 클릭했을 때, 호출되는 메서드
const onStartRecordingEmit = () => {
    stompClient.send(`/app/play/start/${route.params.roomId}`, {}, JSON.stringify({type: "start", sender: loginUser.nickname, content: "start", sheetId: route.params.sheetId, resultId: multiResultId}));
    startRecording();
}

// 유저가 방을 나갔음을 알려주는 소켓 메서드, 해당 vue에서도 이를 받는 subscribe 메서드가 필요하다.
// refactoring은 나중에.
const  sendExit = () => {
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: "exit", sender : loginUser.nickname, content: 'true', sheetId: 0 }));
}

const sendEndDuringPlay = () => {
    stompClient.send(`/app/play/end/quit/${route.params.roomId}`, {}, JSON.stringify({sender: loginUser.nickname, score: myJaccardScore, multiResultId: multiResultId}));
    musicStore.f1Score = [];
    musicStore.jaccardScore = [];
}

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
            // 상대방의 점수를 받는 부분 
            const scoreData = JSON.parse(scoreSocket.body);
            if(scoreData.nickname != loginUser.nickname){
              opponentF1Score.value = scoreData.f1Score;
              opponentJaccardScore.value = scoreData.jaccardScore;
            }
            // 상대방의 점수를 받았을 때, isLast가 1이라면(채점이 모두 끝났다면), update한다.
            if(musicStore.isLast == true){
                const myScore = parseFloat(myJaccardScore.value);
                const otherScore = parseFloat(opponentJaccardScore.value);
                local.patch(`/plays/multi/${multi_result_id}`, {
                myUserId: loginUser.id,
                myScore: myScore,
                otherUserId: opponentUser.nickname,
                otherScore: otherScore
                }).then(response => {
                    isRequested = true;
                }).catch(error => {
                console.log("멀티 플레이 데이터 업데이트 중 오류 발생")
                });
            }
          });

          stompClient.subscribe(`/play/start/socket/${route.params.roomId}`, async (socket) => {
            const message = JSON.parse(socket.body);
            if(loginUser.nickname != message.sender){
            startRecording();
            isPlayBehind = true;
            // 결과 아이디 최신화.
            if(message.resultId != 0){
                // multiResultId = message.resultId;
                isLastSender = true;
            }
            try{
                const response = await local.post("/plays/multi" , {
                sheetId: route.params.sheetId
                },{
                        withCredentials: true
                    });
                
                multiResultId = response.data
                multi_result_id = response.data;
                musicStore.singleResultId = multi_result_id
                stompClient.send(`/app/play/id/${route.params.roomId}`, {}, JSON.stringify({sender: loginUser.nickname, resultId: multi_result_id}))
                isResultSender = true;
                // sheet.js에서 중간 점수를 보낼 때, multiResultId를 같이 보낼 수 있도록 sheet.js 저장
                // musicStore.singleResultId = multiResultId;
                // sheet store에 multiResultId 저장
                }catch(error){
                    console.log("멀티 플레이 데이터 저장 중 오류 발생");
                }

            }
          });

          stompClient.subscribe(`/play/id/socket/${route.params.roomId}`, (socket) => {
            const message = JSON.parse(socket.body);
            if(message.sender != loginUser.nickname){
                multi_result_id = message.resultId;
                // 스토어에 저장
                musicStore.singleResultId = multi_result_id;
            }
          })

        stompClient.subscribe(`/wait/socket/start/${route.params.roomId}`, function(socket){
            const message = JSON.parse(socket.body);
            userStore.opponentUser.nickname = "유저가 방을 나갔습니다."
            userStore.opponentUser.userImg = null
            opponentF1Score.value = 0;
            opponentJaccardScore.value = 0;
        })

        });
}



// watch를 사용하여 f1 배열의 변화를 감지하고 myF1Score를 업데이트
watch(
    () => musicStore.f1,
    (newF1Scores) => {
        if (newF1Scores.length !== 0) {
            myF1Score.value = Math.floor(
                (newF1Scores.reduce((acc, score) => acc + score, 0) /
                    newF1Scores.length) * 100
            );
        } else {
            myF1Score.value = 0;
        }
    },
    { deep: true } // 배열 내부의 변화도 감지
);

// watch를 사용하여 jaccard 배열의 변화를 감지하고 myJaccardScore를 업데이트
watch(
    () => musicStore.jaccard,
    (newJaccardScores) => {
        if (newJaccardScores.length !== 0) {
            myJaccardScore.value = Math.floor(
                (newJaccardScores.reduce((acc, score) => acc + score, 0) /
                    newJaccardScores.length) * 100
            );
        } else {
            myJaccardScore.value = 0;
        }
        // 변화된 점수를 상대방에게 전송하는 소켓 메서드
        stompClient.send(`/app/play/${route.params.roomId}`, {}, JSON.stringify({nickname: loginUser.nickname, f1Score: myF1Score.value, jaccardScore: myJaccardScore.value}));
    },
    { deep: true } // 배열 내부의 변화도 감지
);

// 악보를 끝까지 완주했을 때, 호출되는 메서드
// Todo: 모달창으로 성공, 실패를 알려줄 것.
watch(() => musicStore.isLast,
  (Last) => {
    if(isLastSender){
        stompClient.send(`/app/play/end/${route.params.roomId}`, {}, JSON.stringify(
        {
            sender: loginUser.nickname,
            score: myJaccardScore.value,
            multiResultId: multiResultId
        })
)}else{
    
    
}});

const onClickQuit = () => {
    isQuitting.value = true;
    router.push("/room/multi/list");
}


const handleBeforeUnload = async () => {

    // 유저가 배틀 페이지를 벗어나면 waiting으로 전환.
    playStore.changeStatus(route.params.roomId, "waiting");

    if(isQuitting.value || isPopstate.value || isReloading.value){
        musicStore.f1Score = [];
        musicStore.jaccardScore = [];
        musicStore.isLast = 0;
    } 
    else 
    {
        await playStore.exitRoom(route.params.roomId);
        musicStore.f1Score = [];
        musicStore.jaccardScore = [];
        userStore.opponentUser.nickname = "";
        userStore.opponentUser.userImg = null;
        musicStore.isLast = 0;
        sendExit();
        sendEndDuringPlay();
    }
    musicStore.isLast = 0;
    musicStore.f1Score = [];
    musicStore.jaccardScore = [];}
;

onMounted(() => {
    connect();
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정
    window.addEventListener('popstate', () => {
    isPopstate.value = true;
    });
    // 페이지 내부의 ui를 사용해서 이동하는 것이 아닌, 주소창 입력 또는 탭 종료 같은 외부 방법으로 이동할 때
    // 상대방에게 나갔음을 알리는 것.
    window.addEventListener('beforeunload', handleBeforeUnload);
    musicStore.f1Score = [];
    musicStore.jaccardScore = [];
})

onBeforeUnmount(()=>{
    stompClient.disconnect();
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정 해제
    window.addEventListener('popstate', () => {
    isPopstate.value = true;
    });
    window.removeEventListener('beforeunload', handleBeforeUnload);
})

onBeforeRouteLeave( async (to, from, next) => {
    musicStore.isLast = 0;
    if(to.name == from.name){
        isReloading.value = true;
    }

    if (to.name == 'multiPlay') {
    next();
    } else {
    const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
    if (answer) {
        if(musicStore.isRecording){
            sendEndDuringPlay();
        }
        musicStore.f1Score = [];
        musicStore.jaccardScore = [];
        sendExit();
        await playStore.exitRoom(route.params.roomId);
        musicStore.f1Score = [];
        musicStore.jaccardScore = [];
        userStore.opponentUser.nickname = "";
        userStore.opponentUser.userImg = null;
        next();
    } else {
        // isExiting.value = false;
        next(false);
    }
    }
    
})



</script>
<template>
    <div class="container">
        <div class="up">
            <Sheet :sheetId="route.params.sheetId" height="95" @startRecordingEmit="onStartRecordingEmit"/>
        </div>
        <div class="down"
        :style="{
                    backgroundImage: `url(${require('@/assets/img/sheet_play/ground.png')})`,
                    backgroundBlendMode: 'multiply', 
                    backgroundColor: 'rgba(0, 0, 0, 0.2)', 
                    backgroundSize: '100% 100%', 
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat' }"
        >
            <!-- 본인 프로필을 표시하는 컴포넌트 -->
            <UserCardForPlay :user="loginUser" @onClickStart="onClickStart" :f1Score="myF1Score" :jaccardScore="myJaccardScore" />
            <button class="btn  w-24" @click="onClickQuit"
            :style="{
              backgroundImage: `url(${require('@/assets/img/sheet_play/box_pink.png')})`,
              backgroundSize: '100% 100%', // 배경 이미지가 요소에 딱 맞게 조정됨
              backgroundPosition: 'center',
              backgroundRepeat: 'no-repeat' }"
            >
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
