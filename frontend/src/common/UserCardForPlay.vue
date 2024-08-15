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


const resultScore = computed(() => {
    return Math.min(100,(Math.max(0,(props.f1Score - 50)) + Math.max(0,(props.jaccardScore - 40))) * 100 / 80 );
});


// wait/play 화면의 프로필 ui 변경을 위한 route이름 변수화
const routeName = route.name;



</script>

<template>
    <div 
        class="bg-[#f3f7fd] text-[#4A4A4A] relative flex flex-grow w-full h-[180px] justify-center gap-2 rounded-3xl font-bold overflow-hidden  hover:shadow-lg transition-all duration-300"
    >
        <div class="flex w-[198px] h-[198px] pl-8 pb-4 justify-center items-center">
            <div class="flex flex-shrink-0 w-[150px] h-[150px] justify-center items-center rounded-full overflow-hidden">
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
                <div class="flex ml-4 justify-between w-full h-full items-center">
                    <div class="text-black text-lg w-full">현재 스코어</div>
                    <div class="text-[#4A90E2] text-3xl flex items-center pb-2 h-full w-[80%]">
                        {{ resultScore.toFixed(2) }}
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</template>


<style scoped>

</style>
