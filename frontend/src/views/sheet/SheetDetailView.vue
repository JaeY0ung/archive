<script setup>
import { onMounted } from "vue";
import Sheet from "@/common/sheet/Sheet.vue";
import BigSheetCard from "@/common/sheet/BigSheetCardForDetail.vue";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { storeToRefs } from "pinia";
import { computed, ref, watch, defineAsyncComponent } from "vue";
import { useUserStore } from "@/stores/user";
import { useRoute, useRouter } from "vue-router";
import { showLoginRequestAlert } from "@/util/alert";
import {
    searchSheetDetail,
    searchSheetsByFilter,
    searchStarRateListBySheetId,
    registerStarRateBySheetId,
} from "@/api/sheet";
import { format, parseISO } from "date-fns";
import { ko } from "date-fns/locale";
import { userPageService } from "@/api/user-page.js";

const userStore = useUserStore();
const { userInfo, isLogin } = storeToRefs(userStore);
const route = useRoute();
const router = useRouter();
const sheet = ref({});
const sameLevelSheets = ref([]);
const starRateList = ref([]);
const starRateStatistic = ref([0, 0, 0, 0, 0]);
const starRateAvg = ref(null);
const singlePlaySheets = ref([]);

const totalStarRateCount = computed(() => {
    let sum = 0;
    starRateStatistic.value.map((s) => (sum += s));
    return sum;
});

// 싱글 플레이 악보 정보 가져오기
const fetchSinglePlaySheets = async (userId) => {
    singlePlaySheets.value = await userPageService.fetchSinglePlaySheets(userId);
};

// 난이도 평가 페이지로 이동
const goToDifficultyEvaluation = () => {
    if (singlePlaySheets.value.length > 0) {
        router.push({ name: "difficultyEvaluation" });
    } else {
        alert("난이도 평가에 참여하려면 최소 한 개 이상의 싱글 플레이 악보가 필요합니다.");
    }
};

const starRateMaxCount = computed(() => {
    let max = 0;
    starRateStatistic.value.map((s) => {
        if (s > max) max = s;
    });
    return max;
});

// 별점 평균과 별점 통계 계산
const calculateStarRateStats = () => {
    starRateStatistic.value = [0, 0, 0, 0, 0];
    let sum = 0;
    starRateList.value.forEach((starRateInfo) => {
        sum += starRateInfo.starRate;
        starRateStatistic.value[starRateInfo.starRate - 1]++;
    });
    starRateAvg.value = sum ? round(sum / starRateList.value.length, 2) : null;
};

const starRateRegisterForm = ref({
    content: "",
    starRate: 5,
});

// 같은 수준의 악보 랜덤으로 가져오기
const searchRandomSameLevelSheets = async () => {
    await searchSheetsByFilter({ levels: sheet.value.level, sort: "RANDOM" }, ({ data }) => {
        if (!data) return;
        const sheets = [];
        data.map((s) => {
            if (s.id != route.params.sheetId) sheets.push(s);
        });
        sameLevelSheets.value = sheets;
    });
};

// 별점 가져오기
const searchStarRateList = async () => {
    await searchStarRateListBySheetId(route.params.sheetId, ({ data }) => {
        if (!data) return;
        starRateList.value = data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        calculateStarRateStats();
    });
};

const formatDate = (dateString) => {
    const date = parseISO(dateString);
    return format(date, "yyyy년 MM월 dd일 HH:mm", { locale: ko });
};

// 별점 등록하기
const registerStarRate = async () => {
    if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }
    // if (singlePlaySheets.value.length === 0) {
    //     alert('난이도 평가에 참여하려면 최소 한 개 이상의 싱글 플레이 악보가 필요합니다.');
    //     return;
    // }
    if (!starRateRegisterForm.value.content) {
        alert("평가 글을 작성해주세요");
        return;
    } else if (!starRateRegisterForm.value.starRate) {
        alert("별점을 입력해주세요");
        return;
    }
    await registerStarRateBySheetId(
        route.params.sheetId,
        starRateRegisterForm.value,
        async (res) => {
            starRateRegisterForm.value = { content: "", starRate: 5 };
            await searchStarRateList();
        }
    );
};

