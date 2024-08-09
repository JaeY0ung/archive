<script setup>
import { ref } from 'vue';

const emit = defineEmits(['send-sheet-category'])

const cards = ref([
    {sort: "POPULAR", src:'https://newsimg.hankookilbo.com/cms/articlerelease/2021/01/17/fb3de445-6b62-49df-9dab-0fa8efa9cc8c.jpg', title:'인기 악보'},
    {sort: "LATEST", src:'https://img.hankyung.com/photo/202405/01.36880695.1.jpg', title:'최신 플레이 악보'},
    {sort: "LEVEL", src:'https://images.saramingig.co.kr/product/F/0/X/F0Xdpaep3WJVpIR.jpeg/o2j/resize/900', title:'티어 악보'},
    {sort: "RANDOM", src:'https://images.saramingig.co.kr/product/i/D/e/iDepWckeH5rM57A.jpeg/o2j/resize/900', title:'랜덤 악보'},
])
</script>

<template>
    <div class="card-box flex w-full justify-around items-center">
        <template v-for="card in cards" >
            <div class="card flex-1 cursor-pointer card-compact" @click="emit('send-sheet-category', card.sort)"> <!-- W-72 -->
                <figure class="w-full">
                    <img class="rounded-2xl" width="93%" :src="card.src" alt="card" />
                </figure>
                <div class="card-body">
                    <h2 class="card-title">{{ card.title }}</h2>
                    <p>{{ card.content }}</p>
                    <div class="card-actions justify-end"></div>
                </div>
            </div>
        </template>
    </div>
</template>

<style scoped>
.card-box:has(div:hover) > div:not(:hover){
    filter: blur(4px)
}

.card-box:has(div:hover) > div:hover{
    transform: scale(1.05);
}

.card{
    transition: all 155ms ease-in-out;
}
</style>