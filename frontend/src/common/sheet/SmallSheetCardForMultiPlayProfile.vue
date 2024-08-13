<script setup>
import { ref, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { getTitleByLen } from "@/util/string-util";
import Tier from "@/common/icons/Tier.vue";
import Profile from "@/common/icons/Profile.vue";

const props = defineProps({
    sheet: {
        type: Object,
        required: true,
    },
});

const router = useRouter();
const showModal = ref(false);

const battleResult = computed(() => {
    if (props.sheet.draw) {
        return "DRAW";
    } else if (props.sheet.myScore > props.sheet.otherScore) {
        return "WIN";
    } else {
        return "LOSE";
    }
});

const resultStyle = computed(() => {
    if (props.sheet.draw) {
        return "bg-[#555555] text-white";
    } else if (props.sheet.myScore > props.sheet.otherScore) {
        return "bg-green-100 text-green-500";
    } else {
        return "bg-red-100 text-red-500";
    }
});

const sheetImageUrl = computed(() =>
    props.sheet.songImg
        ? `data:image/jpeg;base64,${props.sheet.songImg}`
        : require("@/assets/img/default/song_img.png")
);

const formatPlayTime = (seconds) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;
    return `${hours.toString().padStart(2, "0")}:${minutes
        .toString()
        .padStart(2, "0")}:${remainingSeconds.toString().padStart(2, "0")}`;
};

const isValidUserImg = (userImg) => {
    return userImg && userImg.trim().length > 0 && !userImg.endsWith("base64,null");
};

const openModal = () => {
    showModal.value = true;
};

const closeModal = () => {
    showModal.value = false;
};

const goToUserProfile = (nickname) => {
    if (nickname) {
        if (showModal.value) {
            closeModal();
            nextTick(() => {
                router.push({
                    name: "userProfile",
                    params: { nickName: nickname },
                });
            });
        } else {
            router.push({
                name: "userProfile",
                params: { nickName: nickname },
            });
        }
    } else {
        alert("사용자 정보를 불러올 수 없습니다.");
    }
};

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
</script>

<template>
    <div>
        <div
            class="sheet-container w-[330px] h-[90px] m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg overflow-hidden relative"
            style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3)"
            @click="openModal"
        >
            <div class="flex justify-start gap-3">
                <!-- (왼쪽) 악보 사진 -->
                <div class="h-[80px] w-[80px] flex-shrink-0 flex justify-center">
                    <img
                        class="rounded-lg object-cover"
                        :src="sheetImageUrl"
                        alt="원본 곡 이미지"
                    />
                </div>

                <!-- (중앙) 악보 정보 -->
                <div class="min-w-0 flex-grow pt-[5px] flex flex-col gap-1">
                    <div class="bold flex justify-between items-start" style="font-size: 16px">
                        <div class="custom-title mr-2">
                            {{ formatTitle(sheet.title) }}
                        </div>
                        <Tier class="w-[16px] h-[16px] flex-shrink-0" :level="sheet.level" />
                    </div>
                    <div class="medium mb-1 truncate" style="font-size: 12px">
                        {{ sheet.songComposer }}
                    </div>
                    <div class="medium flex items-center gap-1 truncate" style="font-size: 12px">
                        업로더 {{ sheet.uploaderNickname }}
                    </div>
                </div>
            </div>

            <!-- (오른쪽) 점수 및 추가 기능 -->
            <div class="p-[5px] flex flex-col items-end justify-center gap-2">
                <slot />
                <div :class="['p-3 rounded-full flex items-center justify-center', resultStyle]">
                    <span class="font-bold text-lg">
                        {{ battleResult }}
                    </span>
                </div>
            </div>

            <!-- 호버 오버레이 -->
            <div class="hover-overlay">
                <span class="hover-text">자세히 보기</span>
            </div>
        </div>

        <!-- Modal -->
        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <div class="modal-header">
                    <h2 class="modal-title">대결 결과</h2>
                    <button class="close-btn" @click="closeModal">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="sheet-info">
                        <img :src="sheetImageUrl" alt="Sheet Image" class="sheet-image" />
                        <h3 class="sheet-title">{{ sheet.title }}</h3>
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="info-label">작곡가</span>
                                <span class="info-value info-container">{{
                                    sheet.songComposer
                                }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">업로더</span>
                                <span class="info-value info-container">{{
                                    sheet.uploaderNickname
                                }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">난이도</span>
                                <span class="info-value info-container">
                                    <Tier class="tier-icon" :level="sheet.level" />
                                </span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">플레이 시간</span>
                                <span class="info-value info-container">{{
                                    formatPlayTime(sheet.playTime)
                                }}</span>
                            </div>
                        </div>
                    </div>

                    <div class="players-info">
                        <h3 class="section-title">플레이어 정보</h3>
                        <div class="players-grid">
                            <div
                                :class="[
                                    'player-box',
                                    {
                                        winner: sheet.myScore > sheet.otherScore,
                                        loser: sheet.myScore < sheet.otherScore,
                                    },
                                ]"
                                @click="goToUserProfile(sheet.myNickname)"
                            >
                                <div class="profile-image-container">
                                    <img
                                        v-if="isValidUserImg(sheet.myProfileImg)"
                                        :src="'data:image/jpeg;base64,' + sheet.myProfileImg"
                                        alt="My Profile"
                                        class="profile-image"
                                    />
                                    <Profile v-else class="profile-image" />
                                </div>
                                <div class="profile-info">
                                    <p class="nickname">{{ sheet.myNickname }}</p>
                                    <p class="score">점수 {{ sheet.myScore }}</p>
                                    <p class="result-text" v-if="sheet.myScore > sheet.otherScore">
                                        WIN
                                    </p>
                                    <p
                                        class="result-text"
                                        v-else-if="sheet.myScore < sheet.otherScore"
                                    >
                                        LOSE
                                    </p>
                                    <p class="result-text" v-else>DRAW</p>
                                </div>
                            </div>
                            <div
                                :class="[
                                    'player-box',
                                    {
                                        winner: sheet.otherScore > sheet.myScore,
                                        loser: sheet.otherScore < sheet.myScore,
                                    },
                                ]"
                                @click="goToUserProfile(sheet.otherNickname)"
                            >
                                <div class="profile-image-container">
                                    <img
                                        v-if="isValidUserImg(sheet.otherProfileImg)"
                                        :src="'data:image/jpeg;base64,' + sheet.otherProfileImg"
                                        alt="Opponent Profile"
                                        class="profile-image"
                                    />
                                    <Profile v-else class="profile-image" />
                                </div>
                                <div class="profile-info">
                                    <p class="nickname">{{ sheet.otherNickname }}</p>
                                    <p class="score">점수 {{ sheet.otherScore }}</p>
                                    <p class="result-text" v-if="sheet.otherScore > sheet.myScore">
                                        WIN
                                    </p>
                                    <p
                                        class="result-text"
                                        v-else-if="sheet.otherScore < sheet.myScore"
                                    >
                                        LOSE
                                    </p>
                                    <p class="result-text" v-else>DRAW</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- <div class="battle-result" :class="resultStyle">
                        <p class="result">{{ battleResult }}</p>
                    </div> -->
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
* {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

*::-webkit-scrollbar {
    display: none;
}

.sheet-container {
    position: relative;
    cursor: pointer;
}

.hover-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    border-radius: 8px;
}

