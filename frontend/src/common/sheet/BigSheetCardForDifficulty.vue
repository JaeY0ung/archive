<template>
    <div class="rounded-lg shadow-lg p-6 max-w-[1000px] w-full mx-auto bg-white bg-opacity-40">
        <div class="flex flex-col md:flex-row gap-6">
            <div class="w-full md:w-1/3 lg:w-1/4 flex-shrink-0">
                <div
                    v-if="sheetInfo.imageUrl"
                    class="w-full aspect-square rounded-lg shadow-md overflow-hidden"
                >
                    <img
                        :src="sheetInfo.imageUrl"
                        alt="악보 이미지"
                        class="w-full h-full object-cover"
                    />
                </div>
                <div
                    v-else
                    class="w-full aspect-square bg-gray-200 rounded-lg shadow-md flex items-center justify-center"
                >
                    <font-awesome-icon :icon="['fas', 'music']" class="text-gray-400 text-5xl" />
                </div>
            </div>

            <div class="flex-grow flex flex-col justify-between">
                <div class="space-y-4">
                    <div class="flex items-start justify-between flex-wrap gap-2">
                        <h2 class="text-2xl md:text-3xl font-bold text-gray-800 leading-tight">
                            {{ sheet.title }}
                        </h2>
                        <Tier :level="sheet.level" class="flex-shrink-0" />
                    </div>

                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div class="flex items-center space-x-2">
                            <font-awesome-icon :icon="['fas', 'pen']" class="text-gray-500" />
                            <span class="text-gray-700 font-medium">작곡가:</span>
                            <span class="text-gray-900">{{ sheet.songComposer }}</span>
                        </div>
                        <div class="flex items-center space-x-2">
                            <font-awesome-icon :icon="['fas', 'user']" class="text-gray-500" />
                            <span class="text-gray-700 font-medium">업로더:</span>
                            <span
                                class="cursor-pointer text-gray-900 hover:text-blue-600"
                                @click="goToUserProfile"
                            >
                                {{ sheet.uploaderNickname }}
                            </span>
                        </div>
                        <div class="flex items-center space-x-2">
                            <font-awesome-icon :icon="['fas', 'eye']" class="text-gray-500" />
                            <span class="text-gray-700 font-medium">조회수:</span>
                            <span class="text-gray-900">{{ sheet.viewCount }}</span>
                        </div>
                        <div class="flex items-center space-x-2 cursor-pointer" @click="toggleLike">
                            <font-awesome-icon
                                :icon="['fas', 'heart']"
                                :class="localLikeStatus ? 'text-red-500' : 'text-gray-500'"
                            />
                            <span class="text-gray-700 font-medium">좋아요:</span>
                            <span class="text-gray-900 select-none">{{ localLikeCount }}</span>
                        </div>
                        <div class="flex items-center space-x-2">
                            <font-awesome-icon :icon="['fas', 'star']" class="text-yellow-400" />
                            <span class="text-gray-700 font-medium">평점:</span>
                            <span class="text-gray-900">{{
                                starRateAvg ? starRateAvg.toFixed(2) : "미평가"
                            }}</span>
                        </div>
                        <div class="flex items-center space-x-2">
                            <font-awesome-icon
                                :icon="['fas', 'shopping-cart']"
                                class="text-gray-500"
                            />
                            <span class="text-gray-700 font-medium">가격:</span>
                            <span class="text-gray-900">{{
                                sheet.price === 0 || sheet.price === null
                                    ? "무료"
                                    : `${sheet.price} 원`
                            }}</span>
                        </div>
                    </div>
                </div>

                <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6">
                    <!-- <button
                        class="btn bg-purple-600 hover:bg-purple-700 text-white rounded-full transition-all duration-300 ease-in-out transform hover:scale-105 flex items-center justify-center relative overflow-hidden group h-12"
                        @click="goToDifficultyRatingPage"
                    >
                        <font-awesome-icon
                            :icon="['fas', 'chart-line']"
                            class="text-2xl transition-all duration-300 group-hover:opacity-0 group-hover:scale-0"
                        />
                        <span
                            class="absolute transition-all duration-300 opacity-0 scale-0 group-hover:opacity-100 group-hover:scale-100 text-xs text-center w-full px-1"
                        >
                            난이도<br />평가
                        </span>
                    </button> -->
                    <button
                        class="btn bg-gray-800 hover:bg-gray-900 text-white rounded-full transition-all duration-300 ease-in-out transform hover:scale-105 flex items-center justify-center relative overflow-hidden group h-12"
                        @click="goToPlayRoom"
                    >
                        <font-awesome-icon
                            :icon="['fas', 'play']"
                            class="text-2xl transition-all duration-300 group-hover:opacity-0 group-hover:scale-0"
                        />
                        <span
                            class="absolute transition-all duration-300 opacity-0 scale-0 group-hover:opacity-100 group-hover:scale-100 text-xs"
                        >
                            플레이
                        </span>
                    </button>
                    <button
                        class="btn bg-green-600 hover:bg-green-700 text-white rounded-full transition-all duration-300 ease-in-out transform hover:scale-105 flex items-center justify-center relative overflow-hidden group h-12"
                        :class="{ 'opacity-50 cursor-not-allowed': isCartButtonDisabled }"
                        @click="addSheetToOrder"
                        :disabled="isCartButtonDisabled"
                    >
                        <font-awesome-icon
                            :icon="['fas', 'shopping-cart']"
                            class="text-2xl transition-all duration-300 group-hover:opacity-0 group-hover:scale-0"
                        />
                        <span
                            class="absolute transition-all duration-300 opacity-0 scale-0 group-hover:opacity-100 group-hover:scale-100 text-xs"
                        >
                            {{ sheet.price === 0 || sheet.price === null ? "무료" : "장바구니" }}
                        </span>
                    </button>
                    <button
                        class="btn bg-blue-600 hover:bg-blue-700 text-white rounded-full transition-all duration-300 ease-in-out transform hover:scale-105 flex items-center justify-center relative overflow-hidden group h-12"
                        @click="downloadSheet"
                    >
                        <font-awesome-icon
                            :icon="['fas', 'download']"
                            class="text-2xl transition-all duration-300 group-hover:opacity-0 group-hover:scale-0"
                        />
                        <span
                            class="absolute transition-all duration-300 opacity-0 scale-0 group-hover:opacity-100 group-hover:scale-100 text-xs"
                        >
                            다운로드
                        </span>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <ModalComponent
        :show="showModal"
        message="장바구니에 추가되었습니다."
        primaryButtonText="장바구니로 이동"
        secondaryButtonText="계속 쇼핑하기"
        @primaryAction="goToCart"
        @secondaryAction="continueShopping"
    />
