<script setup>
import { ref, onMounted, watch } from 'vue';

const emit = defineEmits(['measure-changed']);

const props = defineProps({
  isPlay: {type: String, required : false, default: false},
  // width: {type: String, required : false, default: '100%'},
  // height: {type: String, required : false, default: '70vh'},
})

const osmd = ref(null);

watch(() => props.isPlay, (newValue, oldValue) => {
  if (newValue === 'play') {
    play();
  } else if (newValue === 'pause') {
    pause();
  } else if (newValue === 'stop') {
    stop();
  }
})

const osmdContainer = ref(null);
const volume = ref(50); // 초기 볼륨 값을 50으로 설정
// let osmd = null;
let playbackManager = null;


const loadMusicXML = async(xmlFile) => {
  const response = await fetch(xmlFile);
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
  osmd.value.FollowCursor = true;

  timingSource.reset();
  timingSource.pause();
  timingSource.Settings = osmd.value.Sheet.playbackSettings;
  playbackManager.initialize(osmd.value.Sheet.musicPartManager);
  playbackManager.addListener(osmd.value.cursor);

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
  osmd.value.PlaybackManager = playbackManager;
};

const play = () => {
  playbackManager.play();
};

const pause = () => {
  playbackManager.pause();
};

const stop = () => {
  playbackManager.pause();
  playbackManager.reset();
};

const setVolume = () => {
  // 메트로놈 볼륨 등 추가로 재생 가능
  const instrumentIds = Array.from(playbackManager.instrumentIdMapping.keys());
  instrumentIds.forEach((instrumentId) => {
    playbackManager.volumeChanged(instrumentId, volume.value);
  });
};

onMounted(async () => {
  const xml = await loadMusicXML('/loa.musicxml');
  
  const script = document.createElement('script');
  script.src = '/opensheetmusicdisplay.min.js'; // public 폴더에 있는 파일의 경로
  script.onload = () => {
    osmd.value = new window.opensheetmusicdisplay.OpenSheetMusicDisplay(osmdContainer.value);

    osmd.value.setOptions({
      pageFormat: 'A4 P', // P = 세로 L = 가로
      pageBackgroundColor: 'white',
    });
  };
  document.body.appendChild(script);

  watch(osmd, async(newValue, oldValue) => {
    await osmd.value.load(xml); 
    osmd.value.render();
    setupPlaybackManager();
  })
});
</script>

<template>
  <div class="w-full h-full">
    <!-- 연주 X : scroll -->
    <div class="rounded-xl h-[94%] w-full overflow-hidden" ref="osmdContainer"></div>
  </div>
</template>

<style>
.time-count {
  font-size: 100px;
  color:brown;
  position: fixed;
  top: 50vh;
  left:50vw;
  z-index: 50;
}
</style>
