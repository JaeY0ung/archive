<template>
    <div class="rounded-lg shadow-lg p-6 max-w-[1000px] w-full h-[300px] mx-auto bg-white bg-opacity-40">
        <div class="flex flex-col md:flex-row gap-6">
            <!-- 악보 이미지 -->
            <div class="w-full md:w-1/3 aspect-square">
                <div
                    v-if="sheetInfo.imageUrl"
                    class="w-full h-full rounded-lg shadow-md overflow-hidden"
                >
                    <img
                        :src="sheetInfo.imageUrl"
                        alt="악보 이미지"
                        class="w-full h-full object-cover"
                    />
                </div>
                <div
                    v-else
                    class="w-full h-full bg-gray-200 rounded-lg shadow-md flex items-center justify-center"
                >
                    <font-awesome-icon :icon="['fas', 'music']" class="text-gray-400 text-5xl" />
                </div>
            </div>

            <!-- 악보 정보 -->
            <div class="w-full md:w-2/3 flex flex-col justify-between">
                <div class="space-y-4">
                    <div class="flex items-start justify-between">
                        <h2 class="text-2xl font-bold text-gray-800 leading-tight">
                            {{ sheet.title }}
                        </h2>
                        <Tier :level="sheet.level" class="ml-2 flex-shrink-0" />
                    </div>

                    <div class="text-gray-600 space-y-2">
                        <p class="flex items-center">
                            <span class="font-semibold w-20">작곡가:</span>
                            <span>{{ sheet.songComposer }}</span>
                        </p>
                        <p class="flex items-center">
                            <span class="font-semibold w-20">게시자:</span>
                            <span
                                class="cursor-pointer text-blue-600 hover:underline"
                                @click="goToUserProfile"
                            >
                                {{ sheet.uploaderNickname }}
                            </span>
                        </p>
                    </div>

                    <div class="flex items-center space-x-6 text-sm text-gray-500">
                        <div class="flex items-center">
                            <font-awesome-icon :icon="['fas', 'eye']" class="mr-2 text-gray-400" />
                            <span>{{ sheet.viewCount }}</span>
                        </div>
                        <div class="flex items-center cursor-pointer" @click="toggleLike">
                            <font-awesome-icon
                                :icon="['fas', 'heart']"
                                :class="sheetInfo.likeStatus ? 'text-red-500' : 'text-gray-400'"
                                class="mr-2"
                            />
                            <span>{{ sheetInfo.likeCount }}</span>
                        </div>
                        <div class="flex items-center">
                            <font-awesome-icon
                                :icon="['fas', 'star']"
                                class="mr-2 text-yellow-400"
                            />
                            <span>{{ sheet.difficulty ? sheet.difficulty : "Unrated" }}</span>
                        </div>
                    </div>
                </div>

                <div
                    class="flex flex-wrap justify-between items-center mt-6 space-y-2 md:space-y-0"
                >
                    <button
                        class="btn bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-full transition duration-300 ease-in-out transform hover:scale-105 w-full md:w-auto"
                        @click="goToDifficultyRatingPage"
                    >
                        <font-awesome-icon :icon="['fas', 'chart-line']" class="mr-2" />
                        난이도 기여
                    </button>
                    <button
                        class="btn bg-gray-800 hover:bg-gray-900 text-white px-6 py-2 rounded-full transition duration-300 ease-in-out transform hover:scale-105 w-full md:w-auto"
                        @click="goToPlayRoom"
                    >
                        <font-awesome-icon :icon="['fas', 'play']" class="mr-2" />
                        TEST
                    </button>
                    <button
                        class="btn flex items-center justify-center space-x-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-full transition duration-300 ease-in-out transform hover:scale-105 w-full md:w-auto"
                        :class="{ 'opacity-50 cursor-not-allowed': isCartButtonDisabled }"
                        @click="addSheetToOrder"
                        :disabled="isCartButtonDisabled"
                    >
                        <font-awesome-icon :icon="['fas', 'shopping-cart']" />
                        <span>{{ sheet.price === 0 ? "무료" : `${sheet.price}원` }}</span>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- 모달 컴포넌트 -->
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
import { computed, ref, watch } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
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
} from "@fortawesome/free-solid-svg-icons";

// FontAwesome 아이콘 등록
library.add(faMusic, faEye, faHeart, faStar, faChartLine, faPlay, faShoppingCart);

const router = useRouter();
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
});

const sheetInfo = ref(props.sheet);
const showModal = ref(false);

const isCartButtonDisabled = computed(() => sheetInfo.value.price === 0);

watch(
    () => props.sheet,
    (newSheet) => {
        sheetInfo.value = { ...newSheet };
        if (newSheet.songImg) {
            sheetInfo.value.imageUrl = `data:image/jpeg;base64,${newSheet.songImg}`;
        } else {
            sheetInfo.value.imageUrl = ""; // 이미지가 없을 경우 빈 문자열로 설정
        }
    },
    { deep: true }
);

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

    if (sheetInfo.value.likeStatus) {
        await dislikeSheet(props.sheet.id);
        sheetInfo.value.likeStatus = false;
        sheetInfo.value.likeCount--;
    } else {
        await likeSheet(props.sheet.id);
        sheetInfo.value.likeStatus = true;
        sheetInfo.value.likeCount++;
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

const goToDifficultyRatingPage = () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }
    router.push({ name: "sheetDifficultyRating", params: { sheetId: sheetInfo.value.id } });
};
</script>

<style scoped></style>
