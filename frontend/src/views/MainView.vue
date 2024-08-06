<script setup>
import { ref } from 'vue';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import { searchSheetsByFilter, searchRecentChallengedsheets } from '@/api/sheet';

const popularSheets = ref([]); // 인기 악보 리스트
const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const recommendSheets = ref([]); // 추천 악보 리스트
const recentChallengedSheet = ref({}); // 최근에 도전했던 악보

const getPopularsheets = async () => {
	searchSheetsByFilter(
		{ sort: "POPULAR" },
		({ data }) => {
			if (!data) return;
			popularSheets.value = data;
			popularSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지'); // TODO: songImg가 없으면 기본 로고로.
		}
	)
}

const getnewsheets = async () => {
	searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			newSheets.value = data;
			newSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		}
	)
}

const getRecommendsheets = async () => {
	searchSheetsByFilter(
		{ levels: 1, sort: "RANDOM" },
		({ data }) => {
			if (!data) return;
			recommendSheets.value = data;
			recommendSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지'); 
		}
	)
}

const getRecentChallengedsheets = async () => {
	searchRecentChallengedsheets(
		({ data }) => {
			if (!data) return;
			recentChallengedSheet.value = data;
			recentChallengedSheet.imageUrl = recentChallengedSheet.songImg ? `data:image/jpeg;base64,${recentChallengedSheet.songImg}` : '기본 이미지'; 
		}
	)
}

getPopularsheets();
getnewsheets();
getRecommendsheets();
// getRecentChallengedsheets();
</script>

<template>
	<div class="m-auto w-full flex flex-col gap-[50px]">

		<div class="flex flex-col gap-[50px] p-[10px] bg-white/50 rounded-xl w-full mb-[50px]">
			<div class="flex flex-col gap-5">
				<div class="bold">인기</div>
				<div class="scroll-x flex">
					<template v-if="popularSheets">
						<SmallSheetCard v-for="sheet in popularSheets" :key="sheet.id" :sheet="sheet"/>
					</template>
				</div>
			</div>
			<div class="flex flex-col gap-5">
				<div class="bold">최신</div>
				<div class="scroll-x flex">
					<template v-if="newSheets">
						<SmallSheetCard v-for="sheet in newSheets" :key="sheet.id" :sheet="sheet" />
					</template>
				</div>
			</div>
		</div>

		<div class="flex justify-between">
			<div class="flex flex-col gap-5 w-[70%] p-[10px] bg-white/50 rounded-xl">
				<div  class="bold">추천</div>
				<div class="scroll-x flex">
					<template v-if="recommendSheets">
						<SmallSheetCard v-for="sheet in recommendSheets" :key="sheet.id" :sheet="sheet" />
					</template>
				</div>
			</div>

			<div class="p-[10px] bg-white/50 rounded-xl w-[25%]">
				<div>
					<div><p class="bold">도전 중인 악보</p></div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
</style>