<script setup>
import { ref, reactive, watch } from "vue";
import axios from "axios";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
var stompClient = null;

function connect() {
    var client = new SockJS('http://localhost:8081/archive-websocket');
    stompClient = Stomp.over(client);
    stompClient.connect({}, function (frame){
        // subscribe logic
    });
}

const props = defineProps({
    triggerSplit: Number,
    isPlay: {
        type: Boolean,
        default: false,
        required: false,
    },
    roomId: Number
});
let roomId = props.roomId;
// const local = localAxios();

const isRecording = ref(false);
const mediaRecorder = ref(null);
const audioBlobs = ref([]);
let chunks = reactive([]); // 분할된 음원을 저장할 버퍼

// watch를 사용하여 props 변경 감지 및 함수 실행
watch(
    () => props.triggerSplit,
    (newValue) => {
        if (newValue) {
            splitRecording();
        }
    }
);

// isPlay 상태 변경에 따라 녹음 시작 및 종료
watch(
    () => props.isPlay,
    (newValue) => {
        if (newValue) {
            startRecording();
        } else {
            stopRecording();
        }
    }
);

// 시작 버튼을 눌렀을때 작동하는 함수
const startRecording = async () => {
    // 기존 블롭 초기화
    audioBlobs.value = [];
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    //* ! MediaRecorder 초기화
    mediaRecorder.value = new MediaRecorder(stream);

    // ondataavailabe => 이벤트가 발생됐을 때 실행되는 함수
    mediaRecorder.value.ondataavailable = (event) => {
        if (event.data.size > 0) {
            chunks.push(event.data);
        }
    };

  // 녹음기가 종료됐을때 다시 실행(3초마다 무한반복)
    mediaRecorder.value.onstop = () => {
        if (chunks.length > 0) {
            const formData = new FormData();
            const blob = new Blob(chunks, { type: "audio/webm" }); // Binary Large Object (음악 파일)
            formData.append("file", blob, `chunck_${audioBlobs.value.length}.webm`);
            /* 파일 전송으로 바꿔야함 */
            axios
                //post("https://arc-hive.shop/fastapi/playing", formData, {
                .post("http://localhost:8000/fastapi/playing", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                })
                .then((res) => {
                    console.log("파일 업로드 성공: ", res.data);
                })
                .catch((err) => {
                    console.error("파일 업로드 실패: ", err);
                });
            // 경로, 익명 함수, 로그인 이메일
            // stompClient.send()
            audioBlobs.value.push({ blob }); //
            chunks.length = 0; // 청크 초기화
        }
        // 녹음을 다시 시작합니다.
        if (isRecording.value) {
            mediaRecorder.value.start();
        }
    };

    mediaRecorder.value.start();
    isRecording.value = true;
};

const splitRecording = () => {
    if (isRecording.value && mediaRecorder.value.state === "recording") {
        mediaRecorder.value.stop();
    }
};

// 녹음 중에 stop->start 상태가 무한반복이기 때문에 종료 조건을 알려줘야 한다.
const stopRecording = () => {
    isRecording.value = false;
    if (mediaRecorder.value.state === "recording") {
        mediaRecorder.value.stop();
    }
};

</script>

<template>
    <div></div>
</template>

<style scoped>
button {
    margin: 5px;
}
</style>
