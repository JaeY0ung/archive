<script setup>
import { tierInfo } from "@/util/tier-info";
import { sortInfo } from "@/util/sort";
import { searchSheetsByFilter } from "@/api/sheet";
import { onMounted, onUpdated, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { getAllGenres } from "@/api/genre";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import SmallSheetCardVer2 from "@/common/sheet/SmallSheetCardVer2.vue";

const { isLogin } = storeToRefs(useUserStore());

const route = useRoute();
const router = useRouter();

const genres = ref([]);
const sheets = ref([]);

const priceInfo = ref([
    { value: 0, title: "무료" },
    { value: 1, title: "유료" },
]);

const searchFilter = ref({
	keyword: "", // 검색어 없음
	levels: [0, 1, 2, 3, 4, 5], // 모든 레벨
	genres: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10], // 모든 장르
	prices: [0, 1], // 무료, 유료 (전부)
	successStatuses: [], // 필터 없음
    sort: "LATEST", // 최신순
})

const view = ref("list")

const search = async () => {
	await searchSheetsByFilter(
		{
			keyword: searchFilter.value.keyword,
			levels: searchFilter.value.levels.join(","),
			genres: searchFilter.value.genres.join(","),
			prices: searchFilter.value.prices.join(","),
			successStatuses: searchFilter.value.successStatuses.join(","),
            sort: searchFilter.value.sort,
            page: searchFilter.value.page,
		},
		({ data }) => { 
			sheets.value = data;
		}
	)
}
getAllGenres(({ data }) => genres.value = data)


// 다른 페이지에서 넘어왔을 때
onMounted(async () => {
    searchFilter.value.keyword = route.query.keyword || "";
    await search();
});

// 검색 필터 감지
watch(searchFilter, async() => {
	await search();
}, { deep: true });

watch(
    searchFilter,
    async () => {
        await search();
    },
    { deep: true }
);

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};
</script>

