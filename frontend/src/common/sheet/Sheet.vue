<script setup>
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";
import { useMusicStore } from '@/stores/sheet';
import { storeToRefs } from "pinia";

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
const { isPlay } = storeToRefs(musicStore);
const { startRecording, stopRecording, startMusic, pauseMusic, stopMusic } = musicStore;
</script>

<template>
    <div class="flex flex-col w-full h-full ">
        <div class="flex">
            <img width="30px" v-if="!isPlay" :src="require('@/assets/img/sheet_play/play.svg')" @click="startMusic" class="cursor-pointer"/>
            <img width="30px" v-else :src="require('@/assets/img/sheet_play/pause.svg')" @click="pauseMusic" class="cursor-pointer"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/reset.svg')" @click="stopMusic" class="cursor-pointer"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/mic.svg')" @click="startRecording" class="cursor-pointer"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/mic_off.svg')" @click="stopRecording" class="cursor-pointer"/>
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