.sheet-container:hover .hover-overlay {
    opacity: 1;
}

.hover-text {
    color: white;
    font-size: 18px;
    font-weight: bold;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    border-radius: 16px;
    width: 450px;
    max-width: 90%;
    height: 600px;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
    font-family: "Noto Sans KR", sans-serif;
    overflow: auto;
}

.modal-header {
    padding: 20px;
    background-color: #f8f9fa;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e9ecef;
}

.modal-title {
    font-size: 24px;
    font-weight: 700;
    color: #343a40;
    margin: 0;
}

.close-btn {
    background: none;
    border: none;
    font-size: 28px;
    color: #adb5bd;
    cursor: pointer;
    transition: color 0.3s;
}

.close-btn:hover {
    color: #495057;
}

.modal-body {
    padding: 24px;
}

.sheet-info {
    text-align: center;
    margin-bottom: 24px;
}

.sheet-image {
    width: 140px;
    height: 140px;
    object-fit: cover;
    border-radius: 12px;
    margin: 0 auto 16px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: block;
}

.sheet-title {
    font-size: 22px;
    font-weight: 700;
    color: #343a40;
    margin-bottom: 16px;
}

.info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    padding: 16px;
    border-radius: 8px;
}

.info-item {
    display: flex;
    flex-direction: column;
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.info-label {
    padding: 8px;
    font-weight: 600;
    color: #ffffff;
    background-color: #6c757d;
    text-align: center;
}

.info-value {
    color: #343a40;
    padding: 8px;
    min-height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.info-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
}
.tier-icon {
    width: 24px;
    height: 24px;
}

.section-title {
    font-size: 20px;
    font-weight: 700;
    color: #343a40;
    margin-bottom: 16px;
    border-bottom: 2px solid #e9ecef;
    padding-bottom: 8px;
}

.players-info {
    margin-bottom: 24px;
}

.players-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.player-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px;
    border-radius: 12px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.player-box.winner {
    background-color: rgba(40, 167, 69, 0.1);
}

.player-box.loser {
    background-color: rgba(220, 53, 69, 0.1);
}

.profile-image-container {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    margin-bottom: 12px;
    border: 3px solid #e9ecef;
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-info {
    text-align: center;
}

.nickname {
    font-weight: 600;
    color: #343a40;
    margin: 0 0 4px;
}

.score {
    color: #6c757d;
    margin: 0 0 4px;
}

.result-text {
    font-weight: 700;
    margin: 0;
}

.winner .result-text {
    color: #28a745;
}

.loser .result-text {
    color: #dc3545;
}

.battle-result {
    padding: 20px;
    border-radius: 12px;
    text-align: center;
}

.result {
    font-size: 28px;
    font-weight: 700;
    margin: 0;
}
</style>
