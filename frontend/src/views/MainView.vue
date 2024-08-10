<script setup>
import { ref } from 'vue';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import { useRouter } from "vue-router"
import {searchSheetsByFilter, getRecommendSheetByUserRecentPlay} from '@/api/sheet';

const router = useRouter();

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
		}
	)
}

const getnewsheets = async () => {
	searchSheetsByFilter(
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

// const getRecentChallengedsheets = async () => {
// 	searchRecentChallengedsheets(
// 		({ data }) => {
// 			if (!data) return;
// 			recentChallengedSheet.value = data;
// 		}
// 	)
// }

getPopularsheets();
getnewsheets();
getRecommendSheets();
const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};
</script>

<template>
	<div class="flex w-full h-full flex-col ">
		<div class="flex flex-col w-full gap-[50px] bg-white/50 rounded-xl mb-[50px]">

			<div class="flex flex-grow flex-col w-full h-[150px] gap-5">
				<div class="bold">인기</div>
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex w-full absolute scroll-x">
						<template v-if="popularSheets">
							<SmallSheetCard v-for="sheet in popularSheets" :key="sheet.id" :sheet="sheet" @click="goToSheetDetail(sheet.id)"/>
						</template>
					</div>
				</div>
			</div>

			<div class="flex flex-grow flex-col w-full h-[150px] gap-5">
				<div class="bold">최신</div>
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex w-full absolute scroll-x">
						<template v-if="newSheets">
							<SmallSheetCard v-for="sheet in newSheets" :key="sheet.id" :sheet="sheet" @click="goToSheetDetail(sheet.id)"/>
						</template>
					</div>
				</div>
			</div>
		</div>

		<div class="flex justify-between">
			<div class="flex flex-col gap-5 w-[70%] h-[150px] p-[10px] bg-white/50 rounded-xl">
				<div  class="bold">추천</div>
				<div class="flex flex-grow w-full h-full relative overflow-hidden items-center">
					<div class="flex w-full absolute scroll-x">
						<template v-if="recommendSheets">
							<SmallSheetCard v-for="sheet in recommendSheets" :key="sheet.id" :sheet="sheet" @click="goToSheetDetail(sheet.id)"/>
						</template>
					</div>
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