</template>

<script setup>
import { computed, ref, watch, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { showLoginRequestAlert } from "@/util/alert";
import { addToOrder } from "@/util/order";
import { likeSheet, dislikeSheet } from "@/api/likesheet";
import ModalComponent from "@/common/modal/ModalComponent";
import Tier from "@/common/icons/Tier.vue";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
    faMusic,
    faEye,
    faHeart,
    faStar,
    faChartLine,
    faPlay,
    faShoppingCart,
    faDownload,
    faUser,
    faPen,
} from "@fortawesome/free-solid-svg-icons";
import { userPageService } from "@/api/user-page.js";

library.add(
    faMusic,
    faEye,
    faHeart,
    faStar,
    faChartLine,
    faPlay,
    faShoppingCart,
    faDownload,
    faUser,
    faPen
);

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { userInfo, isLogin } = storeToRefs(userStore);

const props = defineProps({
    sheet: {
        type: Object,
        required: true,
        default: () => ({
            id: 0,
            imageUrl: "",
            title: "",
            songComposer: "",
            level: 0,
            uploaderNickname: "",
            likeCount: 0,
            likeStatus: false,
            difficulty: 0,
            viewCount: 0,
            price: 0,
        }),
    },
    starRateAvg: {
        type: Number,
        default: null,
    },
});

const showModal = ref(false);
const localLikeStatus = ref(props.sheet.likeStatus);
const localLikeCount = ref(props.sheet.likeCount);
const singlePlaySheets = ref([]);

watch(
    () => props.sheet,
    (newSheet) => {
        localLikeStatus.value = newSheet.likeStatus;
        localLikeCount.value = newSheet.likeCount;
    },
    { deep: true }
);

const isCartButtonDisabled = computed(() => props.sheet.price === 0);

const sheetInfo = computed(() => {
    const newSheet = { ...props.sheet };
    if (newSheet.songImg) {
        newSheet.imageUrl = `data:image/jpeg;base64,${newSheet.songImg}`;
    } else {
        newSheet.imageUrl = "";
    }
    return newSheet;
});

const goToUserProfile = () => {
    router.push({ name: "userProfile", params: { nickName: props.sheet.uploaderNickname } });
};

const goToPlayRoom = () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }
    router.push({ name: "singlePlay", params: { sheetId: props.sheet.id } });
};

const toggleLike = async () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }

    try {
        if (localLikeStatus.value) {
            await dislikeSheet(props.sheet.id);
            localLikeStatus.value = false;
            localLikeCount.value--;
        } else {
            await likeSheet(props.sheet.id);
            localLikeStatus.value = true;
            localLikeCount.value++;
        }
    } catch (error) {
        console.error("좋아요 토글 중 오류 발생:", error);
    }
};

const addSheetToOrder = async () => {
    if (isCartButtonDisabled.value) return;
    try {
        await addToOrder(sheetInfo.value);
        showModal.value = true;
    } catch (error) {
        console.error("장바구니에 담기는 데 실패했습니다.", error);
    }
};

const goToCart = () => {
    router.push({ name: "order" });
    showModal.value = false;
};

const continueShopping = () => {
    showModal.value = false;
};

const fetchSinglePlaySheets = async (userId) => {
    singlePlaySheets.value = await userPageService.fetchSinglePlaySheets(userId);
};

const goToDifficultyRatingPage = async () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }

    await fetchSinglePlaySheets(userInfo.value.id);
    const hasPlayedSheet = singlePlaySheets.value.some((sheet) => sheet.id === props.sheet.id);

    if (hasPlayedSheet) {
        router.push({ name: "sheetDifficultyRating", params: { sheetId: sheetInfo.value.id } });
    } else {
        alert(
            "이 악보를 플레이한 기록이 없습니다. 난이도 평가를 위해서는 먼저 악보를 플레이해야 합니다."
        );
    }
};

const downloadSheet = () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }
};

onMounted(() => {
    if (isLogin.value) {
        fetchSinglePlaySheets(userInfo.value.id);
    }
});
</script>

<style scoped>
@media (max-width: 1023px) {
    .lg\:w-1\/3 {
        width: 100%;
        max-width: 300px;
        margin: 0 auto;
    }
}
</style>

<style scoped>
@media (max-width: 1023px) {
    .lg\:w-1\/3 {
        width: 100%;
        max-width: 300px;
        margin: 0 auto;
    }
}
</style>
