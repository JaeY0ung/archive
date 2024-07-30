<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { usePlayStore } from '@/stores/play.js';

const router = useRouter();
const playStore = usePlayStore();

const selectMode = (mode) => {
  playStore.setMode(mode);
  router.push({ name: 'wait' });
};
</script>

<template>
  <div>pianoSaurusView</div>
  <button @click="() => selectMode('single')">Single Mode</button>
  <button @click="() => selectMode('multi')">Multi Mode</button>
</template>

<style scoped>
button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: #2196f3;
  color: #fff;
  border: none;
  border-radius: 5px;
  margin-top: 20px;
}
</style>
