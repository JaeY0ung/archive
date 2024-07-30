<script setup>
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";
import RecordButton from "@/common/sheet/RecordButton.vue";
import { ref, onMounted } from "vue";
import {
  LinearTimingSource,
  PlaybackManager,
  BasicAudioPlayer,
} from "@/assets/js/opensheetmusicdisplay.min.js";

const props = defineProps({
  showController: Boolean,
  width: Number,
  height: Number,
});

const playbackManager = ref(null);
const isPlay = ref(false);
const triggerSplit = ref(0);

const handleMeasureChanged = (measureIndex) => {
  console.log("Measure changed:", measureIndex);
  triggerSplit.value++;
};

const musicStarted = () => {
  console.log("Music started event");
  isPlay.value = true;
};

const musicFinished = () => {
  console.log("Music finished event");
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
    />
  </div>
</template>

<style scoped>
/* Add your styles here */
</style>
