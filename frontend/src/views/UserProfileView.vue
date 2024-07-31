<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const local = localAxios();
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const nickname = ref("");
const singleScore = ref(0);
const followersCount = ref(0);
const followingsCount = ref(0);

const mockRecentPlayedSheets = ref([
    { id: 1, title: "곡 제목 1", artist: "아티스트 1", imageUrl: "placeholder-image-1.jpg" },
    { id: 2, title: "곡 제목 2", artist: "아티스트 2", imageUrl: "placeholder-image-2.jpg" },
    { id: 3, title: "곡 제목 3", artist: "아티스트 3", imageUrl: "placeholder-image-3.jpg" },
    { id: 4, title: "곡 제목 4", artist: "아티스트 4", imageUrl: "placeholder-image-4.jpg" },
    { id: 5, title: "곡 제목 5", artist: "아티스트 5", imageUrl: "placeholder-image-5.jpg" },
]);

const mockRecentBattleSheets = ref([...mockRecentPlayedSheets.value]);
const mockLikedSheets = ref([...mockRecentPlayedSheets.value]);

// 팔로워와 팔로잉 정보를 가져오는 함수
const fetchFollowInfo = async () => {
    try {
        const [followersResponse, followingsResponse] = await Promise.all([
            local.get("/follows/followers"),
            local.get("/follows/followings"),
        ]);

        followersCount.value = followersResponse.data.length;
        followingsCount.value = followingsResponse.data.length;
    } catch (error) {
        console.error("팔로우 정보를 가져오는 데 실패했습니다:", error);
    }
};

onMounted(async () => {
    if (userInfo.value) {
        nickname.value = userInfo.value.nickname;
        singleScore.value = userInfo.value.singleScore;
    }
    await fetchFollowInfo();

    // 스크롤 이벤트 리스너 추가
    const scrollContainers = document.querySelectorAll(".scroll-x");
    scrollContainers.forEach((container) => {
        container.addEventListener("mousedown", (e) => startDragging(e, container));
        container.addEventListener("mouseleave", () => stopDragging(container));
        container.addEventListener("mouseup", () => stopDragging(container));
        container.addEventListener("mousemove", (e) => doDrag(e, container));
    });
});

onUnmounted(() => {
    cancelMomentumTracking();
});

// 개선된 드래그 스크롤 기능
let isDragging = false;
let startX, scrollLeft;
let momentumID;

const startDragging = (e, el) => {
    isDragging = true;
    startX = e.pageX - el.offsetLeft;
    scrollLeft = el.scrollLeft;
    cancelMomentumTracking();
};

const stopDragging = (el) => {
    isDragging = false;
    beginMomentumTracking(el);
};

const doDrag = (e, el) => {
    if (!isDragging) return;
    e.preventDefault();
    const x = e.pageX - el.offsetLeft;
    const walk = (x - startX) * 2;
    el.scrollLeft = scrollLeft - walk;
};

// 관성 스크롤
let velX = 0;
let amplitude = 0;
let frame = 0;
let timestamp = 0;

const beginMomentumTracking = (el) => {
    cancelMomentumTracking();
    timestamp = Date.now();
    frame = el.scrollLeft;
    momentumID = requestAnimationFrame(() => autoScroll(el));
};

const cancelMomentumTracking = () => {
    cancelAnimationFrame(momentumID);
};

const autoScroll = (el) => {
    const elapsed = Date.now() - timestamp;
    if (elapsed > 1000) return; // 1초 후 자동 스크롤 중지

    const delta = el.scrollLeft - frame;
    frame = el.scrollLeft;

    const v = (1000 * delta) / (1 + elapsed);
    velX = 0.8 * v + 0.2 * velX;

    if (Math.abs(velX) > 0.1) {
        el.scrollLeft += (velX * 16) / 1000;
        momentumID = requestAnimationFrame(() => autoScroll(el));
    }
};
</script>

<template>
    <div class="profile-container">
        <div class="user-profile">
            <img src="placeholder-profile-image.jpg" alt="User Profile" class="profile-image" />
            <span class="user-name">{{ nickname }}</span>
            <span class="single-score">Score: {{ singleScore }}</span>
            <span class="followers">Followers: {{ followersCount }}</span>
            <span class="following">Following: {{ followingsCount }}</span>
            <button class="fight-btn">Fight</button>
            <button class="follow-btn">Follow</button>
        </div>

        <div class="sheets-container">
            <div class="sheet-section">
                <h3>최근 싱글 플레이</h3>
                <div class="scroll-x">
                    <div
                        v-for="sheet in mockRecentPlayedSheets"
                        :key="sheet.id"
                        class="card-wrapper"
                    >
                        <SmallSheetCard :sheet="sheet" />
                    </div>
                </div>
            </div>

            <div class="sheet-section">
                <h3>최근 대결 플레이</h3>
                <div class="scroll-x">
                    <div
                        v-for="sheet in mockRecentBattleSheets"
                        :key="sheet.id"
                        class="card-wrapper"
                    >
                        <SmallSheetCard :sheet="sheet" />
                    </div>
                </div>
            </div>

            <div class="sheet-section">
                <h3>좋아요한 악보</h3>
                <div class="scroll-x">
                    <div v-for="sheet in mockLikedSheets" :key="sheet.id" class="card-wrapper">
                        <SmallSheetCard :sheet="sheet" />
                    </div>
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
    cursor: grab;
    user-select: none;
    scroll-behavior: smooth;
    -webkit-overflow-scrolling: touch;
}

.scroll-x:active {
    cursor: grabbing;
}

.card-wrapper {
    flex: 0 0 auto;
    width: 200px; /* Adjust this value based on your SmallSheetCard width */
}

.scroll-x::-webkit-scrollbar {
    height: 8px;
}

.scroll-x::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.scroll-x::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
}

.scroll-x::-webkit-scrollbar-thumb:hover {
    background: #555;
}

h3 {
    margin-bottom: 10px;
    font-size: 1.2em;
    color: #333;
}
</style>
