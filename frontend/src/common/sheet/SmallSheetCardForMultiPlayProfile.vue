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
    return (
        userImg && userImg.trim().length > 0 && !userImg.endsWith("base64,null")
    );
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
</script>

<template>
    <div>
        <div
            class="m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg cursor-pointer"
            style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3)"
            @click="openModal"
        >
            <div class="flex justify-start gap-3">
                <!-- (왼쪽) 악보 사진 -->
                <div class="h-[80px] w-[80px] flex justify-center">
                    <img
                        class="rounded-lg object-cover"
                        :src="sheetImageUrl"
                        alt="원본 곡 이미지"
                    />
                </div>

                <!-- (중앙) 악보 정보 -->
                <div class="min-w-[110px] pt-[5px] flex flex-col gap-1">
                    <div
                        class="bold flex justify-between"
                        style="font-size: 18px"
                    >
                        <div class="flex items-center">
                            {{ getTitleByLen(sheet.title, 10) }}
                        </div>
                        <div class="flex items-center">
                            <Tier
                                class="w-[18px] h-[18px]"
                                :level="sheet.level"
                            />
                        </div>
                    </div>
                    <div class="medium mb-3" style="font-size: 12px">
                        {{ sheet.songComposer }}
                    </div>
                    <div
                        class="medium flex items-center gap-1"
                        style="font-size: 12px"
                    >
                        업로더 {{ sheet.uploaderNickname }}
                    </div>
                </div>
            </div>

            <!-- (오른쪽) 점수 및 추가 기능 -->
            <div class="p-[5px] flex flex-col items-end justify-center gap-2">
                <slot />
                <div
                    :class="[
                        'p-2 rounded-full flex items-center justify-center',
                        resultStyle,
                    ]"
                >
                    <span class="font-bold text-2xl">
                        {{ battleResult }}
                    </span>
                </div>
            </div>
        </div>

        <!-- Modal -->
        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <div class="modal-grid">
                    <!-- 왼쪽 섹션 -->
                    <div class="left-section">
                        <div class="sheet-image-container">
                            <img
                                :src="sheetImageUrl"
                                alt="Sheet Image"
                                class="sheet-image"
                            />
                        </div>
                        <div class="sheet-info">
                            <h2 class="sheet-title">{{ sheet.title }}</h2>
                            <div class="info-item">
                                <span class="info-label">작곡가</span>
                                <span class="info-value">{{
                                    sheet.songComposer
                                }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">업로더</span>
                                <span class="info-value">{{
                                    sheet.uploaderNickname
                                }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">난이도</span>
                                <span class="info-value">
                                    <Tier
                                        class="w-[18px] h-[18px] inline-block"
                                        :level="sheet.level"
                                    />
                                </span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">플레이 시간</span>
                                <span class="info-value">{{
                                    formatPlayTime(sheet.playTime)
                                }}</span>
                            </div>
                        </div>
                    </div>

                    <!-- 오른쪽 섹션 -->
                    <div class="right-section">
                        <h3>플레이어 정보</h3>
                        <!-- 나의 프로필 -->
                        <div
                            class="profile-box my-profile"
                            @click="goToUserProfile(sheet.myNickname)"
                        >
                            <div class="profile-image-container">
                                <img
                                    v-if="isValidUserImg(sheet.myProfileImg)"
                                    :src="
                                        'data:image/jpeg;base64,' +
                                        sheet.myProfileImg
                                    "
                                    alt="My Profile"
                                    class="profile-image"
                                />
                                <Profile
                                    v-else
                                    class="profile-image text-gray-500"
                                />
                            </div>
                            <div class="profile-info">
                                <p class="nickname">
                                    {{ sheet.myNickname }}
                                </p>
                                <p class="score">점수 {{ sheet.myScore }}</p>
                            </div>
                        </div>

                        <!-- 상대 프로필 -->
                        <div
                            class="profile-box opponent-profile"
                            @click="goToUserProfile(sheet.otherNickname)"
                        >
                            <div class="profile-image-container">
                                <img
                                    v-if="isValidUserImg(sheet.otherProfileImg)"
                                    :src="
                                        'data:image/jpeg;base64,' +
                                        sheet.otherProfileImg
                                    "
                                    alt="Opponent Profile"
                                    class="profile-image"
                                />
                                <Profile
                                    v-else
                                    class="profile-image text-gray-500"
                                />
                            </div>
                            <div class="profile-info">
                                <p class="nickname">
                                    {{ sheet.otherNickname }}
                                </p>
                                <p class="score">
                                    점수 {{ sheet.otherScore }}
                                </p>
                            </div>
                        </div>

                        <!-- 대결 결과 -->
                        <div class="battle-result" :class="resultStyle">
                            <h3>대결 결과</h3>
                            <p>{{ battleResult }}</p>
                        </div>
                    </div>
                </div>

                <div class="button-container">
                    <button class="close-btn" @click="closeModal">닫기</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* Styles remain the same as in the original code */
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
    padding: 20px;
    border-radius: 10px;
    max-width: 600px;
    width: 90%;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
}

.left-section {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

.sheet-image-container {
    width: 60%;
    height: 0;
    padding-bottom: 60%;
    position: relative;
    overflow: hidden;
    border-radius: 6px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin: 0 auto;
}

.sheet-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.sheet-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.sheet-title {
    font-size: 22px;
    font-weight: bold;
    color: #2c3e50;
    margin-bottom: 8px;
}

.info-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.info-label {
    font-weight: bold;
    color: #34495e;
    min-width: 70px;
    font-size: 14px;
}

.info-value {
    color: #2c3e50;
    font-size: 14px;
}

.right-section {
    display: flex;
    flex-direction: column;
    gap: 15px;
    align-items: center;
    justify-content: center;
}

.profile-box {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-radius: 6px;
    background-color: #f0f0f0;
    cursor: pointer;
    transition: background-color 0.3s, transform 0.3s;
    width: 100%;
}

.profile-box:hover {
    background-color: #e0e0e0;
    transform: translateY(-2px);
}

.profile-image-container {
    width: 50px;
    height: 50px;
    border-radius: 25px;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-info {
    flex-grow: 1;
}

.profile-info .nickname {
    font-weight: bold;
    font-size: 16px;
    color: #2c3e50;
}

.profile-info .score {
    color: #7f8c8d;
    font-size: 14px;
}

.battle-result {
    padding: 12px;
    border-radius: 6px;
    width: 100%;
}

.battle-result h3 {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 8px;
}

.button-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
}

.close-btn {
    padding: 8px 16px;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    font-size: 14px;
    transition: background-color 0.3s, transform 0.3s;
}

.close-btn:hover {
    background-color: #2980b9;
    transform: translateY(-2px);
}
</style>