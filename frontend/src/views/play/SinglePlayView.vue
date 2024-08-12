<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { useRoute } from "vue-router";
import { useMusicStore } from "@/stores/sheet";
import { ref } from "vue";
import { watch } from "vue";
import { localAxios } from "@/util/http-common";
import { onMounted } from "vue";
import { onBeforeUnmount } from "vue";
import { onBeforeRouteLeave } from "vue-router";

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const musicStore = useMusicStore();
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;
const local = localAxios();
let singleResultId = 0;
const isQuitting = ref(false);
const isPopstate = ref(false);
const isReloading = ref(false);

// 플레이 점수를 가져온다.
const myF1Score = ref(0);
const myJaccardScore = ref(0);

// const myF1Score = computed(() => {
//     if (musicStore.f1.length === 0) return 0;
//     const averageScore =
//         musicStore.f1.reduce((acc, score) => acc + score, 0) /
//         musicStore.f1.length;
//     return Math.floor(averageScore * 100);
// });

if (musicStore.f1.length !== 0) {
    myF1Score.value = Math.floor(
        (musicStore.f1.reduce((acc, score) => acc + score, 0) /
            musicStore.f1.length) * 100
    );
}

// const myJaccardScore = computed(() => {
//     if (musicStore.jaccard.length === 0) return 0;
//     const averageScore =
//     musicStore.jaccard.reduce((acc, score) => acc + score, 0) /
//     musicStore.jaccard.length;
//     return Math.floor(averageScore * 100);
// });
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
    },
    { deep: true } // 배열 내부의 변화도 감지
);

const onClickQuit = () => {
    isQuitting.value = true;
    router.push("/room/multi/list");
}

// Sheet.vue에서 녹음 버튼을 클릭했을 때, 호출되는 메서드
const onStartRecordingEmit = async () => {
  startRecording();
  try{
    const response = await local.post("/plays/single" , {
      sheetId: route.params.sheetId
    },{
			withCredentials: true
		});
    singleResultId = response.data
    console.log("싱글 플레이 데이터 저장 성공");
  }catch(error){
    console.log("싱글 플레이 데이터 저장 중 오류 발생");
  }
}

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

// 혼자 연습하기 방에서 연주 도중 방을 나갔을 때, 호출되는 메서드
// 경고창을 띄워서 해당 연습 기록은 저장되지 않습니다 문구를 보여줄 것.
// 임의로 delete mapping을 호출할 것이지만, 상의 후 최종 결정할 것.
const handleBeforeUnload = async () => {

if(isQuitting.value || isPopstate.value){

}else{
  if(musicStore.isRecording == false){
    const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
  }else{
    const answer = window.confirm("방을 나가시겠습니까?\n완료되지 않은 연습기록은 저장되지 않습니다.");
  }
}
};


onMounted(() => {   
    // 브라우저 뒤로가기 버튼 클릭 시 플래그 설정
    window.addEventListener('popstate', () => {
        isPopstate.value = true;
    });
    window.addEventListener('beforeunload', handleBeforeUnload);

});

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
  window.addEventListener('popstate', () => {
    isPopstate.value = true;
  });
});

onBeforeRouteLeave(async (to, from, next) => {

  // 새로고침 체크
    if(to.name == from.name){
        // isReloading.value = true;
        next();
    }

    if(isPopstate.value == true && to.name == "singleWait"){
      if(window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.")){
        router.push({name: "multiRoomList"});
      }
    }

    if(isPopstate.value == true && to.name == "multiRoomList"){
      next();
    }


    const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
    if (answer) {
      next();
    } else {
      next(false);
    }
  });


</script>

<template>
    <div class="container">
      <div class="up">
        <Sheet :sheetId="route.params.sheetId" height="95" @startRecordingEmit="onStartRecordingEmit"/>
      </div>
      <div class="down">
        <UserCardForPlay :user="loginUser" @onClickStart="onClickStart" :f1Score="myF1Score" :jaccardScore="myJaccardScore" />
        <div class="h-[198px] w-[198px] bg-black flex flex-col justify-evenly items-center">
          <div class="flex flex-grow flex-1 h-[50%] items-center justify-center cursor-pointer rounded-xl bg-yellow-300" @click="onClickQuit">
              나가기
          </div>
        </div>
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