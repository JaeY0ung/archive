<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import FollowModal from "@/common/modal/FollowModal.vue";
import Profile from "@/common/icons/Profile.vue";

const local = localAxios();
const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

const { userInfo } = storeToRefs(userStore);
const userProfile = ref(null);
const userImg = ref("");
const followersCount = ref(0);
const followingsCount = ref(0);
const isFollowing = ref(false);
const showFollowersModal = ref(false);
const showFollowingsModal = ref(false);
const followList = ref([]);
const followModalTitle = ref("");
const userNotFound = ref(false);

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

const isOwnProfile = computed(() => {
    return userInfo.value?.nickname === route.params.nickName;
});

const goToEditPage = () => {
    router.push("/mypage");
};

// 유저 프로필 정보
const fetchUserProfile = async () => {
    try {
        const response = await local.get(`/users/${route.params.nickName}`);
        if (response.data) {
            userProfile.value = response.data;
            userImg.value = `data:image/jpeg;base64,${response.data.userImg}`;
            userNotFound.value = false;
        } else {
            userNotFound.value = true;
        }
    } catch (error) {
        console.error("사용자 프로필을 가져오는데 실패했습니다:", error);
        userNotFound.value = true;
    }
};

// 팔로우 정보
const fetchFollowInfo = async () => {
    try {
        const [followersResponse, followingsResponse] = await Promise.all([
            local.get(`/follows/followers/${route.params.nickName}`),
            local.get(`/follows/followings/${route.params.nickName}`),
        ]);

        followersCount.value = followersResponse.data.length;
        followingsCount.value = followingsResponse.data.length;

        isFollowing.value = followersResponse.data.some(
            (follower) => follower.nickname === userInfo.value.nickname
        );
    } catch (error) {
        console.error("팔로우 정보를 가져오는 데 실패했습니다:", error);
        followersCount.value = 0;
        followingsCount.value = 0;
        isFollowing.value = false;
    }
};

const toggleFollow = async () => {
    if (!userInfo.value || !userInfo.value.id) {
        console.error("로그인한 사용자 정보가 없습니다.");
        return;
    }

    try {
        if (isFollowing.value) {
            await local.delete(`/follows/followings/${userProfile.value.userId}`);
        } else {
            await local.post(`/follows/followings/${userProfile.value.userId}`);
        }
        isFollowing.value = !isFollowing.value;
        await fetchFollowInfo();
    } catch (error) {
        console.error("팔로우/언팔로우에 실패했습니다:", error);
        isFollowing.value = !isFollowing.value;
    }
};

const openFollowModal = async (type) => {
    try {
        const response = await local.get(`/follows/${type}/${route.params.nickName}`);
        followList.value = response.data;
        followModalTitle.value = type === "followers" ? "Followers" : "Following";
        type === "followers"
            ? (showFollowersModal.value = true)
            : (showFollowingsModal.value = true);
    } catch (error) {
        console.error(`${type} 목록을 가져오는데 실패했습니다:`, error);
    }
};

const closeModal = () => {
    showFollowersModal.value = false;
    showFollowingsModal.value = false;
};

const isValidUserImg = computed(() => {
    return (
        userImg.value && userImg.value.trim().length > 0 && !userImg.value.endsWith("base64,null")
    );
});

const handleImageError = () => {
    userImg.value = "";
};

onMounted(async () => {
    await fetchUserProfile();
    if (!userNotFound.value) {
        await fetchFollowInfo();
    }
});
</script>

<template>
    <div class="profile-page-container">
        <div class="profile-container">
            <div v-if="userNotFound" class="user-not-found">
                <h2>사용자를 찾을 수 없습니다</h2>
                <p>요청하신 프로필을 찾을 수 없습니다. 다시 확인해 주세요.</p>
                <button class="btn back-btn" @click="$router.push('/')">메인으로 돌아가기</button>
            </div>

            <template v-else>
                <div class="user-profile">
                    <div class="profile-header">
                        <div class="profile-image-container">
                            <img
                                v-if="isValidUserImg"
                                :src="userImg"
                                alt="User Profile"
                                class="profile-image"
                                @error="handleImageError"
                            />
                            <Profile v-else class="profile-icon" />
                        </div>
                        <div class="profile-info">
                            <h2 class="user-name">{{ userProfile?.nickname }}</h2>
                            <div class="stats-container">
                                <div class="stat-item">
                                    <span class="stat-value">{{ userProfile?.singleScore }}</span>
                                    <span class="stat-label">Score</span>
                                </div>
                                <div
                                    class="stat-item clickable"
                                    @click="openFollowModal('followers')"
                                >
                                    <span class="stat-value">{{ followersCount }}</span>
                                    <span class="stat-label">Followers</span>
                                </div>
                                <div
                                    class="stat-item clickable"
                                    @click="openFollowModal('followings')"
                                >
                                    <span class="stat-value">{{ followingsCount }}</span>
                                    <span class="stat-label">Following</span>
                                </div>
                            </div>
                        </div>
                        <div class="profile-actions">
                            <template v-if="!isOwnProfile">
                                <button
                                    :class="['btn', 'follow-btn', { 'unfollow-btn': isFollowing }]"
                                    @click="toggleFollow"
                                >
                                    {{ isFollowing ? "Unfollow" : "Follow" }}
                                </button>
                                <button class="btn fight-btn">Fight</button>
                            </template>
                            <button v-else class="btn edit-btn" @click="goToEditPage">
                                Edit Profile
                            </button>
                        </div>
                    </div>
                </div>

                <div class="sheet-sections">
                    <div class="sheet-section">
                        <h3>최근 싱글 플레이</h3>
                        <div class="scroll-container">
                            <SmallSheetCard
                                v-for="sheet in mockRecentPlayedSheets"
                                :key="sheet.id"
                                :sheet="sheet"
                            />
                        </div>
                    </div>

                    <div class="sheet-section">
                        <h3>최근 대결 플레이</h3>
                        <div class="scroll-container">
                            <SmallSheetCard
                                v-for="sheet in mockRecentBattleSheets"
                                :key="sheet.id"
                                :sheet="sheet"
                            />
                        </div>
                    </div>

                    <div class="sheet-section">
                        <h3>좋아요한 악보</h3>
                        <div class="scroll-container">
                            <SmallSheetCard
                                v-for="sheet in mockLikedSheets"
                                :key="sheet.id"
                                :sheet="sheet"
                            />
                        </div>
                    </div>
                </div>
            </template>
        </div>

        <FollowModal
            v-if="showFollowersModal || showFollowingsModal"
            :title="followModalTitle"
            :follow-list="followList"
            @close="closeModal"
        />
    </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700&family=Poppins:wght@300;400;600&display=swap");
