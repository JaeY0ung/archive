<script setup>
import { ref } from 'vue';
import { useRouter } from "vue-router"
import { searchSheetsByFilter, getRecommendSheetByUserRecentPlay, searchRecentPlayedsheet } from '@/api/sheet';
import MainViewComponent from '@/common/main/MainViewComponent.vue';
import RankingList from '@/common/main/RankingList.vue';
import RecentPlaySheetCard from '@/common/sheet/RecentPlaySheetCard.vue'
import MainSwipeComponent from '@/common/main/MainSwipeComponent.vue';

const router = useRouter();
const recentPlayedSheet = ref();
const popularSheets = ref([]); // 인기 악보 리스트
const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const recommendSheets = ref([]); // 추천 악보 리스트
const randomSheets = ref([]);

const getPopularsheets = async () => {
	await searchSheetsByFilter(
		{ sort: "POPULAR" },
		({ data }) => {
			if (!data) return;
			popularSheets.value = data;
		}
	)
	console.log(popularSheets.value);
}

const getNewSheets = async () => {
	await searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			newSheets.value = data;
		}
	)
	console.log(newSheets.value);
}

const getRandomSheets = async () => {
	await searchSheetsByFilter(
		{ sort: "RANDOM" },
		({ data }) => {
			if (!data) return;
			randomSheets.value = data;
		}
	)
	console.log(randomSheets.value);
}

const getRecommendSheets = async () => {
	await searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			recommendSheets.value = data;
		}
	)
	console.log(recommendSheets.value);
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
getNewSheets();
getRecommendSheets();
getRandomSheets();

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};

const goToSheetSearchListView = (sort) => {
	router.push({ name: 'sheetSearch', query: { sort } });
}



</script>

<template>
	<div class="w-full h-full flex flex-col justify-between">
		<div class="w-full flex flex-col justify-start gap-5">
			<div class="h-[45vh] flex flex-row">
				<div class="flex-6 p-2 flex justify-center items-center">
					<RecentPlaySheetCard :sheet="sheet" />
				</div>
				<MainSwipeComponent class="flex-1" :sheets="popularSheets" :title="'인기'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('POPULAR')"/>
				<MainSwipeComponent class="flex-1" :sheets="newSheets" :title="'최신'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('LATEST')"/>
				<MainSwipeComponent class="flex-1" :sheets="recommendSheets" :title="'추천'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('RECOMMNED')"/>
				<div class="flex-1 h-full">
					<RankingList/>
				</div>
			</div>

			<div class="h-[40vh] flex flex-row gap-2">
				<MainViewComponent  :sheets="popularSheets" :title="'인기'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('POPULAR')" />
				<MainViewComponent :sheets="newSheets" :title="'최신'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('LATEST')"/>
				<MainViewComponent :sheets="recommendSheets" :title="'추천'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('RECOMMNED')"/>
				<MainViewComponent :sheets="randomSheets" :title="'랜덤'" @goToSheetDetail="goToSheetDetail" @goToSheetSearchListView="goToSheetSearchListView('RANDOM')"/>
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