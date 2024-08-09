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
                            <p class="single-score">Score: {{ userProfile?.singleScore }}</p>
                            <div class="follow-info">
                                <span class="followers" @click="openFollowModal('followers')">
                                    <strong>{{ followersCount }}</strong> Followers
                                </span>
                                <span class="following" @click="openFollowModal('followings')">
                                    <strong>{{ followingsCount }}</strong> Following
                                </span>
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

                <div class="sheet-sections">
                    <div class="sheet-section">
                        <h3>최근 싱글 플레이</h3>
                        <div class="scroll-container-wrapper">
                            <div class="scroll-container">
                                <SmallSheetCard
                                    v-for="sheet in mockRecentPlayedSheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                />
                            </div>
                        </div>
                    </div>

                    <div class="sheet-section">
                        <h3>최근 대결 플레이</h3>
                        <div class="scroll-container-wrapper">
                            <div class="scroll-container">
                                <SmallSheetCard
                                    v-for="sheet in mockRecentBattleSheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                />
                            </div>
                        </div>
                    </div>

                    <div class="sheet-section">
                        <h3>좋아요한 악보</h3>
                        <div class="scroll-container-wrapper">
                            <div class="scroll-container">
                                <SmallSheetCard
                                    v-for="sheet in mockLikedSheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                />
                            </div>
                        </div>
                    </div>
                </div>

                <FollowModal
                    v-if="showFollowersModal || showFollowingsModal"
                    :title="followModalTitle"
                    :follow-list="followList"
                    @close="closeModal"
                />
            </template>
        </div>
    </div>
</template>

<style scoped>
@import url("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css");
.profile-page-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.profile-container {
    width: 90%;
    max-width: 1100px;
    height: 600px;
    background-color: #ffffffc2;
    border-radius: 10px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.profile-container::-webkit-scrollbar {
    display: none;
}

.user-profile {
    padding: 20px;
    margin-bottom: 20px;
    border-bottom: 1px solid #e0e0e0;
}

.profile-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.profile-image-container {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-image,
.profile-icon {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-icon {
    background-color: #e9ecef;
    display: flex;
    justify-content: center;
    align-items: center;
    color: #6c757d;
}

.profile-info {
    flex-grow: 1;
}

.user-name {
    font-size: 1.3em;
    font-weight: bold;
    margin-bottom: 5px;
    color: #343a40;
}

.single-score {
    font-size: 1em;
    color: #6c757d;
    margin-bottom: 10px;
}

.follow-info {
    display: flex;
    gap: 20px;
    font-size: 0.9em;
}

.followers,
.following {
    cursor: pointer;
    color: #495057;
}

.followers:hover,
.following:hover {
    text-decoration: underline;
}

.profile-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.btn {
    padding: 6px 12px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
    font-size: 0.9em;
    transition: background-color 0.3s, transform 0.1s;
}

.btn:hover {
    transform: translateY(-2px);
}

.follow-btn {
    background-color: #007bff;
    color: white;
}

.unfollow-btn {
    background-color: #6c757d;
    color: white;
}

.fight-btn {
    background-color: #dc3545;
    color: white;
}

.edit-btn {
    background-color: #28a745;
    color: white;
}

.sheet-sections {
    padding: 0 20px;
}

.sheet-section {
    margin-bottom: 20px;
}

h3 {
    margin-bottom: 10px;
    font-size: 1.1em;
    color: #343a40;
}

.scroll-container-wrapper {
    width: 100%;
    overflow-x: hidden;
}

.scroll-container {
    display: flex;
    overflow-x: auto;
    gap: 15px;
    padding-bottom: 10px;
    scroll-behavior: smooth;
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.scroll-container::-webkit-scrollbar {
    display: none;
}

.user-not-found {
    text-align: center;
    padding: 50px;
}

.user-not-found h2 {
    color: #dc3545;
    font-size: 1.5em;
    margin-bottom: 15px;
}

.user-not-found p {
    color: #6c757d;
    margin-bottom: 20px;
}

.back-btn {
    background-color: #007bff;
    color: white;
}

.back-btn:hover {
    background-color: #0056b3;
}

</style>
