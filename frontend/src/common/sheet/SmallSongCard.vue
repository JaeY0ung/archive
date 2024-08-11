<script setup>
import { watch } from 'vue';

const props = defineProps({
    song: Object
})

watch(() => props.song, () => {
    console.log(props.song)
    props.song.imageUrl = props.song.img ? `data:image/jpeg;base64,${props.song.img}` : require('@/assets/img/default/song_img.png');
})
props.song.imageUrl = props.song.img ? `data:image/jpeg;base64,${props.song.img}` : require('@/assets/img/default/song_img.png'); 
</script>

<template>
    <div class="m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg relative"
        style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">
        <div class="flex gap-3">
            <!-- (왼쪽) 악보 사진 -->
            <div class="h-[80px] w-[80px] flex f">
                <img class="rounded-lg" :src="song?.imageUrl" alt="원본 곡 이미지">
            </div>
            <!-- (오른쪽) 악보 정보 -->
            <div class="min-w-[160px] flex flex-col gap-2 mt-2">
                <div class="flex bold" style="font-size: 18px;">{{ song?.title }}</div>
                <div class="flex medium" style="font-size: 12px;">{{ song?.composer }}</div>
                <div class="flex medium" style="font-size: 12px;">{{ song?.genreTitle }}</div>
            </div>
        </div>
        <slot />
    </div>
</template>

<style scoped></style>