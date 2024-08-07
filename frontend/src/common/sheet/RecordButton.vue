<script setup>
import { watch } from "vue";
import { useMusicStore } from '@/stores/sheet'; // 상대 경로로 변경

const props = defineProps({
    triggerSplit: Number,
    isPlay: {
        type: Boolean,
        default: false,
        required: false,
    },
    roomId: Number,
});

const musicStore = useMusicStore();

// watch를 사용하여 props 변경 감지 및 함수 실행
watch(
    () => props.triggerSplit,
    (newValue) => {
        if (newValue) {
            musicStore.splitRecording();
        }
    }
);

// isPlay 상태 변경에 따라 녹음 시작 및 종료
watch(
    () => props.isPlay,
    (newValue) => {
        if (newValue) {
            musicStore.startRecording();
        } else {
            musicStore.stopRecording();
        }
    }
);
</script>

<template>
    <div></div>
</template>

<style scoped>
button {
    margin: 5px;
}
</style>
