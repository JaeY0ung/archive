<script setup>
import Sheet from "@/common/sheet/Sheet.vue";
import BigSheetCard from "@/common/sheet/BigSheetCard.vue";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import { useUserStore } from '@/stores/user';
import { useRoute, useRouter } from "vue-router";
import { showLoginRequestAlert } from "@/util/alert"
import { searchSheetDetail, searchSheetsByFilter, searchStarRateListBySheetId, registerStarRateBySheetId } from "@/api/sheet";

const userStore = useUserStore();
const { userInfo, isLogin } = storeToRefs(userStore);
const route = useRoute();
const router = useRouter();
const sheet = ref({});
const sameLevelSheets = ref([]);
const starRateList = ref([]);
const starRateStatistic = ref([0, 0, 0, 0, 0]); 
const starRateAvg = ref(null);

const totalStarRateCount = computed(() => {
	let sum = 0;
	starRateStatistic.value.map(s => sum += s)
	return sum;
})

const starRateMaxCount = computed(() => {
	let max = 0;
	starRateStatistic.value.map(s => {
		if (s > max) max = s
	})
	return max;
})

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

const updateStarRate = (starRate) => {
	starRateRegisterForm.value.starRate = starRate;
}

// 같은 수준의 악보 랜덤으로 가져오기
const searchRandomSameLevelSheets = async () => {
	await searchSheetsByFilter(
		{ levels: sheet.value.level, sort: "RANDOM" },
		({ data }) => {
			if (!data) return;
			const sheets = [];
			data.map(s => {
				if (s.id != route.params.sheetId) sheets.push(s);
			})
			sameLevelSheets.value = sheets;
		}
	)
};

// 별점 가져오기
const searchStarRateList = async () => {
	await searchStarRateListBySheetId(
		route.params.sheetId,
		({ data }) => {
			if (!data) return;
			starRateList.value = data;
			calculateStarRateStats();
		}
	)
};

// 별점 등록하기
const registerStarRate = async () => {
	if (!isLogin.value) {
        showLoginRequestAlert(router);
        return;
    }
	if (!starRateRegisterForm.value.content) {
		alert("평가 글을 작성해주세요")
		return;
	} else if (!starRateRegisterForm.value.starRate) {
		alert("별점을 입력해주세요")
		return;
	}
	await registerStarRateBySheetId(
		route.params.sheetId,
		starRateRegisterForm.value,
		async (res) => {
			await searchStarRateList()
		}
	)
};

const round = (number, place) => {
	return Math.round(number * 10 ** place) / 10 ** place;
}

searchSheetDetail(
	route.params.sheetId, 
	({ data }) => {
		if (!data) return;
		sheet.value = data;
	}
)

watch(sheet, async () => {
	await searchRandomSameLevelSheets();
});

searchStarRateList();

const goToSheetDetail = (sheetId) => {
	router.push({
		name: 'sheetDetail',
		params: { 'sheetId': sheetId },
		replace: true
	})
}
</script>

