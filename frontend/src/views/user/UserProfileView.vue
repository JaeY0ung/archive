<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { userPageService } from "@/api/user-page.js";
import SmallSheetCardSinglePlay from "@/common/sheet/SmallSheetCardForSinglePlayProfile.vue";
import SmallSheetCardMultiPlay from "@/common/sheet/SmallSheetCardForMultiPlayProfile.vue";
import SmallSheetCardLike from "@/common/sheet/SmallSheetCardForLikeProfile.vue";
import FollowModal from "@/common/modal/FollowModal.vue";
import Profile from "@/common/icons/Profile.vue";

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();
const { userInfo } = storeToRefs(userStore);

// 유저 관련
const userProfile = ref(null);
const userImg = ref("");
const userNotFound = ref(false);

// 팔로우 관련
const followersCount = ref(0);
const followingsCount = ref(0);
const isFollowing = ref(false);
const showFollowersModal = ref(false);
const showFollowingsModal = ref(false);
const followList = ref([]);
const followModalTitle = ref("");

// 악보 관련
const singlePlaySheets = ref([]);
const multiPlaySheets = ref([]);
const likedSheets = ref([]);

const isOwnProfile = computed(() => {
    return userInfo.value?.nickname === route.params.nickName;
});

const goToEditPage = () => {
    router.push("/mypage");
};

// 유저 프로필 정보
const fetchUserProfile = async () => {
    try {
        const data = await userPageService.fetchUserProfile(route.params.nickName);
        if (data) {
            userProfile.value = data;
            userImg.value = `data:image/jpeg;base64,${data.userImg}`;
            userNotFound.value = false;
            // 프로필 정보를 가져온 후 악보
            await fetchSinglePlaySheets(data.userId);
            await fetchMultiPlaySheets(data.userId);
            await fetchLikedSheets(data.userId);
        } else {
            userNotFound.value = true;
        }
    } catch (error) {
        console.error("사용자 프로필을 가져오는데 실패했습니다:", error);
        userNotFound.value = true;
    }
};

// 싱글 플레이 악보 정보
const fetchSinglePlaySheets = async (userId) => {
    singlePlaySheets.value = await userPageService.fetchSinglePlaySheets(userId);
};

// 멀티 플레이 악보 정보
const fetchMultiPlaySheets = async (userId) => {
    multiPlaySheets.value = await userPageService.fetchMultiPlaySheets(userId);
};

// 좋아요한 악보 정보
const fetchLikedSheets = async (userId) => {
    likedSheets.value = await userPageService.fetchLikedSheets(userId);
};

// 팔로우 정보
const fetchFollowInfo = async () => {
    const followInfo = await userPageService.fetchFollowInfo(route.params.nickName);
    followersCount.value = followInfo.followersCount;
    followingsCount.value = followInfo.followingsCount;
    isFollowing.value = followInfo.followers.some(
        (follower) => follower.nickname === userInfo.value.nickname
    );
};

const toggleFollow = async () => {
    if (!userInfo.value || !userInfo.value.id) {
        console.error("로그인한 사용자 정보가 없습니다.");
        return;
    }

    try {
        await userPageService.toggleFollow(userProfile.value.userId, isFollowing.value);
        isFollowing.value = !isFollowing.value;
        await fetchFollowInfo();
    } catch (error) {
        console.error("팔로우/언팔로우에 실패했습니다:", error);
    }
};