@import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css");

.profile-page-container {
    width: 100%;
    height: 100%;
    max-width: 1000px;
    max-height: 600px;
    display: flex;
    box-sizing: border-box;
    margin: 0 auto;
    padding: 20px;
    font-family: "Roboto", sans-serif;
    overflow: auto;
}

.profile-container {
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 100%;
    overflow: auto; 
    scrollbar-width: none;
    -ms-overflow-style: none; 
}

.user-profile {
    padding: 30px;
    background-color: #f8f9fa;
    border-bottom: 1px solid #e0e0e0;
}

.profile-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.profile-image-container {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.profile-image,
.profile-icon {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-icon {
    background-color: #e0e0e0;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #6c757d;
    font-size: 40px;
}

.profile-info {
    flex-grow: 1;
    margin-left: 30px;
}

.user-name {
    font-family: "Poppins", sans-serif;
    font-size: 2em;
    font-weight: 600;
    margin-bottom: 15px;
    color: #2c3e50;
}

.stats-container {
    display: flex;
    gap: 30px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-value {
    font-size: 1.4em;
    font-weight: 600;
    color: #2c3e50;
}

.stat-label {
    font-size: 0.9em;
    color: #7f8c8d;
    margin-top: 5px;
}

.clickable {
    cursor: pointer;
    transition: all 0.3s ease;
}

.clickable:hover {
    transform: translateY(-2px);
}

.clickable:hover .stat-value {
    color: #3498db;
}

.profile-actions {
    display: flex;
    gap: 10px;
}

.btn {
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: 600;
    font-size: 1em;
    transition: all 0.3s;
    font-family: "Poppins", sans-serif;
}

.btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.follow-btn,
.unfollow-btn,
.fight-btn,
.edit-btn {
    display: flex;
    align-items: center;
}

.follow-btn::before,
.unfollow-btn::before,
.fight-btn::before,
.edit-btn::before {
    font-family: "Font Awesome 6 Free";
    font-weight: 900;
    margin-right: 8px;
}

.follow-btn {
    background-color: #3498db;
    color: white;
}

.follow-btn::before {
    content: "\f234";
}

.unfollow-btn {
    background-color: #95a5a6;
    color: white;
}

.unfollow-btn::before {
    content: "\f503";
}

.fight-btn {
    background-color: #e74c3c;
    color: white;
}

.fight-btn::before {
    content: "\f6de";
}

.edit-btn {
    background-color: #2ecc71;
    color: white;
}

.edit-btn::before {
    content: "\f304";
}

.sheet-sections {
    padding: 30px;
}

.sheet-section {
    margin-bottom: 40px;
}

.sheet-section h3 {
    margin-bottom: 20px;
    font-size: 1.4em;
    color: #2c3e50;
    font-family: "Poppins", sans-serif;
    font-weight: 600;
    border-bottom: 2px solid #000000;
    padding-bottom: 10px;
    display: inline-block;
}

.scroll-container {
    display: flex;
    overflow-x: auto;
    gap: 20px;
    padding-bottom: 20px;
    scrollbar-width: none; 
    -ms-overflow-style: none;
}

.scroll-container::-webkit-scrollbar {
    display: none;
}

.scroll-container::-webkit-scrollbar-track {
    background: #e0e0e0;
    border-radius: 4px;
}

.scroll-container::-webkit-scrollbar-thumb {
    background-color: #3498db;
    border-radius: 4px;
}

.user-not-found {
    text-align: center;
    padding: 50px;
}

.user-not-found h2 {
    color: #e74c3c;
    font-size: 1.8em;
    margin-bottom: 20px;
    font-family: "Poppins", sans-serif;
    font-weight: 600;
}

.user-not-found p {
    color: #34495e;
    margin-bottom: 25px;
    font-size: 1.1em;
}

.back-btn {
    background-color: #3498db;
    color: white;
}

.back-btn:hover {
    background-color: #2980b9;
}

.back-btn::before {
    content: "\f060";
    font-family: "Font Awesome 6 Free";
    font-weight: 900;
    margin-right: 8px;
}
</style>
