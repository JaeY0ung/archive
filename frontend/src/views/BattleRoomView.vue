<script setup>


import { ref, onMounted, onBeforeUnmount } from 'vue';

let eventSource;

let score = ref(0);

const connect = () => {
    const socket = new WebSocket('ws://localhost:5173/battle')

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


})

</script>

<template>
    <div>
        <h1>배틀 페이지</h1>
    </div>
    <div>
        <h2>실시간 점수</h2>
        <ul>
            Player: 창용, Score: {{ score.score }}
        </ul>
    </div>
</template>

<style scoped>

</style>