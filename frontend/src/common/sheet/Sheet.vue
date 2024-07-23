<template>
  <div>
    <div>
      <button @click="play">재생</button>
      <button @click="pause">일시정지</button>
      <button @click="stop">정지</button>
    </div>
    <div ref="osmdContainer"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const osmdContainer = ref(null);
let osmd = null;
let playbackManager = null;

const loadMusicXML = async () => {
  const response = await fetch('/loa.musicxml');
  const xml = await response.text();
  return xml;
};

const setupPlaybackManager = () => {
  console.log("setup PlaybackManager");
  const timingSource = new window.opensheetmusicdisplay.LinearTimingSource();
  console.log("init timingSource");
  playbackManager = new window.opensheetmusicdisplay.PlaybackManager(
    timingSource,
    undefined,
    new window.opensheetmusicdisplay.BasicAudioPlayer(),
    undefined
  );
  console.log("init playbackManager");
  playbackManager.DoPlayback = true;
  playbackManager.DoPreCount = false;
  playbackManager.PreCountMeasures = 1;

  const playbackControlPanel = new window.opensheetmusicdisplay.ControlPanel();
  playbackControlPanel.addListener(playbackManager);
  playbackControlPanel.addListener({
    play() {
      osmd.FollowCursor = true;
    },
    pause() {},
    reset() {},
    bpmChanged() {},
    volumeChanged() {},
    volumeMute() {},
    volumeUnmute() {}
  });

  timingSource.reset();
  timingSource.pause();
  timingSource.Settings = osmd.Sheet.playbackSettings;
  playbackManager.initialize(osmd.Sheet.musicPartManager);
  playbackManager.addListener(osmd.cursor);
  playbackManager.addListener(playbackControlPanel);
  playbackManager.reset();
  osmd.PlaybackManager = playbackManager;
};

const play = () => {
  playbackManager.start();
};

const pause = () => {
  playbackManager.pause();
};

const stop = () => {
  playbackManager.stop();
};

onMounted(async () => {
  console.log("onMounted");
  osmd = new window.opensheetmusicdisplay.OpenSheetMusicDisplay(osmdContainer.value);
  console.log("osmd init");
  const xml = await loadMusicXML();
  console.log("loaded xml");
  await osmd.load(xml);
  console.log("osmd loaded xml");
  osmd.render();
  console.log("osmd rendered");
  setupPlaybackManager();
});
</script>

<style>
button {
  margin-right: 10px;
}
</style>
