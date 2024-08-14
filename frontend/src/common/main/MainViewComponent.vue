<script setup>
import 'vue3-carousel/dist/carousel.css';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';

defineProps({
    sheets: Array,
    title: String,
})
const emit = defineEmits(['goToSheetDetail', 'goToSheetSearchListView'])
</script>

<template>
    <div class="container w-full h-full flex p-4 bg-white/50 rounded-2xl">
        <div class="w-full flex flex-col">
            <div class="w-full mb-2 flex justify-between mr-1">
                <div class="text-xl bold">{{ title }}</div>
                <div class="flex bold more-button" @click="emit('goToSheetSearchListView')">
                    <div class="flex items-center mr-2">더보기</div>
                    <div class="flex items-center">
                        <img :src="require('@/assets/img/right-arrow.png')" alt="오른쪽 화살표" width="15px" height="15px" class="max-h-[15px] ">
                    </div>
                </div>
            </div>

            <div class="w-full h-full flex relative scroll-y">
                <div class="w-full flex flex-col absolute scroll-y gap-2">
                    <template v-if="sheets">
                        <template v-for="sheet in sheets" :key="sheet.id">
                            <SmallSheetCard :sheet="sheet" @click="emit('goToSheetDetail', sheet.id)" />
                        </template>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

.container {
  border: 1px solid #C9DEFF;
  border-radius: 12px;
  padding: 30px 28px;
  box-sizing: border-box;
  box-shadow: 0px 0px 6px 1px rgba(63, 128, 234, 0.2);
}

.bold {
  font-weight: bold;
}

.scroll-y {
  overflow-y: auto;
}

.more-button {
  cursor: pointer; /* 마우스 모양을 포인터로 변경 */
}
</style>