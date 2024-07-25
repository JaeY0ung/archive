<script setup>
import { ref } from 'vue';
import SheetPage from './SheetPage.vue';
import RecordButton from '../RecordButton.vue';

const recordComponent = ref(null);
const triggerSplit = ref(false);

const mChange = ()=>{
    console.log("changed/4")
    //triggerSplit.value = !triggerSplit.value; // 이 값을 토글하여 변경을 트리거
}
</script>
<template>
    <div>
        <SheetPage v-on:measure-changed="mChange" :width="800" :height="600"/>
        <RecordButton ref="recordComponent" :triggerSplit="triggerSplit"/>
    </div>
</template>
<style scoped>

</style>