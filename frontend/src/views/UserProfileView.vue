<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";

const local = localAxios();
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);
const route = useRoute();
const router = useRouter();

// 유저 정보
const userProfile = ref(null);
const followersCount = ref(0);
const followingsCount = ref(0);

const isOwnProfile = computed(() => {
    return userInfo.value?.nickname === route.params.nickName;
});

const fetchUserProfile = async () => {
    try {
        const response = await local.get(`/users/${route.params.nickName}`);
        userProfile.value = response.data;
        console.log("가져온 유저 데이터입니다.", response.data);
    } catch (error) {
        console.error("사용자 프로필을 가져오는데 실패했습니다:", error);
    }
};

// 악보 정보
const mockRecentPlayedSheets = ref([
    {
        id: 1,
        title: "곡 제목 1",
        songComposer: "아티스트 1",
        imageUrl: "https://www.spochoo.com/news/photo/202307/105812_212618_410.jpg",
    },
    {
        id: 2,
        title: "곡 제목 2",
        songComposer: "아티스트 2",
        imageUrl: "https://www.spochoo.com/news/photo/202307/105812_212618_410.jpg",
    },
    {
        id: 3,
        title: "곡 제목 3",
        songComposer: "아티스트 3",
        imageUrl: "https://www.spochoo.com/news/photo/202307/105812_212618_410.jpg",
    },
    {
        id: 4,
        title: "곡 제목 4",
        songComposer: "아티스트 4",
        imageUrl: "https://www.spochoo.com/news/photo/202307/105812_212618_410.jpg",
    },
    {
        id: 5,
        title: "곡 제목 5",
        songComposer: "아티스트 5",
        imageUrl: "https://www.spochoo.com/news/photo/202307/105812_212618_410.jpg",
    },
]);

const mockRecentBattleSheets = ref([...mockRecentPlayedSheets.value]);
const mockLikedSheets = ref([...mockRecentPlayedSheets.value]);

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
        followersCount.value = 0;
        followingsCount.value = 0;
    }
};

const followUser = async () => {
    if (!userInfo.value || !userInfo.value.id) {
        console.error("로그인한 사용자 정보가 없습니다.");
        return;
    }

    try {
        await local.post(`/follows/followings/${userInfo.value.id}`);
        console.log("팔로우 성공");
        // 팔로우 성공 후 팔로워 수 업데이트
        await fetchFollowInfo();
    } catch (error) {
        console.error("팔로우에 실패했습니다:", error);
    }
};

const goToEditPage = () => {
    router.push("/mypage");
};

onMounted(async () => {
    await fetchUserProfile();
    await fetchFollowInfo();
});
</script>

<template>
    <div class="profile-container">
        <div class="user-profile">
            <img src="placeholder-profile-image.jpg" alt="User Profile" class="profile-image" />
            <span class="user-name">{{ userProfile?.nickname }}</span>
            <span class="single-score">Score: {{ userProfile?.singleScore }}</span>
            <span class="followers">Followers: {{ followersCount }}</span>
            <span class="following">Following: {{ followingsCount }}</span>
            <template v-if="!isOwnProfile">
                <button class="follow-btn" @click="followUser">Follow</button>
                <button class="fight-btn">Fight</button>
            </template>
            <button v-else class="edit-btn" @click="goToEditPage">Edit</button>
        </div>

        <div class="sheets-container">
            <div class="sheet-section">
                <h3>최근 싱글 플레이</h3>
                <div class="scroll-x">
                    <SmallSheetCard
                        v-for="sheet in mockRecentPlayedSheets"
                        :key="sheet.id"
                        :sheet="sheet"
                    />
                </div>
            </div>

            <div class="sheet-section">
                <h3>최근 대결 플레이</h3>
                <div class="scroll-x">
                    <SmallSheetCard
                        v-for="sheet in mockRecentBattleSheets"
                        :key="sheet.id"
                        :sheet="sheet"
                    />
                </div>
            </div>

            <div class="sheet-section">
                <h3>좋아요한 악보</h3>
                <div class="scroll-x">
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
    padding: 10px 0;
}

.card-wrapper {
    flex: 0 0 auto;
    width: 180px;
    margin-right: 15px;
}

h3 {
    margin-bottom: 10px;
    font-size: 1.2em;
    color: #333;
}

.edit-btn {
    padding: 5px 15px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    background-color: #44ff44;
    color: white;
}
</style>