const openFollowModal = async (type) => {
    try {
        followList.value = await userPageService.fetchFollowList(type, route.params.nickName);
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
    <div
        class="flex flex-col flex-grow w-full items-center justify-center h-[calc(100vh-60px)] overflow-hidden py-4"
    >
        <div class="w-[95%] h-full flex flex-col">
            <div v-if="userNotFound" class="flex-grow flex items-center justify-center">
                <div class="text-center">
                    <h2 class="text-2xl font-bold text-red-600 mb-4">사용자를 찾을 수 없습니다</h2>
                    <p class="text-gray-700 mb-6">
                        요청하신 프로필을 찾을 수 없습니다. 다시 확인해 주세요.
                    </p>
                    <button
                        class="btn back-btn px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition duration-300"
                        @click="$router.push('/')"
                    >
                        메인으로 돌아가기
                    </button>
                </div>
            </div>

            <template v-else>
                <div
                    class="flex-shrink-0 w-full rounded-t-xl shadow-[0_4px_15px_0_rgba(0,0,0,0.1)]"
                >
                    <div
                        class="flex w-full h-[200px] justify-between items-center p-[30px] rounded-t-xl bg-white bg-opacity-80"
                    >
                        <div
                            class="w-[140px] h-[140px] min-w-[140px] bg-red-300 rounded-full overflow-hidden shadow-[0_2px_5px_0_rgba(0,0,0,0.1)]"
                        >
                            <img
                                v-if="isValidUserImg"
                                :src="userImg"
                                alt="User Profile"
                                class="w-full h-full object-cover"
                                @error="handleImageError"
                            />
                            <Profile
                                v-else
                                class="w-full h-full flex items-center justify-center text-gray-500 text-5xl"
                            />
                        </div>
                        <div class="flex-grow ml-[30px]">
                            <h2 class="text-3xl font-semibold text-gray-800 mb-4">
                                {{ userProfile?.nickname }}
                            </h2>
                            <div class="flex gap-8">
                                <div class="text-center">
                                    <span class="block text-2xl font-bold text-gray-700">{{
                                        userProfile?.singleScore
                                    }}</span>
                                    <span class="text-sm text-gray-500">Score</span>
                                </div>
                                <div
                                    class="text-center cursor-pointer hover:text-blue-500 transition duration-300"
                                    @click="openFollowModal('followers')"
                                >
                                    <span class="block text-2xl font-bold text-gray-700">{{
                                        followersCount
                                    }}</span>
                                    <span class="text-sm text-gray-500">Followers</span>
                                </div>
                                <div
                                    class="text-center cursor-pointer hover:text-blue-500 transition duration-300"
                                    @click="openFollowModal('followings')"
                                >
                                    <span class="block text-2xl font-bold text-gray-700">{{
                                        followingsCount
                                    }}</span>
                                    <span class="text-sm text-gray-500">Following</span>
                                </div>
                            </div>
                        </div>
                        <div class="flex flex-col gap-2">
                            <template v-if="!isOwnProfile">
                                <button
                                    :class="[
                                        'btn',
                                        'w-24',
                                        'px-4 py-2 rounded font-semibold transition duration-300',
                                        isFollowing
                                            ? 'bg-gray-400 hover:bg-gray-500'
                                            : 'bg-blue-500 text-white hover:bg-blue-600',
                                    ]"
                                    @click="toggleFollow"
                                >
                                    {{ isFollowing ? "Unfollow" : "Follow" }}
                                </button>
                                <button
                                    class="btn px-4 py-2 rounded font-semibold bg-red-500 hover:bg-red-600 text-white transition duration-300"
                                >
                                    Fight
                                </button>
                            </template>
                            <button
                                v-else
                                class="btn px-4 py-2 rounded font-semibold bg-green-500 hover:bg-green-600 text-white transition duration-300"
                                @click="goToEditPage"
                            >
                                Edit Profile
                            </button>
                        </div>
                    </div>
                </div>

                <div
                    class="flex-grow overflow-y-auto bg-white rounded-b-xl shadow-[0_4px_15px_0_rgba(0,0,0,0.1)] custom-scrollbar"
                >
                    <div class="p-5">
                        <h3
                            class="text-xl font-semibold text-gray-800 mb-1 pb-1 border-b-2 border-gray-300"
                        >
                            싱글 플레이
                        </h3>
                        <div
                            v-if="singlePlaySheets.length > 0"
                            class="w-full h-[100px] relative overflow-x-auto custom-scrollbar-x"
                        >
                            <div class="flex absolute">
                                <SmallSheetCardSinglePlay
                                    v-for="sheet in singlePlaySheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                    class="mr-4"
                                />
                            </div>
                        </div>
                        <div v-else class="text-center text-gray-500 py-4">
                            해당하는 악보가 없습니다.
                        </div>
                    </div>

                    <div class="p-5">
                        <h3
                            class="text-xl font-semibold text-gray-800 mb-1 pb-1 border-b-2 border-gray-300"
                        >
                            대결 플레이
                        </h3>
                        <div
                            v-if="multiPlaySheets.length > 0"
                            class="w-full h-[100px] relative overflow-x-auto custom-scrollbar-x"
                        >
                            <div class="flex absolute">
                                <SmallSheetCardMultiPlay
                                    v-for="sheet in multiPlaySheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                    class="mr-4"
                                />
                            </div>
                        </div>
                        <div v-else class="text-center text-gray-500 py-4">
                            해당하는 악보가 없습니다.
                        </div>
                    </div>

                    <div class="p-5">
                        <h3
                            class="text-xl font-semibold text-gray-800 mb-1 pb-1 border-b-2 border-gray-300"
                        >
                            좋아요
                        </h3>
                        <div
                            v-if="likedSheets.length > 0"
                            class="w-full h-[100px] relative overflow-x-auto custom-scrollbar-x"
                        >
                            <div class="flex absolute">
                                <SmallSheetCardLike
                                    v-for="sheet in likedSheets"
                                    :key="sheet.id"
                                    :sheet="sheet"
                                    class="mr-4"
                                />
                            </div>
                        </div>
                        <div v-else class="text-center text-gray-500 py-4">
                            해당하는 악보가 없습니다.
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

* {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

*::-webkit-scrollbar {
    display: none;
}

html,
body {
    overflow: hidden;
}

div {
    font-family: "Roboto", sans-serif;
}

.profile-container {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.profile-image-container {
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