<template>
    <div class="flex flex-col w-full h-full mb-[10px] bg-red-600 bg-opacity-0">
        <div
            class="flex w-full justify-between items-center p-[20px] bg-white bg-opacity-80 shadow-md rounded-xl mb-2"
        >
            <div class="flex-1 text-left text-1g font-semibold text-gray-700">
                <p>
                    <span class="text-indigo-600">{{
                        searchFilter.keyword
                    }}</span>
                    악보 ( {{ sheets.length }}개의 결과 )
                </p>
            </div>
            <div class="flex items-center space-x-4">
                <!-- <select v-model="searchFilter.sort" class="p-2 border rounded-md bg-gray-50">
					<option v-for="sort in sortInfo" :value="sort.value">{{ sort.title }}</option>
				</select>

				<select v-model="view" class="p-2 border rounded-md bg-gray-50">
					<option value="list">리스트</option>
					<option value="card">카드</option>
				</select> -->
                <div class="relative">
                    <select
                        v-model="searchFilter.sort"
                        class="p-2 pl-4 pr-8 border rounded-md bg-gray-50 text-gray-700 font-medium appearance-none focus:outline-none focus:ring-2 focus:ring-gray-500"
                    >
                        <option v-for="sort in sortInfo" :value="sort.value">
                            {{ sort.title }}
                        </option>
                    </select>
                    <div
                        class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500"
                    >
                        <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20">
                            <path
                                d="M5.293 7.293l4.293 4.293 4.293-4.293 1.414 1.414L10 14.414l-5.707-5.707z"
                            />
                        </svg>
                    </div>
                </div>

                <div class="relative">
                    <select
                        v-model="view"
                        class="p-2 pl-4 pr-8 border rounded-md bg-gray-50 text-gray-700 font-medium appearance-none focus:outline-none focus:ring-2 focus:ring-gray-500"
                    >
                        <option value="list">리스트</option>
                        <option value="card">카드</option>
                    </select>
                    <div
                        class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500"
                    >
                        <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20">
                            <path
                                d="M5.293 7.293l4.293 4.293 4.293-4.293 1.414 1.414L10 14.414l-5.707-5.707z"
                            />
                        </svg>
                    </div>
                </div>
            </div>
        </div>

        <div class="flex flex-grow space-x-4 overflow-hidden">
            <!-- 검색 필터 -->
            <div
                class="flex-shrink-0 w-1/4 bg-white bg-opacity-50 p-4 rounded-lg shadow-md space-y-4 box-border max-h-[calc(100vh-160px)] overflow-y-auto text-sm hide-scrollbar"
            >
                <span class="text-filter mb-4 text-gray-800">검색 필터</span>

                <!-- 티어 필터 -->
                <div class="space-y-1">
                    <span class="text-sm font-semibold text-gray-700"
                        >티어</span
                    >
                    <hr class="border-gray-300" />
                    <div class="space-y-1">
                        <template v-for="tier in tierInfo">
                            <div class="flex items-center space-x-2 ml-3">
                                <label>{{ tier.title }}</label>
                                <input
                                    type="checkbox"
                                    :value="tier.level"
                                    v-model="searchFilter.levels"
                                />
                            </div>
                        </template>
                    </div>
                </div>

                <!-- 무료/유료 필터 -->
                <div class="space-y-2">
                    <h3 class="font-semibold text-gray-700 text-sm">
                        무료/유료
                    </h3>
                    <hr class="border-gray-300" />
                    <div class="space-y-1">
                        <template v-for="price in priceInfo">
                            <div class="flex items-center space-x-2 ml-2">
                                <label>{{ price.title }}</label>
                                <input
                                    type="checkbox"
                                    :value="price.value"
                                    v-model="searchFilter.prices"
                                />
                            </div>
                        </template>
                    </div>
                </div>

                <!-- 장르 필터 -->
                <div class="space-y-2">
                    <h3 class="text-sm font-semibold text-gray-700">장르</h3>
                    <hr class="border-gray-300" />
                    <div class="space-y-1">
                        <template v-for="genre in genres">
                            <div class="flex items-center space-x-2 ml-2">
                                <label>{{ genre.title }}</label>
                                <input
                                    type="checkbox"
                                    :value="genre.id"
                                    v-model="searchFilter.genres"
                                />
                            </div>
                        </template>
                    </div>
                </div>

                <!-- 성공 여부 필터 -->
                <template v-if="isLogin">
                    <div class="space-y-1">
                        <h3 class="text-sm font-semibold text-gray-700">
                            성공 여부
                        </h3>
                        <hr class="border-gray-300" />
                        <div class="space-y-1">
                            <div class="flex items-center space-x-2 ml-2">
                                <label>성공</label>
                                <input
                                    type="checkbox"
                                    value="SUCCESS"
                                    v-model="searchFilter.successStatuses"
                                />
                            </div>
                            <div class="flex items-center space-x-2 ml-2">
                                <label>실패</label>
                                <input
                                    type="checkbox"
                                    value="FAIL"
                                    v-model="searchFilter.successStatuses"
                                />
                            </div>
                        </div>
                    </div>
                </template>
            </div>

            <div
                class="flex-grow bg-white bg-opacity-50 p-4 rounded-lg shadow-md box-border overflow-y-auto max-h-[calc(100vh-160px)]"
            >
                <div
                    class="flex flex-grow w-full h-full relative overflow-hidden items-center"
                >
                    <template v-if="sheets.length">
                        <!-- <div class="flex flex-col w-full absolute scroll-y mt-3" :class="[view === 'card' ? 'scroll-x' : 'scroll-y']">
							<SmallSheetCard v-for="sheet in sheets" :key="sheet.id" :sheet="sheet" />
						</div> -->
                        <!-- 리스트 버전 -->
                        <div
                            v-if="view === 'list'"
                            class="flex flex-col w-full absolute overflow-hidden-scroll overflow-y-auto mt-3"
                        >
                            <SmallSheetCardVer2 
                                v-for="sheet in sheets"
                                :key="sheet.id"
                                :sheet="sheet"
                                :restrictTitle="false" 
                                @click="goToSheetDetail(sheet.id)"
                                
                            />
                        </div>

                        <!-- 카드 버전 -->
                        <div
                            v-if="view === 'card'"
                            class="flex flex-wrap justify-center overflow-y-auto gap-4"
                        >
                            <SmallSheetCard
                                v-for="sheet in sheets"
                                :key="sheet.id"
                                :sheet="sheet"
                                :restrictTitle="true"
                                @click="goToSheetDetail(sheet.id)"
                                class="max-w-[400px] w-full sm:w-auto"
                            />
                        </div>
                    </template>
                    <template v-else>
                        <p>검색 결과가 없습니다.</p>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.left-panel,
.right-panel {
    padding: 20px;
}

.left-panel {
    flex: 2;
    background-color: #f0f0f0;
}

.right-panel {
    flex: 8;
    background-color: #ffffff;
}

.text-filter {
    font-size: 1.2em;
    font-weight: bold;
}

.filter-group {
    margin-top: 10px;
}

.filter-item {
    margin-bottom: 16px;
}

.filter-category {
    font-size: 1.2em;
    font-weight: bold;
}

.filter-value {
    display: flex;
    align-items: center;
    margin-left: 10px;
}

.filter-divider {
    margin: 10px 0;
    border: 0;
    border-bottom: 1px solid #ccc;
}

.scroll-y {
    overflow-y: auto;
    height: calc(100vh - 200px);
}

.scroll-x {
    display: flex;
    overflow-x: auto;
    height: calc(100vh - 200px);
}

.scroll-x > .flex-col {
    flex-direction: row;
}



/* 스크롤바 숨기기 */
::-webkit-scrollbar {
    width: 0px;
    background: transparent;
}

.ms-overflow-style: none; /* IE와 Edge */
scrollbar-width: none; /* Firefox */

/* 추가적으로 스크롤 영역의 스크롤바를 숨기기 위한 클래스 */
.overflow-hidden-scroll {
    overflow-y: scroll; /* 기능적 스크롤을 유지하기 위해 scroll 설정 */
    -ms-overflow-style: none;  /* IE와 Edge에서 스크롤바 숨김 */
    scrollbar-width: none;  /* Firefox에서 스크롤바 숨김 */
}

.overflow-hidden-scroll::-webkit-scrollbar {
    width: 0px;  /* Chrome, Safari, Opera에서 스크롤바 숨김 */
    height: 0px;
    background: transparent; /* 스크롤바 배경을 투명하게 */
}


</style>
