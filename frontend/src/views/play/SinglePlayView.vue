<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { useRoute } from "vue-router";
import { useMusicStore } from "@/stores/sheet";
import { onUnmounted, ref } from "vue";
import { watch } from "vue";
import { localAxios } from "@/util/http-common";
import { onMounted } from "vue";
import { onBeforeUnmount } from "vue";
import { onBeforeRouteLeave } from "vue-router";
import PlayModal from "@/common/modal/PlayModal.vue";
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const musicStore = useMusicStore();
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;
const local = localAxios();

const showModal = ref(false);  // 모달 상태
const modalTitle = ref("");
const modalMessage = ref("");

let singleResultId = 0;
const isQuitting = ref(false);
const isPopstate = ref(false);
const isReloading = ref(false);

// sheet.js에서 올바른 경로로 보낼 수 있도록 mode를 지정해준다.
musicStore.playMode = "single";

// 플레이 점수를 가져온다.
const myF1Score = ref(0);
const myJaccardScore = ref(0);
const resultScore = ref(0);

const updateResultScore = (newScore) => {
  resultScore.value = newScore;
};

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
    },
    { deep: true } // 배열 내부의 변화도 감지
);


watch(
	() => musicStore.isLast,
	async (newVal, oldVal) => {
	  if (newVal) {
		try {
		  const response = await local.patch(`/plays/single/${singleResultId}`, {
        userId: loginUser.id,
        score: Math.min(100,(Math.max(0,(myF1Score.value - 50)) + Math.max(0,(myJaccardScore.value - 40))) * 100 / 80 ),
		  });
		  Swal.fire({
        title: '싱글 플레이 결과',
        html: `
              <p>
                플레이어: ${loginUser.nickname}<br>
                최종 점수: ${Math.min(100,(Math.max(0,(myF1Score.value - 50)) + Math.max(0,(myJaccardScore.value - 40))) * 100 / 80 )}점<br>
              </p>
            `,
        icon: Math.min(100,(Math.max(0,(myF1Score.value - 50)) + Math.max(0,(myJaccardScore.value - 40))) * 100 / 80 ) >= 80 ? 'success' : 'error',
        confirmButtonText: '닫기'
		  }).then(() => {
			// 필요한 경우 추가 작업 수행
		  });

		} catch (error) {
		  Swal.fire({
        title: '오류 발생',
        text: '플레이 데이터를 저장하는 중 오류가 발생했습니다.',
        icon: 'error',
        confirmButtonText: '확인'
		  });
		}
	  }
	}
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
    // sheet.js에서 중간 점수를 보낼 때, singleResultId를 같이 보낼 수 있도록 sheet.js 저장
    musicStore.singleResultId = singleResultId;
    // sheet store에 singleResultId 저장
  }catch(error){
    console.error("싱글 플레이 데이터 저장 중 오류 발생");
  }
}


// 악보를 끝까지 완주했을 때, 호출되는 메서드
watch(
	() => musicStore.isLast,
	async (newVal, oldVal) => {
	  if (newVal) {
      try {
        await local.patch(`/plays/single/${singleResultId}`, {
          userId: loginUser.id,
          score: Math.min(100,(Math.max(0,(myF1Score.value - 50)) + Math.max(0,(myJaccardScore.value - 40))) * 100 / 80 ),
        });
        modalTitle.value = "플레이 완료!";
        modalMessage.value = "축하합니다! 플레이를 완료했습니다.";
      } catch (error) {
        modalTitle.value = "오류 발생";
        modalMessage.value = "플레이 데이터를 저장하는 중 오류가 발생했습니다.";
      }
      showModal.value = true;

	  }
	}
);

// 모달을 닫는 함수
const closeModal = () => {
  showModal.value = false;
};



