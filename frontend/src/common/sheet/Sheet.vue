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
    <div>
        <div class="flex gap-1">
            <img width="30px" v-if="!isPlay" :src="require('@/assets/img/sheet_play/play.svg')" @click="startMusic" class="cursor-pointer"/>
            <img width="30px" v-else :src="require('@/assets/img/sheet_play/pause.svg')" @click="pauseMusic" class="cursor-pointer"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/reset.svg')" @click="stopMusic" class="cursor-pointer"/>
            <!-- <img width="30px" :src="require('@/assets/img/sheet_play/mic.svg')" @click="startRecording" class="cursor-pointer"/>
            <img width="30px" :src="require('@/assets/img/sheet_play/mic_off.svg')" @click="stopRecording" class="cursor-pointer"/> -->
        </div>
        <ScrollContainer :width="props.width" :height="props.height" :sheetId="props.sheetId" />
    </div>
</template>

<style scoped>
button {
    margin: 5px;
}
</style>
