<script setup>
import Sheet from "@/common/sheet/Sheet.vue";
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { jwtDecode } from "jwt-decode";
import { usePlayStore } from "@/stores/play";

const { VUE_APP_REQUEST_URL } = process.env;
const router = useRouter();

const myPoint = ref(0);
const opponentPoint = ref(0);
const score = ref(0);

let stompClient = null;
let eventSource;

const props = defineProps({
    roomId: Number
})

const playStore = usePlayStore();

const connect = () => {
  let socket = new SockJS(`${VUE_APP_REQUEST_URL}/archive-websocket`);
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function (frame) {
    console.log("들어왔습니다~");

    // 여기서 구독
    stompClient.subscribe("/wait/socket/start", function (message) {
      const data = JSON.parse(message.body);

      console.log(data);

      // sender 구분을 해줘야 함.

      if (data.type === "point" && data.sender != decodeToken.username) {
        opponentPoint.value = data.content;
      }
    });
  });

  // 소켓이 열렸을 때
  // socket.onopen = () => {
  //     console.log('Socket opened!');
  // }

  // 메시지를 보냈을 때
  // socket.onmessage = (event) => {
  //     const data = JSON.parse(event.data);
  //     score.value = data;
  //     console.log(score.value);
  // };

  // 소켓이 닫혔을 때
  // socket.onclose = () => {
  //     console.log('Socket closed');
  // }
};

const accessToken = sessionStorage.getItem("accessToken");
const decodeToken = jwtDecode(accessToken);

onMounted(() => {
  connect();

  // eventSource = new EventSource(`${VUE_APP_REQUEST_URL}/battle`); // 해당 엔드포인트에 SSE 연결을 생성하는 객체

  // eventSource.onopen = () => {
  //     console.log('sse opened!');
  // }

  // eventSource.onmessage = (event) => {
  //     const data = JSON.parse(event.data);
  //     score.value = data;
  //     console.log(score.value);
  // };

  // eventSource.onerror = () => {
  //     console.error('EventSource failed');
  //     eventSource.close();
  // };

  // onBeforeUnmount(() => {
  //     eventSource.close();
  // });

  const isPlay = ref("stop");
  const waitTime = ref(2);
  const countTime = ref(5);

  const wait = setInterval(() => {
    waitTime.value--;
    if (waitTime.value === 0) {
      const countdown = setInterval(() => {
        countTime.value--;
        if (countTime.value === 0) {
          isPlay.value = "play";
          clearInterval(countdown);
        }
      }, 1000);

      clearInterval(wait);
    }
  }, 1000);

  // setInterval 설정
  const plusPoint = setInterval(() => {
    myPoint.value += Math.floor(Math.random() * 10);
    stompClient.send(
      "/app/wait/start",
      {},
      JSON.stringify({
        type: "point",
        sender: decodeToken.username,
        content: myPoint.value,
      })
    );
  }, 100);

  // 5초 후에 setInterval 멈추기
  setTimeout(() => {
    clearInterval(plusPoint);
  }, 5000);
});

onBeforeRouteLeave( async (to, from, next) => {
  console.log("이동할 라우트:", to);
  console.log("이동할 라우트:", to.name);
  console.log("현재 라우트:", from);
  console.log("현재 라우트:", from.name);

  if (confirm("방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. battle")) {
    await playStore.exitRoom(props.roomId);
    window.location.href = "http://localhost:3000/pianosaurus"
    next();
  } else {
    next(false);
  }
});
</script>

<template>
  <div>
    <h1>뮤직 배틀 플레이 화면</h1>

  <div>
    <div v-if="waitTime == 0 && countTime !== 0" class="time-count">{{ countTime }}</div>
    <!-- <SheetPage :isPlay/> -->
  </div>

  <div class="score">내점수 {{ myPoint }}점!!</div>
  <div class="score">상대방 {{ opponentPoint }}점!!</div>
  </div>
  
</template>

<style>
#userform {
  display: flex;
  justify-content: space-around;
  margin-bottom: 5%;
}

#profiles {
  display: flex;
  justify-content: space-around;
  height: 10vh;
  /* border: 1px solid orange; */
}

button {
  height: 5vh;
  border: 1px solid white;
}

#profiles div {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

p {
  margin-bottom: 10%;
}

.score {
  font-size: 30px;
}
</style>
