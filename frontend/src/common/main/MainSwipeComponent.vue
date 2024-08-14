<script setup>
import { Carousel, Slide } from 'vue3-carousel';
import 'vue3-carousel/dist/carousel.css';

import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import SmallSheetCardForPhoto from '@/common/sheet/SmallSheetCardForPhoto.vue';

defineProps({
    sheets: Array,
    title: String,
})
const emit = defineEmits(['goToSheetDetail', 'goToSheetSearchListView'])
</script>

<template>
    <div class="w-full h-full flex flex-col gap-5 m-1 box-border">
        <div class="w-full min-w-full h-[30%] flex flex-grow relative overflow-hidden items-center">
            <div class="w-full h-full flex justify-center items-center absolute bg-white bg-opacity-50 rounded-2xl">
                <template v-if="sheets">
                    <Carousel  :mouse-drag="true" :snap-align="'center'" :transition="600" :autoplay="3000" :pause-autoplay-on-hover="true">
                        <template v-for="(sheet, index) in sheets" :key="sheet.id">
                            <Slide :index :card="sheet">
                                <SmallSheetCardForPhoto class="bg-white rounded-2xl shadow-lg" :sheet="sheet"  @click="emit('goToSheetDetail', sheet.id)" />
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

</style>