<script setup>
import { ref } from "vue";
import { useMusicStore } from '@/stores/sheet';

const musicStore = useMusicStore();
const volume = ref(musicStore.volume);

const play = () => {
    musicStore.startMusic();
};

const pause = () => {
    musicStore.pauseMusic();
};

const stop = () => {
    musicStore.stopMusic();
};

const updateVolume = () => {
    musicStore.setVolume(volume.value);
};
</script>

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

<style>
button {
    margin-right: 10px;
}
</style>
