<script setup>
const emit = defineEmits(['play', 'pause', 'stop'])
</script>

<template>
    <div>
        <button @click="emit('play')">재생하기</button>
        <button @click="emit('pause')">일시정지</button>
        <button @click="emit('stop')">처음으로</button>
        <label for="volumeSlider">Volume: {{ volume }}</label>
        <input id="volumeSlider" type="range" min="0" max="100" v-model="volume" @input="setVolume">
    </div>
</template>

<style scoped>
button {
    margin-right: 10px;
}
</style>