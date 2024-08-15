<script setup>
import { ref } from 'vue';
import { useMusicStore } from '@/stores/sheet';
import { storeToRefs } from "pinia";
import { onMounted } from "vue";
import { useRoute } from "vue-router";
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";

const musicStore = useMusicStore();
const { isPlay, isRecording } = storeToRefs(musicStore);
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
const emit = defineEmits(['startRecordingEmit']);

const route = useRoute();

const props = defineProps({
    showController: Boolean,
    isRecording: {
        type: Boolean,
        default: false,
    },
    width: Number,
    height: Number,
    sheetId: Number,
});

const countdown = ref(null);
const showCountdown = ref(false);

const handleEmit = () => {
    // 클릭 이벤트 발생 시 이벤트를 부모 컴포넌트로 전송
    if (route.name != "sheetDetail") {
        // 5초 뒤 시작
        showCountdown.value = true;
        countdown.value = 5;
        
        const timer = setInterval(() => {
            countdown.value--;
            if (countdown.value === 0) {
                clearInterval(timer);
                showCountdown.value = false;
                emit('startRecordingEmit', { message: 'Play Button was clicked!' });
            }
        }, 1000);
        // emit('startRecordingEmit', { message: 'Play Button was clicked!' });
    } else{
        startRecording();
    }
};
</script>

<template>
    <div class="flex flex-col w-full h-full relative">
        <div v-if="showCountdown" class="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
            <div class="text-white text-9xl font-bold">{{ countdown }}</div>
        </div>
        <div class="flex">
            <template v-if="route.name == 'sheetDetail'">
                <img width="30px" v-if="!isPlay" :src="require('@/assets/img/sheet_play/play.svg')" @click="startMusic" class="cursor-pointer"/>
                <img width="30px" v-else :src="require('@/assets/img/sheet_play/pause.svg')" @click="pauseMusic" class="cursor-pointer"/>
                <img width="30px" :src="require('@/assets/img/sheet_play/reset.svg')" @click="stopMusic" class="cursor-pointer"/>
            </template>
            <template v-if="route.name == 'singlePlay' || route.name == 'multiPlay'">
                <img v-if="!isRecording" width="30px" :src="require('@/assets/img/sheet_play/mic.svg')" @click="handleEmit" class="cursor-pointer"/>
                <img v-else width="30px" :src="require('@/assets/img/sheet_play/mic_off.svg')" @click="stopRecording" class="cursor-pointer"/>
            </template>
        </div>
        <ScrollContainer class="flex flex-grow w-full h-50%" :sheetId="props.sheetId" />
    </div>
</template>

<style scoped>
button {
    margin: 5px;
}

#sheet{
    height: 100%;
}
</style>
