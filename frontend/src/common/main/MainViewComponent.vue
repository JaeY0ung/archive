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
    <div class="flex flex-col gap-2">
        <div class="w-full h-[40vh] flex flex-col gap-5 m-1 box-border">
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
        
        <div class="w-full h-[50vh] flex p-4 bg-white/50 rounded-2xl">
            <div class="w-full flex flex-col">
                <div class="w-full mb-2 flex justify-between mr-1">
                    <div class="text-xl bold">{{ title }}</div>
                    <div class="flex bold" @click="emit('goToSheetSearchListView')">
                        <div class="flex items-center mr-2">더보기</div>
                        <div class="flex items-center">
                            <img :src="require('@/assets/img/right-arrow.png')" alt="오른쪽 화살표" width="15px" height="15px" class="max-h-[15px] ">
                        </div>
                    </div>
                </div>

                <div class="w-full h-full flex relative scroll-y">
                    <div class="w-full  flex flex-col absolute scroll-y">
                        <template v-if="sheets">
                            <template v-for="sheet in sheets" :key="sheet.id">
                                <SmallSheetCard :sheet="sheet" @click="emit('goToSheetDetail', sheet.id)" />
                            </template>
                        </template>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>