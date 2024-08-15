<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const showScore = ref(false);

const props = defineProps({
    sheet: Object,
});

const isSuccess = computed(() => props.sheet.singleScore >= 80);
const textColor = computed(() => (isSuccess.value ? "text-green-500" : "text-red-500"));

props.sheet.imageUrl = props.sheet.songImg
    ? `data:image/jpeg;base64,${props.sheet.songImg}`
    : require("@/assets/img/default/song_img.png");

const formatTitle = (title) => {
    if (title.length <= 6) {
        return title;
    } else if (title.length <= 12) {
        const splitIndex = title.substring(0, 7).lastIndexOf(" ");
        if (splitIndex > 0) {
            return title.slice(0, splitIndex) + "\n" + title.slice(splitIndex + 1);
        } else {
            return title.slice(0, 6) + "\n" + title.slice(6);
        }
    } else {
        const truncated = title.substring(0, 12);
        const lastSpaceIndex = truncated.lastIndexOf(" ");
        if (lastSpaceIndex > 0) {
            return title.slice(0, lastSpaceIndex) + "...";
        } else {
            return truncated.slice(0, 11) + "...";
        }
    }
};

const navigateToDetail = () => {
    router.push({ name: "sheetDetail", params: { sheetId: props.sheet.id } });
};
</script>

<template>
    <div class="sheet-card" @click="navigateToDetail">
        <div class="sheet-image">
            <img :src="sheet.imageUrl" :alt="sheet.title" />
        </div>
        <div class="sheet-info">
            <div class="sheet-title">
                <h2>{{ formatTitle(sheet.title) }}</h2>
            </div>
            <p class="composer">{{ sheet.songComposer }}</p>
            <div class="uploader-info">
                <span class="uploader">업로더 {{ sheet.uploaderNickname }}</span>
            </div>
        </div>
        <div class="action-slot">
            <Tier class="tier-icon" :level="sheet.level" />
            <slot />
            <div
                v-if="sheet.singleScore !== undefined"
                class="score-container"
                @mouseenter="showScore = true"
                @mouseleave="showScore = false"
            >
                <div :class="['score-display', isSuccess ? 'success' : 'failure']">
                    <span :class="['score-text', textColor]">
                        {{ showScore ? sheet.singleScore : isSuccess ? "GOOD" : "BAD" }}
                    </span>
                </div>
            </div>
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
    cursor: pointer;
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
    min-width: 0;
}

.sheet-title {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2px;
}

.sheet-title h2 {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0;
    line-height: 1.2;
    max-width: 100%;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    word-break: break-word;
    padding-top: 2px;
}

.composer {
    font-size: 13px;
    color: #666;
    margin: 4px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.3;
}

.uploader-info {
    font-size: 12px;
    margin-top: 4px;
}

.uploader {
    color: #888;
    background-color: #f0f0f0;
    padding: 2px 6px;
    border-radius: 10px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    display: inline-block;
    max-width: 100%;
    line-height: 1.2;
}

.action-slot {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-end;
    margin-left: 8px;
    width: 52px;
}

.tier-icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
    align-self: flex-end;
}

.score-container {
    margin-top: auto;
    align-self: flex-end;
}

.score-display {
    padding: 6px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
}

.score-display.success {
    background-color: #e8f5e9;
}

.score-display.failure {
    background-color: #ffebee;
}

.score-text {
    font-weight: bold;
    font-size: 12px;
    text-align: center;
}
</style>
