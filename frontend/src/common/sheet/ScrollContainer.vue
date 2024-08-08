<script setup>
import { ref, onMounted } from "vue";
import { useMusicStore } from '@/stores/sheet';

const props = defineProps({
    sheetId: Number,
});

const musicStore = useMusicStore();
const container = ref(null);
const outerDiv = ref(null);
const containerWidth = ref(0);
const containerHeight = ref(0);

onMounted(() => {
    if (outerDiv.value) {
        const resizeObserver = new ResizeObserver(entries => {
            for (let entry of entries) {
                if (entry.target === outerDiv.value) {
                    containerWidth.value = entry.contentRect.width;
                    containerHeight.value = entry.contentRect.height;
                }
            }
        });
        resizeObserver.observe(outerDiv.value);

        musicStore.loadAndSetupOsmd(container.value, props.sheetId);
    }
});
</script>

<template>
    <div ref="outerDiv" class="flex flex-grow">
        <div class="pointer-events-none overflow-y-scroll" :style="{ width: containerWidth + 'px', height: containerHeight + 'px' }" >
            <div ref="container"></div>
        </div>
    </div>
</template>

<style scoped>
</style>
