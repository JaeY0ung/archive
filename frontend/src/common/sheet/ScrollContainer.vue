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
    containerWidth.value = outerDiv.value.offsetWidth;
    containerHeight.value = outerDiv.value.offsetHeight;
    musicStore.loadAndSetupOsmd(container.value, props.sheetId)
    .then(
        updateContainerSize()
    );
});

onUnmounted(() => {
    if (resizeObserver && outerDiv.value) {
        resizeObserver.unobserve(outerDiv.value);
        resizeObserver.disconnect();
    }
    musicStore.cleanup();  // cleanup 메서드 호출하여 리소스 정리
});
</script>

<template>
    <div ref="outerDiv" class="flex flex-grow overflow-hidden relative">
        <div class="absolute inset-0 bg-transparent z-10"></div>
        <div ref="container" class="absolute overflow-y-scroll overflow-x-hidden" :style="{ width: containerWidth + 'px', height: containerHeight + 'px', maxHeight: containerHeight+'px' }" ></div>
    </div>
</template>

<style scoped>
</style>
