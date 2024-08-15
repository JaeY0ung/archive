<script setup>
import { Carousel, Slide } from 'vue3-carousel';
import 'vue3-carousel/dist/carousel.css';
import SmallSheetCardForPhoto from '@/common/sheet/SmallSheetCardForPhoto.vue';

defineProps({
    sheets: Array,
    title: String,
})
</script>

<template>
    <div class="container more-button w-full h-full flex flex-col m-1 box-border">
        <div class="w-full min-w-full flex flex-grow relative overflow-hidden items-center">
            <div class="w-full h-full flex justify-center items-center absolute bg-white bg-opacity-50 rounded-2xl">
                <template v-if="sheets">
                    <Carousel :mouse-drag="true" :snap-align="'center'" :transition="600" :autoplay="3000" :pause-autoplay-on-hover="true">
                        <template v-for="(sheet, index) in sheets" :key="sheet.id">
                            <Slide :index :card="sheet">
                                <SmallSheetCardForPhoto class="bg-white rounded-2xl shadow-lg" :sheet="sheet" />
                            </Slide>
                        </template>
                    </Carousel>
                </template>
                <template v-else>
                    없습니다..
                </template>
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