<template>
	<div class="flex w-full h-full justify-between p-5 ">
		<!-- 1. 악보 디테일 정보 -->
		<div class="flex flex-col gap-3 w-[50%] h-full">
			<!-- 1-1. 악보 카드 -->
			<BigSheetCard :sheet="sheet" class="rounded-xl shadow-lg" />
			
			<!-- 1-2. 비슷한 수준의 악보 추천 -->
			<div class="bg-white h-[30%] bg-opacity-60 rounded-xl shadow-md">
				<div class="text-2xl font-semibold text-gray-700 ml-2 mb-2">비슷한 수준의 악보 추천</div>
				<hr class=" border-gray-300 mb-2">
				<div class="scroll-x flexrounded-b-xl h-[100px]">
					<template v-if="sameLevelSheets && sameLevelSheets.length != 0">
						<template v-for="sheet in sameLevelSheets" :key="sheet.id">
							<SmallSheetCard :sheet="sheet" @click="goToSheetDetail(sheet.id)" class="cursor-pointer h-fit" />
						</template>
					</template>
					<template v-else>
						<div class="w-full flex justify-center items-center">
							아직 없습니다.. 죄송합니다 ㅠ
						</div>
					</template>
				</div>
			</div>

			<!-- 1-3. 리뷰 섹션 -->
			<div class="flex flex-col h-[50%] w-full p-2 bg-white bg-opacity-50 rounded-xl shadow-lg">
				<div class="text-2xl font-semibold text-gray-700 mb-2">리뷰 [{{ starRateList.length }}]</div>
				<hr class=" border-gray-300 mb-2">

				<!-- 1.3.1 별점 요약 -->
				<div class="flex justify-around w-full bg-white bg-opacity-40 rounded-xl shadow-md">
					<!-- 1) 별점 평균 -->
					<div class="w-[40%] flex flex-col items-center justify-center text-center bg-gray-100 shadow-md bg-opacity-50 p-2 rounded-lg">
						<div v-if="starRateAvg">
							<div class="flex justify-center">
								<div class="text-5xl font-bold text-gray-600">{{ starRateAvg }}</div>
								<div class="text-gray-600 text-xl mt-6">/ 5.0</div>
							</div>
							<div class="flex justify-center items-center mt-2">
								<template v-for="n in 5">
									<span 
										class="inline-block w-6 h-6 mask mask-star-2" 
										:class="n <= Math.round(starRateAvg) ? 'bg-purple-500' : 'bg-gray-300'">
									</span>
								</template>
							</div>
						</div>
						<div v-else class="text-xl font-semibold text-gray-500">첫번째 별점을<br>등록해주세요</div>
						<div class="text-sm text-gray-600 mt-2">{{ totalStarRateCount }}개의 평가</div>
					</div>
					<!-- 2) 별점 그래프 -->
					<div class="w-[60%] flex flex-row justify-around items-end p-4">
						<template v-for="(starRateCount, index) in starRateStatistic" :key="index">
						<!-- <template v-for="(starRateCount, index) in [1,1,2,3,3]" :key="index"> -->
							<div class="flex flex-col items-center">
								<!-- 회색 막대 배경 -->
								<div 
									class="bg-gray-200 rounded-xl shadow-md"
									:style="{ 
										'height': '100px', 
										'width': '20px',
										'position': 'relative',
										'overflow': 'hidden'
									}"
								>
								<!-- 연보라색 막대 -->
								<div 
									class="bg-purple-200 rounded-xl absolute bottom-0 shadow-md shadow-up shadow-right"
									:style="{ 
										'height': `${(starRateCount / starRateMaxCount) * 100}%`, 
										'width': '100%'
									}"
								></div>
								</div>
								<!-- 막대 하단의 숫자 -->
								<div class="mt-2 text-sm text-gray-600">{{ index + 1 }}점</div>			
							</div>
						</template>
					</div>
				</div>

				<!-- 1.3.2 평가 등록 -->
				<div class="w-full m-auto">
					<form @prevent.default="registerStarRate" class="flex flex-row gap-1 justify-between">
						<!-- (1) 별점 클릭 -->
						<div class="w-[50%] rating rating-lg flex justify-center bg-white bg-opacity-50 mt-2 mb-2 shadow-md rounded-xl">
							<input type="radio" name="rating-10" class="rating-hidden" />
							<div>
								<template v-for="i in 5" >
								<input 
									type="radio" 
									name="rating-10" 
									class="mask mask-star-2 bg-yellow-400 rounded-xl shadow-md" 
									@change="updateStarRate(i)" 
									:checked="{'checked': i == 5}"
									style="width: 40px; height: 40px; margin-right: 5px;" 	
								/>
								</template>
								<div class="text-xs text-gray-500 text-center">별점을 클릭해주세요.</div>
							</div>
						</div>
						<!-- (2) 텍스트 리뷰 등록 -->
						<div class="w-full flex bg-white bg-opacity-50 mt-2 mb-2 rounded-xl justify-center shadow-md">
							<textarea type="text" class="input input-bordered w-[90%] m-2 pl-1" placeholder="한 줄 리뷰를 입력해주세요." v-model="starRateRegisterForm.content" />
							<div @click="registerStarRate" class="btn bg-white m-2 shadow-md text-lg">등록</div>
						</div>
					</form>
				</div> 
				
				<!-- 1.3.3 평가 리스트  -->
				<!-- <div class="max-h-[200px] overflow-y-auto">
					<div v-for="(starRateInfo, index) in starRateList" :key="index" class="flex items-center p-4 bg-gray-100 mb-2 rounded-lg shadow-md">
						<div class="w-1/4 text-gray-800 font-semibold">{{ starRateInfo.nickname }}</div>
						<div class="w-1/4 text-yellow-500 text-xl"><i class="fas fa-star"></i> {{ starRateInfo.starRate }}</div>
						<div class="w-1/2 text-gray-600">{{ starRateInfo.content }}</div>
					</div>
				</div> -->
				<div class="max-h-[200px] overflow-y-auto">
					<div v-for="(starRateInfo, index) in starRateList" :key="index" class="flex items-center p-4 bg-gray-100 mb-2 rounded-lg shadow-md">
						<div class="w-12 h-12 mr-4">
							<img 
								:src="starRateInfo.userImg ? starRateInfo.userImg : require('@/assets/img/common/default_profile.png')" 
								alt="프로필 사진" 
								class="rounded-full object-cover w-full h-full">
						</div>
						
						<div class="w-1/4 text-gray-800 font-semibold">{{ starRateInfo.nickname }}</div>
						
						<!-- 별점 표시 -->
						<div class="w-1/4 text-yellow-500 text-xl flex">
							<template v-for="n in 5">
								<span 
									class="inline-block w-5 h-5 mask mask-star-2 shadow-md" 
									:class="n <= starRateInfo.starRate ? 'bg-purple-400' : 'bg-gray-300'">
								</span>
							</template>
						</div>
						
						<div class="w-1/2 text-gray-600">{{ starRateInfo.content }}</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 2. musicXML -->
		<div class="flex flex-col gap-5 w-[49%]  bg-white rounded-xl mb-[10px] shadow-xl">
			<Sheet :sheetId="Number(route.params.sheetId)"/>
		</div>
	</div>
</template>

<style scoped>
.line::before {
	content: "";
	display: block;
	border-top: 2px solid rgba(0, 0, 0, 0.5);
	width: 100%;
}

.shadow-up {
	box-shadow: 0 -4px 6px -1px rgba(0, 0, 0, 0.1), 0 -2px 4px -1px rgba(0, 0, 0, 0.06);
}

.shadow-right {
	box-shadow: 4px 0 6px -1px rgba(0, 0, 0, 0.1), 2px 0 4px -1px rgba(0, 0, 0, 0.06);
}
</style>
