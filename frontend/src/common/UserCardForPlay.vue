<script setup>
import { useRoute } from 'vue-router';
import { useMusicStore } from '@/stores/sheet';
import { computed } from 'vue';

const route = useRoute();
const musicStore = useMusicStore();

const props = defineProps({
    user: Object
})
const emit = defineEmits(['onClickStart'])

// 반응형 계산을 위한 computed 속성
const f1Score = computed(() => {
    if (musicStore.f1.length === 0) return 0;
    return musicStore.f1.reduce((acc, score) => acc + score, 0) / musicStore.f1.length;
});

const jaccardScore = computed(() => {
    if (musicStore.jaccard.length === 0) return 0;
    return musicStore.jaccard.reduce((acc, score) => acc + score, 0) / musicStore.jaccard.length;
});

// img 파일 디코딩
props.user.userImg = 'data:image/jpeg;base64,' + props.user.userImg;

// wait/play 화면의 프로필 ui 변경을 위한 route이름 변수화
const routeName = route.name;
console.log(routeName);



</script>

<template>
    <div class="player-card">
        <div class="player-img">
            <img :src="user.userImg" alt="Profile Image" />
            <span class="pt-3" v-if="routeName=='singlePlay'">랭킹 13위</span>
        </div>
        <div class="player-info-text">
            <div>{{ user.nickname }}</div>
            <div>현재 스코어(f1) : {{ f1Score }}</div>
            <div>현재 스코어(jaccard) : {{ jaccardScore }}</div>
            <button  class="btn text-white" style="background-color: gray" @click="emit('onClickStart')" v-if="routeName=='singleWait'">
                채점 시작
            </button>
        </div>
    </div>
</template>

<style scoped>
.player-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 40%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.player-img {
  width: 100px;
  height: 100px;
  background-color: white;
  border-radius: 50%;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.player-info-text {
  text-align: center;
  margin-bottom: 10px;
}
</style>
