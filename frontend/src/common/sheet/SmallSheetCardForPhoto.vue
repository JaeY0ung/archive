<script setup>
import { watch } from 'vue';
import { getTitleByLen } from '@/util/string-util';

const props = defineProps({
    sheet: Object,
    restrictTitle: {
        type: Boolean,
        default: true
    }
});

watch(() => props.sheet, () => {
    props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
});
props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
</script>

<template>
    <div class="card-container">
        <div class="image-container">
            <img :src="sheet?.imageUrl" alt="악보 이미지" class="sheet-image">
        </div>
        <div class="content-container">
            <h2 class="sheet-title">
                {{ restrictTitle ? getTitleByLen(sheet?.title, 12) : sheet?.title }}
            </h2>
            <p class="sheet-artist">
                {{ restrictTitle ? getTitleByLen(sheet?.songComposer, 12) : sheet?.songComposer }}
            </p>
            <div class="price-container">
                <p class="sheet-price">
                    {{ sheet.price ? sheet.price + '원' : '무료' }}
                </p>
                <img src="/img/cash.e2f5ab38.png" alt="현금 아이콘" class="cash-icon">
            </div>
            <button class="practice-button">연습하기</button>
        </div>
    </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap');

.card-container {
    font-family: 'Noto Sans KR', sans-serif;
    max-width: 250px;
    width: 100%;
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.image-container {
    width: 100%;
    padding-top: 100%; /* 1:1 Aspect Ratio */
    position: relative;
}

.sheet-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.content-container {
    padding: 16px;
}

.sheet-title {
    font-size: 18px;
    font-weight: 700;
    color: #333;
    margin-bottom: 8px;
}

.sheet-artist {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
}

.price-container {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
}

.sheet-price {
    font-size: 14px;
    font-weight: 500;
    color: #3498db;
    margin-right: 8px;
}

.cash-icon {
    width: 15px;
    height: 15px;
}

.practice-button {
    width: 100%;
    padding: 10px;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.practice-button:hover {
    background-color: #2980b9;
}
</style>