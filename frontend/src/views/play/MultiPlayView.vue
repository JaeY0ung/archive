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
const local = localAxios();
const isLastSender = false;
const isLastRecieved =false;
const isRequested = false;
let opponentScore = 0;
const myF1Score = ref(0);
const myJaccardScore = ref(0);

// sheet.js에서 올바른 경로로 보낼 수 있도록 mode를 지정해준다.
// back에서 single 처럼 경로를 지정해주는 MultiPlayController의 메서드가 필요함.
musicStore.playMode = "multi";

// user의 정보를 accessToken을 이용해 가져온다.
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;

// pinia에 저장되어 있는 상대방의 정보를 가져온다.
const opponentUser = userStore.opponentUser;
const opponentF1Score = ref(0);
const opponentJaccardScore = ref(0);

// Sheet.vue에서 녹음 버튼을 클릭했을 때, 호출되는 메서드
const onStartRecordingEmit = () => {
    console.log("onStartRocordingEmit 실행")
    console.log(route.params.roomId);
    stompClient.send(`/app/play/start/${route.params.roomId}`, {}, JSON.stringify({type: "start", sender: loginUser.nickname, content: "start", sheetId: route.params.sheetId}));
    startRecording();
}

// 유저가 방을 나갔음을 알려주는 소켓 메서드, 해당 vue에서도 이를 받는 subscribe 메서드가 필요하다.
// refactoring은 나중에.
const  sendExit = () => {
    stompClient.send(`/app/wait/start/${route.params.roomId}`, {}, JSON.stringify({ type: "exit", sender : loginUser.nickname, content: 'true', sheetId: 0 }));
}

const sendEndDuringPlay = () => {
    stompClient.send(`/app/play/end/quit/${route.params.roomId}`, {}, JSON.stringify({sender: loginUser.nickname, score: myJaccardScore}));
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
            const scoreData = JSON.parse(scoreSocket.body);
            if(scoreData.nickname != loginUser.nickname){
              opponentF1Score.value = scoreData.f1Score;
              opponentJaccardScore.value = scoreData.jaccardScore;
            }
          });

          stompClient.subscribe(`/play/start/socket/${route.params.roomId}`, (socket) => {
            const message = JSON.parse(socket.body);
            console.log("시작 구독 성공")
            if(loginUser.nickname != message.sender){
              startRecording();
              try{
                console.log("try");
                const response = local.post("/plays/multi" , {
                sheetId: route.params.sheetId
                },{
                        withCredentials: true
                    });
                multiResultId = response.data
                console.log("멀티 플레이 데이터 저장 성공");
                isResultSender = true;
                // sheet.js에서 중간 점수를 보낼 때, multiResultId를 같이 보낼 수 있도록 sheet.js 저장
                musicStore.multiResultId = multiResultId;
                // sheet store에 multiResultId 저장
                }catch(error){
                    console.log("멀티 플레이 데이터 저장 중 오류 발생");
                }

            }
          });

          stompClient.subscribe(`/play/end/socket/${route.params.roomId}`, (socket) => {
            const message = JSON.parse(socket.body);
            console.log("종료 구독 성공");
            if(loginUser.nickname!= message.sender){
                isLastRecieved = true;
                // 종료 신호를 받는 유저가 이미 끝난 상태에서, 종료 신호를 받았을 때의 조건문
                if(musicStore.isLast == 1){
                    if(isLastRecieved || !isRequested){
                        local.patch(`/plays/multi/${multiResultId}`, {
                        myUserId: loginUser.id,
                        myScore: myJaccardScore.value,
                        otherUserId: opponentUser.nickname,
                        otherScore: opponentJaccardScore
                        }).then(response => {
                            isRequested = true;
                        }).catch(error => {
                        console.log("싱글 플레이 데이터 업데이트 중 오류 발생")
                        });
                    }}
            }
          });

          stompClient.subscribe(`/wait/socket/start/${route.params.roomId}`, function(socket){
            console.log("MultiPlayView에서 exit 메시지 구독 실행");
            const message = JSON.parse(socket.body);
            console.log("exit에 들어왔습니다.")
            userStore.opponentUser.nickname = "유저를 기다리는 중...."
            userStore.opponentUser.userImg = null
        })
    });
}



if (musicStore.f1.length !== 0) {
    myF1Score.value = Math.floor(
        (musicStore.f1.reduce((acc, score) => acc + score, 0) /
            musicStore.f1.length) * 100
    );
}

