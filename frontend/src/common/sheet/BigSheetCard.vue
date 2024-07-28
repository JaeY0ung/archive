<script setup>
import { faL } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { useRouter } from 'vue-router'

const props = defineProps({
    sheet: {
        type: Object, required: true, default: {
            id: Number,
            imageUrl: String,
            title: String,
            songComposer: String,
            level: Number,
            uploaderNickname: String,
            likeCount: Number,
            isLike: Boolean,
        }
    }
})

const router = useRouter();
const goToUserProfile = () => {
    router.push({ name: 'userProfile', params: { nickName: props.sheet.uploaderNickname} })
}

const likeSheet = () => {
    axios.post(`http://localhost:8080/likes/sheets/${props.sheet.id}`)
        .then((res) => props.sheet.isLike = false)
        .catch((err) => console.log(err))
}

const dislikeSheet = () => {
    axios.delete(`http://localhost:8080/likes/sheets/${props.sheet.id}`)
        .then((res) => props.sheet.isLike = true)
        .catch((err) => console.log(err))
}
</script>

<template>
    <div class="flex flex-row gap-3 text-xl m-3 p-3 bg-white/80 shadow-2xl rounded-3xl shadow-pink-500/50 pr-3">
        <div class="img flex-none">
            <img class="song-img h-[150px] w-[150px] flex justify-start" :src="sheet.imageUrl" alt="원본 곡 이미지" >
        </div>

        <div class="sheet-info flex-1">
            <div>
                <div class="flex justify-start items-center w-full">
                    <p class="bold text-3xl mr-3">
                        {{ sheet.title }}
                    </p>
                    <div>
                        <img v-if="sheet.level == 1" :src="require('@/assets/img/level/bronze.svg')" alt="">
                        <img v-else-if="sheet.level == 2" :src="require('@/assets/img/level/silver.svg')" alt="">
                        <img v-else-if="sheet.level == 3" :src="require('@/assets/img/level/gold.svg')" alt="">
                        <img v-else-if="sheet.level == 4" :src="require('@/assets/img/level/platinum.svg')" alt="">
                        <img v-else-if="sheet.level == 5" :src="require('@/assets/img/level/diamond.svg')" alt="">
                    </div>
                </div>
                <div>
                    <p class="medium">{{ sheet.songComposer }}</p>
                </div>
            </div>

            <div>
                <div>
                    <p class="medium cursor-pointer " @click="goToUserProfile">
                        업로더명
                        {{ sheet.uploaderNickname }}
                    </p>
                </div>
                <div>
                    <p class="medium">가격 {{ sheet.price }}</p>
                </div>
                <div>
                    <RouterLink :to="{ name: 'waitBattle' }" class="medium block text-xl">곡 연습하러 가기</RouterLink>
                </div>
            </div>
        </div>
        <div>
            <div class="flex">
                <img :src="require('@/assets/img/view.svg')" alt="">
                <p>{{ sheet.viewCount }}</p>
            </div>
            <div class="cursor-pointer">
                <img v-if="sheet.isLike" :src="require('@/assets/img/heart-fill.svg')" alt="꽉 찬 하트" @click="likeSheet">
                <img v-else :src="require('@/assets/img/heart-empty.svg')" alt="빈 하트" @click="dislikeSheet">
            </div>
        </div>
    </div>
</template>

<style scoped>
.sheet-info > * {
    padding: 5px 0;
}
</style>