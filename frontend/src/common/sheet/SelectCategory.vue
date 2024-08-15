<script setup>
import { Carousel, Slide } from 'vue3-carousel';
import { ref } from 'vue';
import 'vue3-carousel/dist/carousel.css';
const emit = defineEmits(['send-sheet-category'])

const cards = ref([
    {sort: "POPULAR", src:'https://newsimg.hankookilbo.com/cms/articlerelease/2021/01/17/fb3de445-6b62-49df-9dab-0fa8efa9cc8c.jpg', title:'인기 악보'},
    {sort: "LATEST", src:'https://img.hankyung.com/photo/202405/01.36880695.1.jpg', title:'최신 플레이 악보'},
    {sort: "LEVEL", src:'https://images.saramingig.co.kr/product/F/0/X/F0Xdpaep3WJVpIR.jpeg/o2j/resize/900', title:'티어 악보'},
    {sort: "RANDOM", src:'https://images.saramingig.co.kr/product/i/D/e/iDepWckeH5rM57A.jpeg/o2j/resize/900', title:'랜덤 악보'},
])
</script>

<template>
    <div class="custom-shadow w-full h-full  px-4 relative bg-[#C9D9FA] overflow-hidden">
        <Carousel class="h-full w-full  absolute" :wrap-around="true" :mouse-drag="true" :items-to-show="2.5" :snap-align="'center'" :transition="300" :autoplay="3000" :pause-autoplay-on-hover="true" >
            <Slide class="flex flex-grow-0 flex-shrink-0 flex-col w-[calc(100%/3)] h-full" v-for="(card, index) in cards" :key="index">
                <div class="flex flex-col flex-grow-0 flex-shrink-0 w-full h-full items-center justify-center overflow-hidden " @click="emit('send-sheet-category', card.sort)">
                    <div class="flex flex-col w-[300px] h-[400px] rounded-2xl">
                        <div class="w-[300px] h-[300px] bg-red-500 rounded-t-2xl flex items-center justify-center overflow-hidden">
                            <img class="object-cover w-full h-full" :src="card.src" alt="카드">
                        </div>  
                        <div class="custom-shadow flex flex-grow flex-col justify-center items-center text-center w-full text-[#4A90E2] font-bold" style="font-size:30px;">
                            <h2>{{ card.title }}</h2>
                        </div>
                    </div>
                </div>
            </Slide>
        </Carousel>
    </div>
</template>

<style>

.custom-shadow {
    @apply rounded-xl ;
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
}

.carousel__viewport  {
    height: 100%; /* carousel_viewport의 높이를 100%로 설정 */
}

.carousel__track {
height: 100%; /* carousel_track의 높이를 100%로 설정 */
}
</style>