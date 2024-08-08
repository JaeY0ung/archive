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
const {startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
</script>

<template>
    <div>
        <Controller v-if="showController" />
        <ScrollContainer :width="props.width" :height="props.height" :sheetId="props.sheetId" />
        <div class="flex gap-1">
            <img width="30px" :src="require('@/assets/img/sheet_play/play.svg')" @click="startMusic"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/pause.svg')" @click="pauseMusic"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/reset.svg')" @click="stopMusic"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/record.svg')" @click="startRecording"/>
            <button @click="stopRecording">녹음 중지</button>
        </div>
    </div>
</template>

<style scoped>
button {
    margin: 5px;
}
</style>
