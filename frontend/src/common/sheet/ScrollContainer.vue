<template>
  <div
    id="scrollContainer"
    style="overflow-y: scroll"
    :style="{ width: width + 'px', height: height + 'px' }"
  >
    <div id="osmdContainer" ref="osmdContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";

const props = defineProps({
  playbackManager: Object,
  width: Number,
  height: Number,
});
const emit = defineEmits(["measure-changed", "music-finished"]);

const osmdContainer = ref(null);
let osmd = null;

const loadMusicXML = async () => {
  const response = await fetch("/loa.musicxml");
  const xml = await response.text();
  return xml;
};

const setupPlaybackManager = () => {
  const timingSource = props.playbackManager.timingSource;
  props.playbackManager.DoPlayback = true;
  props.playbackManager.DoPreCount = false;
  props.playbackManager.PreCountMeasures = 1;
  osmd.FollowCursor = true;

  timingSource.reset();
  timingSource.pause();
  timingSource.Settings = osmd.Sheet.playbackSettings;
  props.playbackManager.initialize(osmd.Sheet.musicPartManager);
  props.playbackManager.addListener(osmd.cursor);

  let lastEmittedMeasureIndex = 0;
  props.playbackManager.addListener({
    cursorPositionChanged: (currentTimestamp, data) => {
      const currentMeasureIndex = data.CurrentMeasureIndex;
      if (
        currentMeasureIndex >= 0 &&
        currentMeasureIndex - lastEmittedMeasureIndex >= 4
      ) {
        lastEmittedMeasureIndex = currentMeasureIndex;
        emit("measure-changed", currentMeasureIndex);
      }
    },
    resetOccurred: (data) => {
      lastEmittedMeasureIndex = 0;
    },
    soundLoaded: (instrumentId, name) => {},
    allSoundsLoaded: () => {},
    pauseOccurred: () => {
      emit("music-finished");
    },
    selectionEndReached: () => {},
    notesPlaybackEventOccurred: () => {},
  });

  props.playbackManager.reset();
  osmd.PlaybackManager = props.playbackManager;
};

onMounted(async () => {
  osmd = new window.opensheetmusicdisplay.OpenSheetMusicDisplay(osmdContainer.value);
  osmd.setOptions({
    pageFormat: "A4 P",
    pageBackgroundColor: "white",
  });
  const xml = await loadMusicXML();
  await osmd.load(xml);
  osmd.render();
  setupPlaybackManager();
});
</script>

<style>
scrollContainer {
  margin: 0px;
  padding: 0px;
  width: 100%;
  box-sizing: border-box;
}
osmdContainer {
  margin: 0px;
  padding: 0px;
  box-sizing: border-box;
}
</style>
