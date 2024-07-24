<template>
  <div style="margin-left: 100px">
    <div>
      <button @click="play">재생</button>
      <button @click="pause">일시정지</button>
      <button @click="stop">정지</button>
      <label for="volumeSlider">Volume: {{ volume }}</label>
      <input id="volumeSlider" type="range" min="0" max="100" v-model="volume" @input="setVolume">
    </div>
    <div id="scrollContainer" style="overflow-y: scroll" :style="{ width: width + 'px', height: height + 'px' }">
      <div id="osmdContainer" ref="osmdContainer"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  width: Number,
  height: Number,
});
const emit = defineEmits(['measure-changed']);

const osmdContainer = ref(null);
const volume = ref(50); // 초기 볼륨 값을 50으로 설정
let osmd = null;
let playbackManager = null;

const loadMusicXML = async () => {
  const response = await fetch('/loa.musicxml');
  const xml = await response.text();
  return xml;
};

const setupPlaybackManager = () => {
  console.log('setup PlaybackManager');
  const timingSource = new window.opensheetmusicdisplay.LinearTimingSource();
  console.log('init timingSource');
  playbackManager = new window.opensheetmusicdisplay.PlaybackManager(
    timingSource,
    undefined,
    new window.opensheetmusicdisplay.BasicAudioPlayer(),
    undefined
  );
  console.log('init playbackManager');
  playbackManager.DoPlayback = true;
  playbackManager.DoPreCount = false;
  playbackManager.PreCountMeasures = 1;
  osmd.FollowCursor = true;

  timingSource.reset();
  timingSource.pause();
  timingSource.Settings = osmd.Sheet.playbackSettings;
  playbackManager.initialize(osmd.Sheet.musicPartManager);
  playbackManager.addListener(osmd.cursor);

  let lastEmittedMeasureIndex = 0;
  playbackManager.addListener({
    // 인터페이스 구현하지 않을 시 오류 발생 ( IPlaybackListener  전체 구현해야 정상 작동 )
    cursorPositionChanged: (currentTimestamp, data) => {
      const currentMeasureIndex = data.CurrentMeasureIndex;
      if (currentMeasureIndex >= 0 && (currentMeasureIndex - lastEmittedMeasureIndex) >= 4) {
        lastEmittedMeasureIndex = currentMeasureIndex;
        emit('measure-changed', currentMeasureIndex);
      }
    },
    resetOccurred: (data) => {
      lastEmittedMeasureIndex = 0;
    },
    soundLoaded: (instrumentId, name) => {
    },
    allSoundsLoaded: () => {
    },
    pauseOccurred: () => {
    },
    selectionEndReached: () => {
    },
    notesPlaybackEventOccurred: () => {
    },
  });

  playbackManager.reset();
  osmd.PlaybackManager = playbackManager;
};

const play = () => {
  console.log('play click');
  playbackManager.play();
};

const pause = () => {
  console.log('pause click');
  playbackManager.pause();
};

const stop = () => {
  console.log('stop click');
  playbackManager.pause();
  playbackManager.reset();
};

const setVolume = () => {
  // 메트로놈 볼륨 등 추가로 재생 가능
  console.log('setVolume', volume.value);
  const instrumentIds = Array.from(playbackManager.instrumentIdMapping.keys());
  instrumentIds.forEach((instrumentId) => {
    playbackManager.volumeChanged(instrumentId, volume.value);
  });
};

onMounted(async () => {
  osmd = new window.opensheetmusicdisplay.OpenSheetMusicDisplay(osmdContainer.value);
  console.log('osmd init');
  osmd.setOptions({
    pageFormat: 'A4 P', // P = 세로 L = 가로
    pageBackgroundColor: 'white',
  });
  const xml = await loadMusicXML();
  console.log('loaded xml');
  await osmd.load(xml);
  console.log('osmd loaded xml');
  osmd.render();
  console.log('osmd rendered');
  setupPlaybackManager();
});
</script>

<style>
button {
  margin-right: 10px;
}
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
