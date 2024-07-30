<script setup>
import { ref, reactive, watch } from "vue";

const props = defineProps({
  triggerSplit: Number,
  isPlay: Boolean,
});

const isRecording = ref(false);
const mediaRecorder = ref(null);
const audioBlobs = ref([]);
let chunks = reactive([]); // 분할된 음원을 저장할 버퍼
let startTime = 0;

// 시작 버튼을 눌렀을때 작동하는 함수
const startRecording = async () => {
  console.log("Record Started");
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
      const blob = new Blob(chunks, { type: "audio/webm" }); // Binary Large Object (음악 파일)
      const url = URL.createObjectURL(blob); // Url 생성 (나중에 파일 전송으로 수정해야함)
      audioBlobs.value.push({ blob, url }); //
      chunks.length = 0; // 청크 초기화
    }
    // 녹음을 다시 시작합니다.
    if (isRecording.value) {
      mediaRecorder.value.start();
    }
  };

  mediaRecorder.value.start();
  isRecording.value = true;
  startTime = performance.now();
};

// 녹음기를 3초마다 초기화시켜주는 함수
const splitRecording = () => {
  setTimeout(() => {
    if (isRecording.value && mediaRecorder.value.state === "recording") {
      mediaRecorder.value.stop();
    }
  }, 3000);
};
const splitRecording2 = () => {
  if (isRecording.value && mediaRecorder.value.state === "recording") {
    mediaRecorder.value.stop();
  }
};

// 녹음 중에 stop->start 상태가 무한반복이기 때문에 종료 조건을 알려줘야 한다.
const stopRecording = () => {
  console.log("recording ended");
  isRecording.value = false;
  if (mediaRecorder.value.state === "recording") {
    mediaRecorder.value.stop();
  }
};

// watch를 사용하여 props 변경 감지 및 함수 실행
watch(
  () => props.triggerSplit,
  (newValue) => {
    if (newValue) {
      splitRecording2();
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
</script>

<template>
  <div>
    <ul>
      <li v-for="(blob, index) in audioBlobs" :key="index">
        <a :href="blob.url" :download="`audio-part-${index + 1}.webm`"
          >Download Part {{ index + 1 }}</a
        >
      </li>
    </ul>
  </div>
</template>

<style scoped>
button {
  margin: 5px;
}
</style>
