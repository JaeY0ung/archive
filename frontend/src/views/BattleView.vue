<script setup>
import SheetPage from '@/common/sheet/SheetPage.vue';

import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, onBeforeRouteLeave } from 'vue-router';

const router = useRouter();

let eventSource;

let score = ref(0);

const connect = () => {
    const socket = new WebSocket('ws://localhost:8081/battle')

    socket.onopen = () => {
        console.log('Socket opened!');
    }

    socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        score.value = data;
        console.log(score.value);
    };

    socket.onclose = () => {
        console.log('Socket closed');
    }
}

onMounted(() => {

    connect();

    // eventSource = new EventSource("http://localhost:8080/battle"); // 해당 엔드포인트에 SSE 연결을 생성하는 객체

    // eventSource.onopen = () => {
    //     console.log('sse opened!');
    // }

    // eventSource.onmessage = (event) => {
    //     const data = JSON.parse(event.data);
    //     score.value = data;
    //     console.log(score.value);
    // };

    // eventSource.onerror = () => {
    //     console.error('EventSource failed');
    //     eventSource.close();
    // };

    // onBeforeUnmount(() => {
    //     eventSource.close();
    // });

    const isPlay = ref("stop");
    const waitTime = ref(2);
    const countTime = ref(5);

    const wait = setInterval(() => {
        waitTime.value--;
        if (waitTime.value === 0) {

            const countdown = setInterval(() => {
            countTime.value--;
            if (countTime.value === 0) {
                isPlay.value = "play";
                clearInterval(countdown);
            }
            }, 1000);

            clearInterval(wait);
        }
    }, 1000);


})

const canLeaveSite = ref(false);

onBeforeRouteLeave((to, from, next) => {
    console.log('이동할 라우트:', to.name);
    console.log('현재 라우트:', from.name);


    if(to.name == "battle" && from.name == "waitBattle"){
        return;
    }else{
        if(confirm('방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. battle')){
            next();
        }else{
            next(false);
        }
    }

//   if (canLeaveSite.value) {
//     next();
//   } else if (confirm('방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. battle')) {
//     next();
//   }


})

</script>

<template>

    <h1>뮤직 배틀 플레이 화면</h1>
    

    <div>
        <div v-if="waitTime == 0 && countTime !== 0" class="time-count">{{ countTime }}</div>
        <!-- <SheetPage :isPlay/> -->
    </div>

</template>

<style>
    
    #userform{
        display:flex;
        justify-content: space-around;
        margin-bottom: 5%;
    }
    
    #profiles{
        display: flex;
        justify-content: space-around;
        height: 10vh;
        /* border: 1px solid orange; */
    }


    button{
        height: 5vh;
        border: 1px solid white ;
    }

    #profiles div {
        display: flex;
        flex-direction: column;
        justify-content: center;
    }

    p {
        margin-bottom: 10%;
    }

    
</style>