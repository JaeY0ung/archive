<script setup>
import { tierInfo } from "@/util/tier-info"
import { sortInfo } from "@/util/sort";
import { searchSheetsByFilter } from "@/api/sheet"
import { onMounted, onUpdated, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { getAllGenres } from "@/api/genre"
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";

const { isLogin } = storeToRefs(useUserStore());

const route = useRoute();

const genres = ref([]); 
const sheets = ref([]);

const priceInfo = ref([
	{ value: 0, title: '무료' },
	{ value: 1, title: '유료' },
])

const searchFilter = ref({
	keyword: "", // 검색어 없음
	levels: [0, 1, 2, 3, 4, 5], // 모든 레벨
	genres: [1, 2, 3, 4, 5, 6], // 모든 장르
	prices: [0, 1], // 무료, 유료 (전부)
	successStatuses: [], // 필터 없음
	sort: "LATEST", // 최신순
})

const view = ref("list")

const search = () => {
	searchSheetsByFilter(
		{
			keyword: searchFilter.value.keyword,
			levels: searchFilter.value.levels.join(","),
			genres: searchFilter.value.genres.join(","),
			prices: searchFilter.value.prices.join(","),
			successStatuses: searchFilter.value.successStatuses.join(","),
			sort: searchFilter.value.sort,
		},
		({ data }) => { 
			sheets.value = data;
			sheets.value.map((s) => s.songImg ? (s.imageUrl = `data:image/jpeg;base64,${s.songImg}`) : "기본 이미지");
		}
	)
}
getAllGenres(({ data }) => genres.value = data)


// 다른 페이지에서 넘어왔을 때
onMounted(() => {
	searchFilter.value.keyword = route.query.keyword || "";
	search();
});

// 검색 필터 감지
watch(searchFilter, () => {
	search();
}, { deep: true });

</script>

<template>
	<div class="flex flex-col w-full h-full mb-[10px] bg-red-500">
		<div class="flex w-full justify-between items-center p-[20px] bg-yellow-200">
			<div class="flex-1 text-left">
				<p>
					<span class="highlight">{{ searchFilter.keyword }}</span> 악보 ( {{ sheets.length }}개의 결과 )
				</p>
			</div>
			<div class="flex items-center gap-[10px]">
				<select v-model="searchFilter.sort">
					<option v-for="sort in sortInfo" :value="sort.value">{{ sort.title }}</option>
				</select>

				<select v-model="view">
					<option value="list">리스트</option>
					<option value="card">카드</option>
				</select>
			</div>
		</div>

		<div class="flex flex-grow w-full mb-[10px]">
			<div class="flex-2 w-[200px] bg-purple-300">
				<span class="text-filter highlight mb-5">검색 필터</span>
				
				<div class="filter-group flex flex-col gap-5">

					<div class="filter-item">
						<span class="filter-category highlight">티어</span>
						<hr class="filter-divider" />
						<template v-for="t in tierInfo">
							<div class="filter-value">
								<label for="bronze">{{ t.title }}</label>
								<input type="checkbox" :value="t.level" v-model="searchFilter.levels" />
							</div>
						</template>
					</div>

					<div class="filter-item">
						<span class="filter-category highlight">무료/유료</span>
						<hr class="filter-divider" />
						<template v-for="price in priceInfo">
							<div class="filter-value">
								<label for="free">{{ price.title }}</label>
								<input type="checkbox" id="free" :value="price.value" v-model="searchFilter.prices" />
							</div>
						</template>
					</div>
					
					<div class="filter-item">
						<span class="filter-category highlight">장르</span>
						<hr class="filter-divider" />
						<template v-for="genre in genres">
							<div class="filter-value">
								<label for="ost">{{ genre.title }}</label>
								<input type="checkbox" :value="genre.id" v-model="searchFilter.genres" />
							</div>
						</template>
					</div>

					<template v-if="isLogin">
						<div class="filter-item">
							<span class="filter-category highlight">성공 여부</span>
							<hr class="filter-divider" />

							<div class="filter-value">
								<label for="success">성공</label>
								<input type="checkbox" value="SUCCESS" v-model="searchFilter.successStatuses"/>
							</div>

							<div class="filter-value">
								<label for="fail">실패</label>
								<input type="checkbox" value="FAIL" v-model="searchFilter.successStatuses"/>
							</div>
						</div>
					</template>
				</div>
			</div>
			<div class="flex-8 flex-grow bg-green-300">
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<template v-if="sheets.length">
						<div class="flex flex-col w-full absolute scroll-y" :class="[view === 'card' ? 'scroll-x' : 'scroll-y']">
							<SmallSheetCard v-for="sheet in sheets" :key="sheet.id" :sheet="sheet" />
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
	font-size: 1.5em;
	/* Larger font size for the "검색 필터" text */
	font-weight: bold;
}

.highlight {
	color: #8a8ecd;
}

.filter-group {
	margin-top: 10px;
}

.filter-item {
	margin-bottom: 16px;
	/* Increased margin for better spacing */
}

.filter-category {
	font-size: 1.2em;
	/* Larger font size for categories */
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

.scroll-x>.flex-col {
	flex-direction: row;
}
</style>
