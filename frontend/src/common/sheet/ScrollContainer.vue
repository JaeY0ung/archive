<script setup>
import { ref, onMounted } from "vue";
import { useMusicStore } from '@/stores/sheet';

const props = defineProps({
    width: Number,
    height: Number,
    sheetId: Number,
});

const musicStore = useMusicStore();
const container = ref(null);

onMounted(() => {
    if (container.value) {
        musicStore.loadAndSetupOsmd(container.value, props.sheetId);
    }
});
</script>

<template>
    <div id="scrollContainer" :style="{ width: props.width + 'px', height: props.height + 'px' }" style="overflow-y:scroll; height:400px;">
        <div id="osmdContainer" ref="container"></div>
    </div>
</template>

<style scoped>
#scrollContainer {
    margin: 0px;
    padding: 0px;
    width: 100%;
    box-sizing: border-box;
}
#osmdContainer {
    margin: 0px;
    padding: 0px;
    box-sizing: border-box;
}
</style>
