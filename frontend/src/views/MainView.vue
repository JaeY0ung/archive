<script setup>
import { Carousel, Slide } from 'vue3-carousel';
import 'vue3-carousel/dist/carousel.css';
import { ref } from 'vue';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import SmallSheetCardForPhoto from '@/common/sheet/SmallSheetCardForPhoto.vue';
import { useRouter } from "vue-router"
import { searchSheetsByFilter, getRecommendSheetByUserRecentPlay, searchRecentPlayedsheet } from '@/api/sheet';
import RecentPlaySheetCard from '@/common/sheet/RecentPlaySheetCard.vue';

const router = useRouter();
const recentPlayedSheet = ref();
const popularSheets = ref([]); // 인기 악보 리스트
const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const recommendSheets = ref([]); // 추천 악보 리스트
const isSingleRank = ref(false);

const rankings = ref([
	{ rank: 1, name: 'User1', wins: 60, losses: 20 },
	{ rank: 2, name: 'User2', wins: 55, losses: 25 },
	{ rank: 3, name: 'User3', wins: 50, losses: 30 },
	{ rank: 4, name: 'User4', wins: 45, losses: 35 },
	{ rank: 5, name: 'User5', wins: 40, losses: 40 },

]);

const getPopularsheets = async () => {
	await searchSheetsByFilter(
		{ sort: "POPULAR" },
		({ data }) => {
			if (!data) return;
			popularSheets.value = data;
		}
	)
}

const getnewsheets = async () => {
	await searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			newSheets.value = data;
		}
	)
}

const getRecommendSheets = async () => {
	await getRecommendSheetByUserRecentPlay(
		(res) => {
			if (res && res.data) {
				recommendSheets.value = res.data; // 객체 내 data에 접근해서 할당
				return
			}
			console.error('No valid data structure returned from searchSheetRecommand');
		}
	)
}

searchRecentPlayedsheet(({ data }) => {
	console.log(data)
	recentPlayedSheet.value = data
});

getPopularsheets();
getnewsheets();
getRecommendSheets();

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};
</script>

<template>
	<div class="w-full h-full flex flex-col justify-between">
		<!-- 위 -->
		<div class="h-[35%] flex justify-between">
			<div class="h-full w-[50%] flex flex-col gap-5 m-1 box-border">
				<div class="w-full h-[30%] flex flex-grow relative overflow-hidden items-center">
					<div
						class="flex justify-center items-center w-full h-full absolute scroll-x bg-white bg-opacity-50 rounded-2xl banner-img">
						<div class="btn btn-primary">대결하러 가기</div>
					</div>
				</div>
			</div>

			<div
				class="h-full min-w-[220px] flex flex-col justify-center bg-white bg-opacity-50 rounded-2xl p-3 m-1 ">
				<!-- 토글 버튼 -->
				<div class="w-full flex justify-between mb-2">
					<button @click="isSingleRank = true"
						:class="{ 'bg-purple-500 text-white': isSingleRank, 'bg-gray-200 text-gray-600': !isSingleRank }"
						class="w-1/2 p-2 rounded-l-lg">
						싱글 랭킹
					</button>
					<button @click="isSingleRank = false" :class="{ 'bg-purple-500 text-white': !isSingleRank, 'bg-gray-200 text-gray-600': isSingleRank }"
						class="w-1/2 p-2 rounded-r-lg">
						멀티 랭킹
					</button>
				</div>

				<div class="w-full flex flex-col flex-grow overflow-hidden">
					<template v-if="isSingleRank">
						<div class="flex-grow overflow-y-auto scroll-y">
							<ul class="space-y-2">
								<li v-for="(item, index) in rankings" :key="index"
									class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
									{{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
								</li>
							</ul>
						</div>
					</template>
					<template v-else>
						<div class="h-full flex-grow overflow-y-auto scroll-y">
							<ul class="space-y-2">
								<li v-for="(item, index) in rankings" :key="index"
									class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
									{{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
								</li>
							</ul>
						</div>
					</template>
				</div>
			</div>

			<!-- 최근 플레이 악보 -->
			<div>
				<div
					class="h-full min-w-[200px] w-[350px] p-2 flex justify-center items-center relative bg-white bg-opacity-0 rounded-xl">
					<template v-if="recentPlayedSheet">
						<RecentPlaySheetCard :sheet="recentPlayedSheet" />
					</template>
					<template>
						연주하러 가실래요?
					</template>
				</div>
			</div>
		</div>


		<!-- 아래 -->
		<div class="h-[60%] w-full flex justify-between gap-3">
			<div class="flex p-4 bg-white/50 rounded-2xl">
				<div class="w-[280px] flex flex-col">
					<div class="text-xl bold mb-2">인기</div>
					<div class="flex w-full h-full relative scroll-y">
						<div class="flex flex-col w-full absolute scroll-y">
							<template v-if="popularSheets">
								<template v-for="sheet in popularSheets" :key="sheet.id">
									<SmallSheetCard :sheet="sheet" @click="goToSheetDetail(sheet.id)" />
								</template>
							</template>
						</div>
					</div>
				</div>
			</div>

			<div class="flex p-4 bg-white/50 rounded-2xl">
				<div class="w-[280px] flex flex-col">
					<div class="text-xl bold mb-2">최신</div>
					<div class="flex flex-grow w-full h-full relative overflow-hidden scroll-y">
						<div class="flex flex-col w-full absolute scroll-y">
							<template v-if="newSheets">
								<template v-for="sheet in newSheets" :key="sheet.id">
									<SmallSheetCard :sheet="sheet" @click="goToSheetDetail(sheet.id)" />
								</template>
							</template>
						</div>
					</div>
				</div>
			</div>

			<div class="w-[350px] flex p-4 bg-white/50 rounded-2xl">
				<div class="w-full h-full flex flex-col">
					<div class="text-xl bold mb-2">추천</div>
					<!-- 추천 악보 사진 슬라이드-->
					<div class="flex justify-center">
						<template v-if="recommendSheets">
							<Carousel :wrap-around="true" :mouse-drag="true" :snap-align="'center'" :transition="300" :autoplay="3000" :pause-autoplay-on-hover="true">
								<template v-for="(sheet, index) in newSheets" :key="sheet.id">
									<Slide :index :card="sheet">
										<SmallSheetCardForPhoto :sheet="sheet"  @click="goToSheetDetail(sheet.id)" />
									</Slide>
								</template>
							</Carousel>
						</template>
						<template v-else>
							아직 추천드릴 악보가 없네요ㅠㅠ
						</template>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>


<style scoped>
.banner-img {
	background-image: url("../assets/img/main-view-banner.svg");
}

.card {
	display: flex;
	justify-content: center;
	align-items: center;
	width: 100%;
	height: 200px;
	background-color: #f0f0f0;
	border-radius: 10px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>