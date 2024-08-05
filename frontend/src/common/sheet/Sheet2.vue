<script setup>
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";
import RecordButton from "@/common/sheet/RecordButton.vue";
import { ref, onMounted } from "vue";
import { LinearTimingSource, PlaybackManager, BasicAudioPlayer} from "@/assets/js/opensheetmusicdisplay.min.js";


//props로 roomId 가져와가지고 경로 설정하는 거, roomId별로 하면, 방마다 다른 구독 및 전송 경로가 지정되어 다르게 할 수 있을 것임.

const props = defineProps({
    showController: Boolean,
    isRecording: {
        type: Boolean,
        default: false,
    },
    width: Number,
    height: Number,
    roomId: Number
});



const playbackManager = ref(null);
const isPlay = ref(false);
const triggerSplit = ref(0);

const handleMeasureChanged = (measureIndex) => {
    triggerSplit.value++;
};

const musicStarted = () => {
    isPlay.value = true;
};

const musicFinished = () => {
    isPlay.value = false;
};

const setVolume = (volume) => {
    if (playbackManager.value) {
        const instrumentIds = Array.from(playbackManager.value.instrumentIdMapping.keys());
        instrumentIds.forEach((instrumentId) => {
            playbackManager.value.volumeChanged(instrumentId, volume);
        });
    }
};

onMounted(() => {
    const timingSource = new LinearTimingSource();
    playbackManager.value = new PlaybackManager(
        timingSource,
        undefined,
        new BasicAudioPlayer(),
        undefined
    );
});
</script>

<template>
    <div>
        <Controller
            v-if="showController"
            :playbackManager="playbackManager"
            @music-started="musicStarted"
            @set-volume="setVolume"
        />
        <ScrollContainer
            :playbackManager="playbackManager"
            :width="props.width"
            :height="props.height"
            @measure-changed="handleMeasureChanged"
            @music-finished="musicFinished"
        />
        <RecordButton
            v-if="isRecording"
            ref="recordComponent"
            :triggerSplit="triggerSplit"
            :isPlay="isPlay"
            :roomId="roomId"
        />
    </div>
</template>

<style scoped>

</style>
