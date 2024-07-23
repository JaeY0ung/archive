<script setup>
import { ref, computed } from 'vue';

const me = ref({
    img: "이미지1",
    name: "악카이브1",
    score: "80",
})

const opponent = ref({
    img: "이미지2",
    name: "악하이브2",
    score: "85",
})

const getLiveResult = computed(() => {
    return me.value.score > opponent.value.score ? "win" : "lose"
})
</script>

<template>
    <div class="container">
        <!-- 배틀 페이지 -->
        <div class="up">
            <RouterView/>
        </div>

        <div class="down">
            <!-- 아래 -->
            <div class="player-card">
                <div class="player-img">{{ me.img }}</div>
                <div class="player-info-text">
                    <div>{{ me.name }}</div>
                    <div>현재 스코어 : {{ me.score }}</div>
                </div>
            </div>

            <div class="button-div">
                <button class="btn btn-primary w-24">
                    시작하기
                </button>
            </div>

            <div class="player-card">
                <div class="player-img">{{ opponent.img }}</div>
                <div class="player-info-text">
                    <div>{{ opponent.name }}</div>
                    <div>현재 스코어 : {{ opponent.score }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    margin: 10px auto; /*  상하 좌우 */
    width: 70vw;
}
.up {
    background-color: rgb(194, 161, 161);
    height: 70vh;
    margin-bottom: 50px;
}

.down {
    display: flex;
    justify-content: center;
}

.button-div {
    width: 20vw;
    display: flex;
    justify-content: center;
    align-items: end;
}

.player-card {
    display: flex;
    width: 40vw;
    border: 3px solid rgb(51, 117, 71);
    border-radius: 15px;
}

.player-img {
    width: 100px;
    height: 100px; 
    background-color: white;
    border-radius: 15px;
    
}
.player-info-text {
    width: 200px;
    margin-left: 10px;
}
</style>