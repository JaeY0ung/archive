<script setup>
import { ref, onMounted } from "vue";
import Controller from "@/common/sheet/Controller.vue";
import ScrollContainer from "@/common/sheet/ScrollContainer.vue";
import RecordButton from "@/common/sheet/RecordButton.vue";

const props = defineProps({
  showController: Boolean,
  isRecording: Boolean,
  width: {
    type: Number,
    default: 800,
  },
  height: {
    type: Number,
    default: 600,
  },
});

const playbackManager = ref(null);
const recordComponent = ref(null);
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
  const timingSource = new window.opensheetmusicdisplay.LinearTimingSource();
  playbackManager.value = new window.opensheetmusicdisplay.PlaybackManager(
    timingSource,
    undefined,
    new window.opensheetmusicdisplay.BasicAudioPlayer(),
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
      :width="width"
      :height="height"
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