if (musicStore.jaccard.length !== 0) {
    myJaccardScore.value = Math.floor(
        (musicStore.jaccard.reduce((acc, score) => acc + score, 0) /
            musicStore.jaccard.length) * 100
    );
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
        console.log("확인")
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
            score: myJaccardScore

        })
)}else{
    // 한 유저가 종료 신호를 보내면, 다른 유저가 받아서 Spring으로 데이터를 전송하는 부분
    // 1) 플레이가 끝났지만, 아직 isSender로부터 메시지를 받지 못한 경우를 제외.
    if(isLastRecieved || !isRequested){
        local.patch(`/plays/multi/${multiResultId}`, {
          myUserId: loginUser.id,
          myScore: myJaccardScore.value,
          otherUserId: opponentUser.nickname,
          otherScore: opponentJaccardScore
        }).then(response => {
            isRequested = true;
        }).catch(error => {
          console.log("싱글 플레이 데이터 업데이트 중 오류 발생")
        });
    }
}});



// 악보를 끝까지 완주했을 때, 호출되는 메서드
// Todo: 모달창으로 성공, 실패를 알려줄 것.
watch(() => musicStore.isLast,
  (Last) => {
    local.patch(`/plays/single/${singleResultId}`, {
      userId: loginUser.id,
      score: myJaccardScore.value
    }).catch(error => {
      console.log("싱글 플레이 데이터 업데이트 중 오류 발생")
    });
  }
);

const onClickQuit = () => {
    isQuitting.value = true;
    router.push("/room/multi/list");
};

const handleBeforeUnload = async () => {
    if(isQuitting.value || isPopstate.value || isReloading.value){} 
    else 
    {
        await playStore.exitRoom(route.params.roomId);
        userStore.opponentUser.nickname = "";
        userStore.opponentUser.userImg = null;
        sendExit();
        sendEndDuringPlay();
    }
};

onMounted(() => {
    connect();
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정
    window.addEventListener('popstate', () => {
    isPopstate.value = true;
    });
    // 페이지 내부의 ui를 사용해서 이동하는 것이 아닌, 주소창 입력 또는 탭 종료 같은 외부 방법으로 이동할 때
    // 상대방에게 나갔음을 알리는 것.
    window.addEventListener('beforeunload', handleBeforeUnload);
})

onBeforeUnmount(()=>{
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정 해제
    window.addEventListener('popstate', () => {
    isPopstate.value = true;
    });
    window.removeEventListener('beforeunload', handleBeforeUnload);
})

onBeforeRouteLeave( async (to, from, next) => {
    // 멀티 플레이룸에서 새로고침을 누를 경우, 새로고침이 시행되지 않도록 한다.
    // 이런 방식으로 안됨. 방법 찾기.
    // if(to.name == from.name){
    //     next(false);
    // }

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

    // if((isPopstate.value == true && to.name == "multiRoomList") || isQuitting.value == true){
    //     if(window.confirm("방을 나가시겠습니까?\n진행중인 플레이의 기록은 0점 처리됩니다.\n방 목록 페이지로 이동합니다.")){
    //             // 0점 처리하는 소켓을 보낸다.
    //             // 상대방이 해당 소켓을 받으면, sender이든, receiver이든 상관없이 게임을 끝내고 spring에 데이터를 전송한다.
    //             // 상대방은 이 소켓을 받을 때의 점수를 저장한다.
    //             // 게임을 끝내기 위해 musicStore의 정지 버튼을 호출한다.
    //             sendEndDuringPlay();
    //             // 유저 프로필 갱신
    //             sendExit();
    //             await playStore.exitRoom(route.params.roomId);
    //             userStore.opponentUser.nickname = "";
    //             userStore.opponentUser.userImg = null;
    //             router.push({name: "multiRoomList"});
    //         }
    //     }
    

    // // 뒤로가기를 누를경우, 경고창을 띄운 이후, 진행하면 multiRoomList로 보낸다.
    // // 즉, router.push가 다시 이루어짐으로써 onBeforeRouteLeave 메서드가 재실행된다.
    // if((isPopstate.value == true && to.name != "multiRoomList")){
    //     // 녹음 도중 나갔을 경우, 0점 처리하고, 끝낸다.
    //     if(musicStore.isRecording == true){
    //     if(window.confirm("방을 나가시겠습니까?\n진행중인 플레이의 기록은 0점 처리됩니다.\n방 목록 페이지로 이동합니다.")){
    //         router.push({name: "multiRoomList"});
    //     }
    //     }else{
    //     if(window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.")){
    //         router.push({name: "multiRoomList"});
    //     }
    //     }
    // }
    // // 뒤로가기가 아닌 나가기 버튼 또는 다른 버튼을 눌러서 방을 나가는 경우.
    // if(window.confirm("방을 나가시겠습니까?\n다른 페이지로 이동합니다.")){
    //     sendExit();
    //     await playStore.exitRoom(route.params.roomId);
    //     userStore.opponentUser.nickname = "";
    //     userStore.opponentUser.userImg = null;
    //     next();
    // }
    
})



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
