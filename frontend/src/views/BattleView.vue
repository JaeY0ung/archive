<script setup>
import SheetPage from '@/common/sheet/SheetPage.vue';
import { ref } from 'vue';

const isPlay = ref("stop");
const waitTime = ref(2);
const countTime = ref(5);

const wait = setInterval(() => {
    waitTime.value--;
    if (waitTime.value === 0) {

        const countdown = setInterval(() => {
        countTime.value--;
        if (countTime.value === 0) {
            isPlay.value = "play";
            clearInterval(countdown);
        }
        }, 1000);

        clearInterval(wait);
    }
}, 1000);
</script>

<template>
    <div>
        <div v-if="waitTime == 0 && countTime !== 0" class="time-count">{{ countTime }}</div>
        <SheetPage :isPlay/>
    </div>
</template>

<style scoped>

</style>