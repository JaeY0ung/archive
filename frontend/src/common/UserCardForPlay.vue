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
const defaultProfileImage = require('@/assets/img/common/default_profile.png');
if(props.user.userImg == null){
    props.user.userImg = defaultProfileImage;
}else{
    // img 파일 디코딩
    props.user.userImg = 'data:image/jpeg;base64,' + props.user.userImg;
}

/*
이곳에 있는 점수를 가져오는 메서드는 위로 올린다.
'위'  : SinglePlayView, MultiPlayView로 올려서 UserCardForPlay로 내려주는 방식을 사용하자.
*/

// 반응형 계산을 위한 computed 속성
// const f1Score = computed(() => {
//     if (musicStore.f1.length === 0) return 0;
//     const averageScore = musicStore.f1.reduce((acc, score) => acc + score, 0) / musicStore.f1.length;
//     return Math.floor(averageScore * 100);
// });

// const jaccardScore = computed(() => {
//     if (musicStore.jaccard.length === 0) return 0;
//     const averageScore = musicStore.jaccard.reduce((acc, score) => acc + score, 0) / musicStore.jaccard.length;
//     return Math.floor(averageScore * 100);
// });


// wait/play 화면의 프로필 ui 변경을 위한 route이름 변수화
const routeName = route.name;
console.log(routeName);



</script>

<template>
    <div class="player-card flex justify-center">
        <div class="player-img">
            <img :src="user.userImg" alt="Profile Image" class="rounded-xl"/>
            <span class="pt-3" v-if="routeName=='singlePlay'">랭킹 13위</span>
        </div>
        <div class="player-info-text w-1">
            <div class="info-item">{{ user.nickname }}</div>
            <div class="info-item">
                유저 총점수
            </div>
            <div class="info-item">
                유저 티어
            </div>
            <!-- <button  class="btn text-white" style="background-color: gray" @click="emit('onClickStart')" v-if="routeName=='singleWait'">
                채점 시작
            </button> -->
        </div>
        <div v-if="routeName == 'singlePlay' || routeName == 'multiPlay'">
            <div>현재 스코어(f1) : {{ f1Score }}</div>
            <div>현재 스코어(jaccard) : {{ jaccardScore }}</div>
        </div>
    </div>
</template>

<style scoped>
.player-card {
  flex-direction: row;
  align-items: center;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 100%;
  height: 80%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.player-img {
  background-color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
}

img {
    height: 100px;
    width: 100px;
}

.player-info-text {
  text-align: center;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  flex: 1; /* This ensures it takes up the remaining space */
  justify-content: space-around;
}

.info-item{
    margin-bottom: 5%;
}
</style>
