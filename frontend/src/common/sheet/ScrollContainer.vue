<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useMusicStore } from '@/stores/sheet';
import debounce from 'lodash.debounce';

const props = defineProps({
    sheetId: Number,
});

const musicStore = useMusicStore();
const container = ref(null);
const outerDiv = ref(null);
const containerWidth = ref(0);
const containerHeight = ref(0);
let resizeObserver;

const updateContainerSize = () => {
    if (outerDiv.value) {
        containerWidth.value = outerDiv.value.offsetWidth;
        containerHeight.value = outerDiv.value.offsetHeight;
    }
}

const handleResize = entries => {
    for (let entry of entries) {
        if (entry.target === outerDiv.value) {
            updateContainerSize();
        }
    }
};

onMounted(() => {
    resizeObserver = new ResizeObserver(handleResize);
    resizeObserver.observe(outerDiv.value);
    musicStore.loadAndSetupOsmd(container.value, props.sheetId);
    updateContainerSize();
});

onUnmounted(() => {
    if (resizeObserver && outerDiv.value) {
        resizeObserver.unobserve(outerDiv.value);
        resizeObserver.disconnect();
    }
});
</script>

<template>
    <div ref="outerDiv" class="flex flex-grow overflow-hidden relative">
        <div ref="container" class="pointer-events-none absolute overflow-y-scroll overflow-x-hidden" :style="{ width: containerWidth + 'px', height: containerHeight + 'px' }" ></div>
    </div>
</template>

<style scoped>
</style>
