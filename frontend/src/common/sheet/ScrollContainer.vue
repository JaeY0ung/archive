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
    musicStore.loadAndSetupOsmd(container.value, props.sheetId);
});
</script>

<template>
    <div class="pointer-events-none m-0 p-0 w-full box-border" :style="{ width: props.width + 'px', height: props.height + '%' }" style="overflow-y:scroll;">
        <div class="m-0 p-0 box-border" ref="container"></div>
    </div>
</template>

<style scoped>
</style>
