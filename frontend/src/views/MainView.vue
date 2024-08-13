<script setup>
import { ref } from 'vue';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import SmallSheetCardForPhoto from '@/common/sheet/SmallSheetCardForPhoto.vue';
import { useRouter } from "vue-router"
import { searchSheetsByFilter, getRecommendSheetByUserRecentPlay } from '@/api/sheet';
import RecentPlaySheetCard from '@/common/sheet/RecentPlaySheetCard.vue';

const router = useRouter();
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

getPopularsheets();
getnewsheets();
getRecommendSheets();

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};
</script>

<template>
	<div class="flex w-full h-full flex-col">
		<!-- 위 -->
		<div class="flex justify-between gap-2 h-full">
			<div class="flex flex-col gap-5 w-[70%] p-[10px] rounded-xl">
				<div class="flex flex-grow w-full h-[30%] relative overflow-hidden items-center">
					<div class="flex w-full h-full absolute scroll-x bg-white bg-opacity-50">
						
					</div>
				</div>
				

			</div>
			<div class="p-[10px] bg-white bg-opacity-0 rounded-xl w-[25%] h-full flex justify-center items-center relative">
				<RecentPlaySheetCard/>
			</div>
		</div>
		<!-- 아래 -->
		<div class="flex flex-row w-full gap-[20px] pr-4 pl-4 bg-white/50 rounded-xl mb-[50px]">
			<div class="flex flex-grow flex-col w-[40vw] h-[40vw] gap-5">
				<div class="bold">인기</div>
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex flex-col w-full absolute scroll-y">
						<template v-if="popularSheets">
							<SmallSheetCard v-for="sheet in popularSheets" :key="sheet.id" :sheet="sheet"
								@click="goToSheetDetail(sheet.id)" />
						</template>
					</div>
				</div>
			</div>
			<div class="flex flex-grow flex-col w-[40vw] h-[40vw] gap-5">
				<div class="bold">최신</div>
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex flex-col w-full absolute scroll-y">
						<template v-if="newSheets">
							<SmallSheetCard v-for="sheet in newSheets" :key="sheet.id" :sheet="sheet"
								@click="goToSheetDetail(sheet.id)" />
						</template>
					</div>
				</div>
			</div>
			
			<div class="flex flex-col gap-2">
				<div class="flex flex-col mt-4 h-[50vh] w-[20vw] overflow-hidden">
					<!-- 토글 버튼 -->
					<div class="flex justify-center mb-2">
						<button 
							@click="isSingleRank = true"
							:class="{'bg-purple-500 text-white': isSingleRank, 'bg-gray-200 text-gray-600': !isSingleRank}"
							class="px-4 py-2 rounded-l-lg">
							싱글 랭킹
						</button>
						<button 
							@click="isSingleRank = false"
							:class="{'bg-purple-500 text-white': !isSingleRank, 'bg-gray-200 text-gray-600': isSingleRank}"
							class="px-4 py-2 rounded-r-lg">
							멀티 랭킹
						</button>
					</div>

					<!-- 싱글 랭킹 -->
					<div v-if="isSingleRank" class="bg-white bg-opacity-50 w-full flex flex-col flex-grow overflow-hidden">
						<div class="flex-shrink-0">싱글 랭킹 명예의 전당</div>
						<div class="flex-grow overflow-y-auto">
							<ul class="space-y-2">
								<li v-for="(item, index) in rankings" :key="index" class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
									{{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
								</li>
							</ul>
						</div>
					</div>

					<!-- 멀티 랭킹 -->
					<div v-else class="bg-white bg-opacity-50 w-full flex flex-col flex-grow overflow-hidden">
						<div class="flex-shrink-0">멀티 랭킹 명예의 전당</div>
						<div class="flex-grow overflow-y-auto">
							<ul class="space-y-2">
								<li v-for="(item, index) in rankings" :key="index" class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
									{{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
								</li>
							</ul>
						</div>
					</div>
				</div>
				<!-- 추천 악보 사진 슬라이드-->
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex w-full absolute scroll-x transition-transform duration-500"
							:style="{ transform: `translateX(-${currentIndex * 100}%)` }">
						<template v-if="recommendSheets">
							<SmallSheetCardForPhoto v-for="sheet in recommendSheets" :key="sheet.id" :sheet="sheet"
								@click="goToSheetDetail(sheet.id)" />
						</template>
					</div>
				</div> 
			</div>
		</div>
	</div>
</template>


<style scoped>
.scroll-x {
  overflow-x: auto;
}

.scroll-y {
  overflow-y: auto;
}
</style>