// 혼자 연습하기 방에서 연주 도중 방을 나갔을 때, 호출되는 메서드
// 경고창을 띄워서 해당 연습 기록은 저장되지 않습니다 문구를 보여줄 것.
// 임의로 delete mapping을 호출할 것이지만, 상의 후 최종 결정할 것.
const handleBeforeUnload = async () => {

// 새로고침도 방을 나가게 할 것이라면 || isReloading.value도 추가
if(isQuitting.value || isPopstate.value){

}else{
  if(musicStore.isRecording == false){
    const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
  }else{
    // 녹음 중 탭을 닫거나 주소창을 이동시키는 방법으로 방을 나갔을 경우
    // local.patch 또는 local.delete로 데이터 수정.
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
        // 새로고침을 불가능하게 만들었다. 불가능하게 안된다. 방법 찾기
        next(false);
    }

    // 뒤로가기를 누를경우, 경고창을 띄운 이후, 진행하면 multiRoomList로 보낸다.
    if(isPopstate.value == true && to.name == "singleWait"){
      if(musicStore.isRecording == true){
        if(window.confirm("방을 나가시겠습니까?\n진행중인 플레이의 기록은 0점 처리됩니다.\n방 목록 페이지로 이동합니다.")){
          router.push({name: "multiRoomList"});
        }
      }else{
        if(window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.")){
          router.push({name: "multiRoomList"});
        }
      }
    }

    // 뒤로가기를 눌렀을 때, multiRoomList로 보낸이후에 해당 조건문에 걸려서 방목록 페이지로 이동한다.
    // 또는 나가기 버튼을 누른 경우
    if((isPopstate.value == true && to.name == "multiRoomList")){
      // 뒤로가기를 눌렀을 때, 녹음 중이 아니라면 방목록 페이지로 이동시킨다.
      if(musicStore.isRecording == false){
        next();
      }else{
        // 뒤로가기를 눌렀을 때, 녹음 중이라면 0점 처리되고, 방목록 페이지로 이동시킨다.
        local.patch(`/plays/single/${singleResultId}`, {
          userId: loginUser.id,
          score: 0
        }).catch(error => {
          console.error("싱글 플레이 데이터 업데이트 중 오류 발생")
        });
      }
    }

    // 방을 나가기 또는 여타 버튼을 눌렀을 때 동작한다.
    const answer = window.confirm("방을 나가시겠습니까?\n방 목록 페이지로 이동합니다.");
    if (answer) {
      next();
    } else {
      next(false);
    }
  });

onUnmounted(()=>{
  musicStore.isLast=false;
  musicStore.isPlay= false;
  musicStore.f1 = [];
  musicStore.jaccard = [];
})

</script>

<template>
  <div class="mx-auto w-[80vw] h-[90vh] bg-[#f3f7fd] rounded-2xl p-5 shadow-lg">
    <div class="bg-white h-[70%] p-5 rounded-2xl shadow-md">
      <Sheet :sheetId="route.params.sheetId" height="95" @startRecordingEmit="onStartRecordingEmit"/>
    </div>
    <div class="flex justify-center items-center h-[25vh] gap-4">
      <UserCardForPlay class="bg-white custom-shadow" :user="loginUser" @onClickStart="onClickStart" :f1Score="myF1Score" :jaccardScore="myJaccardScore" @updateResultScore="updateResultScore" />
      <div class="h-[180px] w-[198px] flex justify-center items-center">
        <div 
          class="custom-shadow flex-grow h-full flex items-center justify-center cursor-pointer rounded-xl text-3xl font-bold bg-white text-[#4A90E2]  transition-all duration-300 hover:bg-sky-100"
          @click="onClickQuit">
          나가기
        </div>
      </div>
    </div>

  <!-- 모달 컴포넌트 -->
<!--	  <PlayModal :visible="showModal" :title="modalTitle" :message="modalMessage" @close="closeModal" />-->
  </div>
</template>

<style scoped>
.custom-shadow {
    @apply rounded-xl bg-white;
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
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