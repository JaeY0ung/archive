<script setup>
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";
import { useMusicStore } from '@/stores/sheet';

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

const musicStore = useMusicStore();

const startRecording = () => {
    musicStore.startRecording();
};

const stopRecording = () => {
    musicStore.stopRecording();
};

const startMusic = () => {
    musicStore.startMusic();
};

const pauseMusic = () => {
    musicStore.pauseMusic();
};

const stopMusic = () => {
    musicStore.stopMusic();
};

</script>

<template>
    <div>
        <Controller v-if="showController" />
        <ScrollContainer :width="props.width" :height="props.height" :sheetId="props.sheetId" />
        <div>
            <button @click="startRecording">녹음 시작</button>
            <button @click="stopRecording">녹음 중지</button>
            <button @click="startMusic">재생</button>
            <button @click="pauseMusic">일시정지</button>
            <button @click="stopMusic">정지</button>
        </div>
    </div>
</template>

<style scoped>
button {
    margin: 5px;
}
</style>
