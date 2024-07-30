<template>
  <div>
    <button @click="play">재생</button>
    <button @click="pause">일시정지</button>
    <button @click="stop">정지</button>
    <label for="volumeSlider">Volume: {{ volume }}</label>
    <input
      id="volumeSlider"
      type="range"
      min="0"
      max="100"
      v-model="volume"
      @input="updateVolume"
    />
  </div>
</template>

<script setup>
import { ref, watch } from "vue";

const props = defineProps({
  playbackManager: Object,
});
const emit = defineEmits(["music-started", "set-volume"]);

const volume = ref(50);

const play = () => {
  console.log("play click");
  props.playbackManager.play();
  emit("music-started");
};

const pause = () => {
  console.log("pause click");
  props.playbackManager.pause();
};

const stop = () => {
  console.log("stop click");
  props.playbackManager.pause();
  props.playbackManager.reset();
};

const updateVolume = () => {
  emit("set-volume", volume.value);
};
</script>

<style>
button {
  margin-right: 10px;
}
</style>
