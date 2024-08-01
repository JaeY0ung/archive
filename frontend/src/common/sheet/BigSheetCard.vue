<script setup>
import { ref, watch } from 'vue';
import { localAxios } from '@/util/http-common';
import { useRouter } from 'vue-router'

const local = localAxios();
const props = defineProps({
    sheet: {
        type: Object,
        required: true,
        default: {
            id: Number,
            imageUrl: String,
            title: String,
            songComposer: String,
            level: Number,
            uploaderNickname: String,
            likeCount: Number,
            likeStatus: Boolean,
            difficulty: Number, // 아직 안보냄
        }
    }
})

const sheetInfo = ref(props.sheet)
watch(() => props.sheet, (newSheet) => {
    sheetInfo.value = newSheet 
})

const router = useRouter();
const goToUserProfile = () => {
    router.push({ name: 'userProfile', params: { nickName: props.sheet.uploaderNickname} })
}
// 좋아요 기능
const likeSheet = () => {
    local.post(`/likes/sheets/${props.sheet.id}`)
        .then((res) => {
            sheetInfo.value.likeStatus = true;
            sheetInfo.value.likeCount++;
        })
        .catch((err) => console.log(err))
}

// 좋아요 해제 기능
const dislikeSheet = () => {
    local.delete(`/likes/sheets/${props.sheet.id}`)
        .then((res) => {
            sheetInfo.value.likeStatus = false
            sheetInfo.value.likeCount--;
        })
        .catch((err) => console.log(err))
}

// 싱글 배틀 페이지로 이동하기.
const goToPlayRoom = () => {
    router.push({name: 'waitBattle'})
}

// TODO : 장바구니에 넣는 로직 추가하기
const addSheetToCart = () => {
    
}

</script>

<template>
    <div class="flex flex-row gap-3 text-[1.25vw] p-3 bg-white/80 shadow-2xl rounded-3xl shadow-pink-500/50 pr-3">
        <!-- 이미지 -->
        <div class="img flex-none">
            <img class="song-img h-[150px] w-[150px] flex justify-start" :src="sheet.imageUrl" alt="원본 곡 이미지" >
        </div>
        <!-- 중간 -->
        <div class="flex flex-1 flex-col gap-2 mt-3">
            <div class="items-center flex justify-between w-full">
                <div class="flex justify-start">
                    <div class="bold text-[2.25vw] mr-3">
                        {{ sheet.title }}
                    </div>
                    <div class="flex">
                        <img v-if="sheet.level == 1" :src="require('@/assets/img/level/bronze.svg')" alt="">
                        <img v-else-if="sheet.level == 2" :src="require('@/assets/img/level/silver.svg')" alt="">
                        <img v-else-if="sheet.level == 3" :src="require('@/assets/img/level/gold.svg')" alt="">
                        <img v-else-if="sheet.level == 4" :src="require('@/assets/img/level/platinum.svg')" alt="">
                        <img v-else-if="sheet.level == 5" :src="require('@/assets/img/level/diamond.svg')" alt="">
                    </div>
                </div>

                <div class="flex items-start gap-3">
                    <div class="flex items-center">
                        <img :src="require('@/assets/img/star-fill.svg')" alt="별">
                        <p>{{ sheet.difficulty ? sheet.difficulty : 'Unrated' }}</p>
                    </div>
                    <div class="flex items-center">
                        <img :src="require('@/assets/img/view.svg')" alt="눈">
                        <p>{{ sheet.viewCount }}</p>
                    </div>
                    <div class="flex items-center cursor-pointer">
                        <img v-if="sheetInfo.likeStatus === true" :src="require('@/assets/img/heart-fill.svg')" alt="꽉 찬 하트" @click="dislikeSheet">
                        <img v-else :src="require('@/assets/img/heart-empty.svg')" alt="빈 하트" @click="likeSheet">
                        {{ sheetInfo.likeCount  }}
                    </div>
                </div>
            </div>

            <div>
                <p class="medium">{{ sheet.songComposer }}</p>
            </div>

            <div>
                <div class="medium flex flex-row gap-3">
                    <div>업로더명</div>
                    <div class="cursor-pointer" @click="goToUserProfile">{{ sheet.uploaderNickname }}</div>
                </div>
            </div>
            <div class="items-center flex justify-between">
                <div>
                    <RouterLink class="underline" :to="{ name: 'sheetDifficultyRating', params: { sheetId : sheet.id } }">난이도 기여하러 가기</RouterLink>
                </div>
                <div>
                    <button class="btn btn-primary w-[100px] h-1 max-w-[8vw] " @click="goToPlayRoom">플레이하기</button>
                </div>
            </div>
            
        </div>
        <!-- 세로 선 -->
        <div class="line"></div>
        <!-- 장바구니 -->
        <div class="items-center w-[1vw] flex flex-col justify-center gap-1 cursor-pointer" @click="addSheetToCart" >
            <img :src="require('@/assets/img/cart.svg')" alt="장바구니 이미지">

            <div class="text-[1vw]">
                {{ sheet.price }}
            </div>
        </div>
    </div>
</template>

<style scoped>
.line::before {
    content: "";
    display: block;
    border-left: 2px solid black;
    height: 100%;
}
</style>