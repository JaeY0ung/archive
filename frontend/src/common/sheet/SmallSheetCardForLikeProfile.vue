<script setup>
import { watch } from "vue";
import { getTitleByLen } from "@/util/string-util";
import { useRouter, useRoute } from "vue-router";
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const route = useRoute();

const props = defineProps({
    sheet: Object,
    restrictTitle: {
        type: Boolean,
        default: true,
    },
});

watch(
    () => props.sheet,
    () => {
        props.sheet.imageUrl = props.sheet.songImg
            ? `data:image/jpeg;base64,${props.sheet.songImg}`
            : require("@/assets/img/default/song_img.png");
    }
);
props.sheet.imageUrl = props.sheet.songImg
    ? `data:image/jpeg;base64,${props.sheet.songImg}`
    : require("@/assets/img/default/song_img.png");
</script>

<template>
    <div class="sheet-card">
        <div class="sheet-image">
            <img :src="sheet.imageUrl" :alt="sheet.title" />
        </div>
        <div class="sheet-info">
            <div class="sheet-title">
                <h2>{{ restrictTitle ? getTitleByLen(sheet.title, 12) : sheet.title }}</h2>
                <Tier class="tier-icon" :level="sheet.level" />
            </div>
            <p class="composer">{{ getTitleByLen(sheet.songComposer, 20) }}</p>
            <div class="uploader-info">
                <span class="uploader">업로더 {{ sheet.uploaderNickname }}</span>
                <span v-if="route.name === 'order'" class="price">{{ sheet.price }}원</span>
            </div>
        </div>
        <div class="action-slot">
            <slot />
        </div>
    </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap");

.sheet-card {
    font-family: "Noto Sans KR", sans-serif;
    display: flex;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 12px;
    margin-bottom: 16px;
    transition: all 0.3s ease;
    width: 330px;
    height: 100px;
	user-select: none;
    cursor: default; 
}

.sheet-card * {
    user-select: none; 
    cursor: default; 
}

.sheet-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.sheet-image {
    width: 80px;
    height: 80px;
    margin-right: 12px;
    flex-shrink: 0;
}

.sheet-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 8px;
}

.sheet-info {
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    overflow: hidden;
}

.sheet-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2px;
}

.sheet-title h2 {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: calc(100% - 30px);
    line-height: 1.1; /* 줄 높이 조정 */
    padding-top: 2px; /* 상단 패딩 추가 */
}

.tier-icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
}

.composer {
    font-size: 13px;
    color: #666;
    margin: 0 0 8px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.3;
}

.uploader-info {
    font-size: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.uploader {
    color: #888;
    background-color: #f0f0f0;
    padding: 3px 6px; 
    border-radius: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 70%;
    line-height: 1.2; 
}

.price {
    font-weight: 500;
    color: #007aff;
    line-height: 1.2; 
}

.action-slot {
    display: flex;
    align-items: center;
    margin-left: 8px;
}
</style>
