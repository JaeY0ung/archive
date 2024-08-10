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
    <div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
        <Carousel class="flex h-full w-full items-center absolute" :wrap-around="true" :mouse-drag="true" :items-to-show="2.5" :snap-align="center"  :transition="300">
            <Slide class="flex flex-grow-0 flex-shrink-0 flex-col w-[80px] h-[400px] cursor-pointer bg-black/80" v-for="(card, index) in cards" :key="index" >
                <div class="ml-[5px] mr-[5px]" @click="emit('send-sheet-category', card.sort)">
                    <div class="w-full">
                        <img class="rounded-2xl" :src="card.src" alt="card" />
                    </div>
                    <div class="flex justify-center items-center w-full text-white font-bold" style="font-size:30px;">
                        <h2>{{ card.title }}</h2>
                    </div>
                </div>
            </Slide>
        </Carousel>
    </div>
</template>

<style scoped>

.card-box:has(div:hover) > div:hover{
    transform: scale(1.05);
}

.card{
    transition: all 155ms ease-in-out;
}
</style>