const round = (number, place) => {
    return Math.round(number * 10 ** place) / 10 ** place;
};

const fetchSheetDetail = async () => {
    await searchSheetDetail(route.params.sheetId, ({ data }) => {
        if (!data) return;
        console.log("가져온 악보 정보", data);
        sheet.value = data;
    });
};

const goToSheetDetail = (sheetId) => {
    router.push({
        name: "sheetDetail",
        params: { sheetId: sheetId },
        replace: true,
    });
};

onMounted(async () => {
    await fetchSheetDetail();
    await searchStarRateList();
});

watch(
    () => route.params.sheetId,
    async () => {
        await fetchSheetDetail();
    }
);

watch(sheet, async () => {
    await searchRandomSameLevelSheets();
});

// Sheet 컴포넌트 비동기 로드
const AsyncSheet = defineAsyncComponent(() => import("@/common/sheet/Sheet.vue"));

onMounted(async () => {
    await fetchSheetDetail();
    await searchStarRateList();
    if (isLogin.value) {
        await fetchSinglePlaySheets(userInfo.value.id);
    }
});
</script>

<template>
    <div class="flex w-full h-full justify-between p-2 ">
        <!-- 1. 악보 디테일 정보 -->
        <div class="flex flex-col gap-5 w-[50%]">
            <!-- 1-1. 악보 카드 -->
            <BigSheetCard
                :sheet="sheet"
                :starRateAvg="starRateAvg"
                class="rounded-xl shadow-lg hover:shadow-xl transition-shadow duration-300  bg-white rounded-xl"
            />

            <!-- 1-2. 비슷한 수준의 악보 추천 및 리뷰 섹션 (combined) -->
            <div
                class="flex flex-col h-[calc(100%-220px)] w-full p-4 bg-white rounded-xl shadow-lg overflow-auto hide-scrollbar"
            >
                <!-- 비슷한 수준의 악보 추천 -->
                <div class="mb-6 hide-scrollbar">
                    <h2 class="text-xl font-bold text-gray-800 mb-4">비슷한 수준의 악보</h2>
                    <div class="scroll-x flex rounded-xl pb-2">
                        <template v-if="sameLevelSheets && sameLevelSheets.length != 0">
                            <SmallSheetCard
                                v-for="sheet in sameLevelSheets"
                                :key="sheet.id"
                                :sheet="sheet"
                                @click="goToSheetDetail(sheet.id)"
                                class="cursor-pointer h-fit mr-4 last:mr-0 transition-transform duration-300 hover:scale-105"
                            />
                        </template>
                        <template v-else>
                            <div class="w-full flex justify-center items-center text-gray-500">
                                해당하는 악보가 없습니다.
                            </div>
                        </template>
                    </div>
                </div>

                <!-- 리뷰 섹션 -->
                <div class="mt-6">
                    <h2 class="text-xl font-bold text-gray-800 mb-4">
                        리뷰 [{{ starRateList.length }}]
                    </h2>

                    <!-- 1.3.1 별점 요약 -->
                    <div
                        class="flex justify-between w-full bg-gray-50 rounded-xl shadow-md p-6 mb-6"
                    >
                        <!-- 1) 별점 평균 -->
                        <div class="w-[40%] flex flex-col items-center justify-center text-center">
                            <div v-if="starRateAvg" class="mb-2">
                                <div class="text-6xl font-bold text-purple-600">
                                    {{ starRateAvg }}
                                </div>
                                <div class="text-gray-500 text-lg">/ 5.0</div>
                            </div>
                            <div v-else class="text-xl font-semibold text-gray-500">
                                첫번째 별점을<br />등록해주세요
                            </div>
                            <div class="flex justify-center items-center mt-2">
                                <template v-for="n in 5">
                                    <span
                                        class="inline-block w-8 h-8 mask mask-star-2"
                                        :class="
                                            n <= Math.round(starRateAvg)
                                                ? 'bg-yellow-400'
                                                : 'bg-gray-300'
                                        "
                                    >
                                    </span>
                                </template>
                            </div>
                            <div class="text-sm text-gray-600 mt-2">
                                {{ totalStarRateCount }}개의 평가
                            </div>
                        </div>
                        <!-- 2) 별점 그래프 -->
                        <div class="w-[55%] flex flex-row justify-around items-end">
                            <template
                                v-for="(starRateCount, index) in starRateStatistic"
                                :key="index"
                            >
                                <div class="flex flex-col items-center">
                                    <div
                                        class="bg-gray-200 rounded-t-xl"
                                        :style="{
                                            height: '120px',
                                            width: '30px',
                                            position: 'relative',
                                            overflow: 'hidden',
                                        }"
                                    >
                                        <div
                                            class="bg-purple-400 absolute bottom-0 w-full transition-all duration-500 ease-out"
                                            :style="{
                                                height: `${
                                                    (starRateCount / starRateMaxCount) * 100
                                                }%`,
                                            }"
                                        ></div>
                                    </div>
                                    <div class="mt-2 text-sm font-medium text-gray-600">
                                        {{ index + 1 }}점
                                    </div>
                                </div>
                            </template>
                        </div>
                    </div>

                    <!-- 1.3.2 평가 등록 -->
                    <div class="w-full mb-6">
                        <form
                            @submit.prevent="registerStarRate"
                            class="flex items-center gap-2 bg-gray-50 p-3 rounded-xl shadow-md"
                        >
                            <div class="rating rating-md">
                                <template v-for="i in 5">
                                    <input
                                        type="radio"
                                        name="rating-10"
                                        class="mask mask-star-2 bg-yellow-400"
                                        :value="i"
                                        v-model="starRateRegisterForm.starRate"
                                    />
                                </template>
                            </div>
                            <input
                                v-model="starRateRegisterForm.content"
                                class="input input-bordered flex-grow"
                                placeholder="한 줄 리뷰를 입력해주세요."
                            />
                            <button type="submit" class="btn btn-primary btn-sm">등록</button>
                        </form>
                    </div>

                    <!-- 1.3.3 평가 리스트  -->
                    <div class="space-y-4">
                        <div
                            v-for="starRateInfo in starRateList"
                            :key="starRateInfo.id"
                            class="flex items-start p-3 bg-gray-50 rounded-lg shadow-md transition-all duration-300 hover:shadow-lg"
                        >
                            <img
                                :src="
                                    starRateInfo.userImg
                                        ? `data:image/jpeg;base64,${starRateInfo.userImg}`
                                        : require('@/assets/img/common/default_profile.png')
                                "
                                alt="프로필 사진"
                                class="w-10 h-10 rounded-full object-cover mr-3"
                            />
                            <div class="flex-grow">
                                <div class="flex justify-between items-center mb-1">
                                    <span class="font-semibold text-gray-800 text-sm">{{
                                        starRateInfo.nickname
                                    }}</span>
                                    <div class="flex">
                                        <template v-for="n in 5">
                                            <span
                                                class="inline-block w-4 h-4 mask mask-star-2"
                                                :class="
                                                    n <= starRateInfo.starRate
                                                        ? 'bg-yellow-400'
                                                        : 'bg-gray-300'
                                                "
                                            >
                                            </span>
                                        </template>
                                    </div>
                                </div>
                                <p class="text-gray-600 text-sm">{{ starRateInfo.content }}</p>
                                <p class="text-gray-400 text-xs mt-1">
                                    {{ formatDate(starRateInfo.createdAt) }}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 2. musicXML -->
        <div class="flex flex-col gap-5 w-[49%] bg-white rounded-xl shadow-xl overflow-hidden">
            <AsyncSheet :sheetId="Number(route.params.sheetId)" />
        </div>
    </div>
</template>

<style scoped>
.hide-scrollbar {
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.hide-scrollbar::-webkit-scrollbar {
    display: none;
}

.scroll-x {
    overflow-x: auto;
    white-space: nowrap;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: thin;
    scrollbar-color: rgba(155, 155, 155, 0.5) transparent;
}

.scroll-x::-webkit-scrollbar {
    height: 6px;
}

.scroll-x::-webkit-scrollbar-track {
    background: transparent;
}

.scroll-x::-webkit-scrollbar-thumb {
    background-color: rgba(155, 155, 155, 0.5);
    border-radius: 20px;
    border: transparent;
}
</style>
