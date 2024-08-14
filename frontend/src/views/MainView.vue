<script setup>
import { ref } from 'vue';
import { useRouter } from "vue-router"
import { searchSheetsByFilter, getRecommendSheetByUserRecentPlay, searchRecentPlayedsheet } from '@/api/sheet';
import MainViewComponent from '@/common/main/MainViewComponent.vue';
import RankingList from '@/common/main/RankingList.vue';
import RecentPlaySheetCard from '@/common/sheet/RecentPlaySheetCard.vue'

const router = useRouter();
const recentPlayedSheet = ref();
const popularSheets = ref([]); // 인기 악보 리스트
const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const recommendSheets = ref([]); // 추천 악보 리스트

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
	await searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			recommendSheets.value = data;
		}
	)
	// await getRecommendSheetByUserRecentPlay(
	// 	(res) => {
	// 		if (res && res.data) {
	// 			recommendSheets.value = res.data; // 객체 내 data에 접근해서 할당
	// 			return
	// 		}
	// 		console.error('No valid data structure returned from searchSheetRecommand');
	// 	}
	// )
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

const goToSheetSearchListView = (sort) => {
	router.push({ name: 'sheetSearch', query: { sort } });
}


</script>

<template>
	<div class="w-full h-full flex flex-col justify-between">
		<div class="w-full flex justify-start gap-5">
			<MainViewComponent class="w-[24%]" :sheets="popularSheets" :title="'인기'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('POPULAR')" />
			<MainViewComponent class="w-[24%]" :sheets="newSheets" :title="'최신'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('LATEST')"/>
			<MainViewComponent class="w-[24%]" :sheets="recommendSheets" :title="'추천'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('RECOMMNED')"/>
			<div class="w-[20%] flex flex-col gap-4">
				<div class="h-[440px]">
					<RankingList/>
				</div>
				<div class="h-[200px] min-w-[200px] w-[350px] p-2 flex justify-center items-center relative bg-white bg-opacity-50 rounded-2xl">
					<RecentPlaySheetCard :sheet="sheet" />
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