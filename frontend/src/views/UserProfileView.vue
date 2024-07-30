<script setup>
import { ref } from "vue";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const local = localAxios();
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

// 스토어에서 유저 인포 데이터
const nickname = userInfo.value.nickname;
const singleScore = userInfo.value.singleScore;

// 팔로우 팔로잉 데이터

// 최근 싱글 플레이 데이터
const mockRecentPlayedSheets = ref([
    { id: 1, title: "곡 제목 1", artist: "아티스트 1", imageUrl: "placeholder-image-1.jpg" },
    { id: 2, title: "곡 제목 2", artist: "아티스트 2", imageUrl: "placeholder-image-2.jpg" },
    { id: 3, title: "곡 제목 3", artist: "아티스트 3", imageUrl: "placeholder-image-3.jpg" },
    { id: 4, title: "곡 제목 4", artist: "아티스트 4", imageUrl: "placeholder-image-4.jpg" },
    { id: 5, title: "곡 제목 5", artist: "아티스트 5", imageUrl: "placeholder-image-5.jpg" },
]);

// 대결 곡과 좋아요한 악보에도 동일한 목업 데이터 사용
const mockRecentBattleSheets = ref([...mockRecentPlayedSheets.value]);
const mockLikedSheets = ref([...mockRecentPlayedSheets.value]);
</script>

<template>
    <div class="profile-container">
        <div class="user-profile">
            <img src="placeholder-profile-image.jpg" alt="User Profile" class="profile-image" />
            <span class="user-name">{{ nickname }}</span>
            <span class="single-score">Score: {{ singleScore }}</span>
            <span class="followers">Followers: 100</span>
            <span class="following">Following: 50</span>
            <button class="fight-btn">Fight</button>
            <button class="follow-btn">Follow</button>
        </div>

        <div class="sheets-container">
            <div class="sheet-section">
                <h3>최근 싱글 플레이</h3>
                <div class="scroll-x flex">
                    <SmallSheetCard
                        v-for="sheet in mockRecentPlayedSheets"
                        :key="sheet.id"
                        :sheet="sheet"
                    />
                </div>
            </div>

            <div class="sheet-section">
                <h3>최근 대결 플레이</h3>
                <div class="scroll-x flex">
                    <SmallSheetCard
                        v-for="sheet in mockRecentBattleSheets"
                        :key="sheet.id"
                        :sheet="sheet"
                    />
                </div>
            </div>

            <div class="sheet-section">
                <h3>좋아요한 악보</h3>
                <div class="scroll-x flex">
                    <SmallSheetCard
                        v-for="sheet in mockLikedSheets"
                        :key="sheet.id"
                        :sheet="sheet"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.profile-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 50px;
    width: 100%;
}

.user-profile {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 10px 20px;
    background-color: #f0f0f0;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.profile-image {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    object-fit: cover;
}

.user-name {
    font-weight: bold;
    font-size: 1.2em;
}

.user-id {
    color: #777;
    font-size: 0.9em;
}

.single-score,
.followers,
.following {
    color: #555;
}

.fight-btn,
.follow-btn {
    padding: 5px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
}

.fight-btn {
    background-color: #ff4444;
    color: white;
}

.follow-btn {
    background-color: #4444ff;
    color: white;
}

.sheets-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 20px;
}

.sheet-section {
    width: 100%;
    padding: 20px;
    background-color: rgb(255, 255, 255, 0.5);
    border-radius: 15px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.scroll-x {
    display: flex;
    overflow-x: auto;
    gap: 15px;
    padding: 10px 0;
}

h3 {
    margin-bottom: 10px;
    font-size: 1.2em;
    color: #333;
}
</style>
