<script setup>
import { useRoute } from 'vue-router';
import { useMusicStore } from '@/stores/sheet';
import { computed } from 'vue';
import Tier from './icons/Tier.vue';

const route = useRoute();
const musicStore = useMusicStore();

const props = defineProps({
    user: Object,
    f1Score: Number,
    jaccardScore: Number
})
const defaultProfileImage = require('@/assets/img/common/default_profile.png');

if(props.user.userImg == null){
    props.user.userImg = defaultProfileImage;
}else{
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

// const resultScore = computed(() => {
//     return Math.min(100,(Math.max(0,(f1Score.value - 30)) + Math.max(0,(jaccardScore.value - 20))) / 120)*100;
// });






// wait/play 화면의 프로필 ui 변경을 위한 route이름 변수화
const routeName = route.name;
console.log(routeName);



</script>

<template>
    <div 
        class="user-card relative flex flex-grow w-[30vw] h-[180px] justify-center gap-2 rounded-3xl overflow-hidden bg-gradient-to-r from-sky-200/80 to-white/90 hover:shadow-lg transition-all duration-300"
    >
        <div class="flex w-[198px] h-[198px] pl-8 pb-4 justify-center items-center">
            <div class="flex flex-shrink-0 w-[150px] h-[150px] justify-center items-center rounded-full overflow-hidden bg-white">
                <img :src="user.userImg" alt="Profile Image" class="object-cover h-full w-full" />
            </div>
        </div>
        <div class="flex flex-col w-[90%]">
            <div class="flex flex-col flex-grow h-full pl-[10px] text-black text-2xl">
                <div class="flex flex-1 items-center">
                    {{ user.nickname }}
                    <Tier v-if="user.nickname !== '상대를 기다리는 중...'" :level="3" class="ml-4" /> 
                </div>
            </div>
            <hr class="bg-gray-900 ml-5 w-[90%]">
            <div v-if="routeName == 'singlePlay' || routeName == 'multiPlay'" class="flex flex-col flex-grow items-start justify-center h-full pl-[10px]">
                <div class="flex ml-4 justify-between h-full items-center">
                    <div class="text-black text-lg w-full">현재 스코어</div>
                    <div class="text-gray-900 flex items-center h-full w-[80%]">
                        {{jaccardScore}}
                    </div>
                </div>
            </div>
        </div>

        <!-- 슬라이딩 효과를 주는 색깔바 -->
        <div class="absolute inset-0 bg-blue-400 transition-transform duration-500 transform -translate-x-full hover:translate-x-0 z-10"></div>
    </div>
</template>


<style scoped>

</style>
