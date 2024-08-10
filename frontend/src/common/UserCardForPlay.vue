<script setup>
import { useRoute } from 'vue-router';
import { useMusicStore } from '@/stores/sheet';
import { computed } from 'vue';

const route = useRoute();
const musicStore = useMusicStore();

const props = defineProps({
    user: Object
})
const defaultProfileImage = require('@/assets/img/common/default_profile.png');

if(props.user.userImg == null){
    props.user.userImg = defaultProfileImage;
}else{
    // img 파일 디코딩
    props.user.userImg = 'data:image/jpeg;base64,' + props.user.userImg;
}
// 반응형 계산을 위한 computed 속성
const f1Score = computed(() => {
    if (musicStore.f1.length === 0) return 0;
    const averageScore = musicStore.f1.reduce((acc, score) => acc + score, 0) / musicStore.f1.length;
    return Math.floor(averageScore * 100);
});

const jaccardScore = computed(() => {
    if (musicStore.jaccard.length === 0) return 0;
    const averageScore = musicStore.jaccard.reduce((acc, score) => acc + score, 0) / musicStore.jaccard.length;
    return Math.floor(averageScore * 100);
});


// wait/play 화면의 프로필 ui 변경을 위한 route이름 변수화
const routeName = route.name;
console.log(routeName);



</script>

<template>
    <div class="flex flex-grow h-[198px] justify-center bg-black rounded-bl-xl overflow-hidden">
        <div class="flex w-[198px] h-[198px] justify-center items-center ">
            <div class="flex flex-shrink-0 w-[150px] h-[150px] justify-center items-center rounded-full overflow-hidden bg-white">
                <img :src="user.userImg" alt="Profile Image" class="object-cover h-full w-full" />
            </div>
            <span class="pt-3" v-if="routeName=='singlePlay'">랭킹 13위</span>
        </div>
        <div class="flex flex-col flex-grow  h-full pl-[10px] " style="font-size:30px; color:white">
            <div class="flex flex-1  items-center ">
                {{ user.nickname }}
            </div>
            <div class="flex flex-1  items-center ">
                유저 총점수
            </div>
            <div class="flex flex-1  items-center ">
                유저 티어
            </div>
        </div>
        <div v-if="routeName == 'singlePlay'">
            <div>현재 스코어(f1) : {{ f1Score }}</div>
            <div>현재 스코어(jaccard) : {{ jaccardScore }}</div>
        </div>
    </div>
</template>

<style scoped>
</style>
