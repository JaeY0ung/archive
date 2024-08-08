<script setup>
import { useRoute, useRouter } from "vue-router";
import { computed, ref, watch } from "vue";
import { localAxios } from "@/util/http-common";
import BigSheetCard from "@/common/sheet/BigSheetCard.vue";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { searchSheetDetail, searchSheetsByFilter, searchStarRateListBySheetId, registerStarRateBySheetId } from "@/api/sheet";

const route = useRoute();
const local = localAxios();
const isPlay = ref("stop");
const router = useRouter();
const sheet = ref({});
const sameLevelSheets = ref([]);
const starRateList = ref([]);
const starRateStatistic = ref([0, 0, 0, 0, 0]); // 별점5 인 리뷰들의 수

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

const starRateAvg = computed(() => {
	starRateStatistic.value = [0, 0, 0, 0, 0];
	let sum = 0;
	starRateList.value.map((starRateInfo) => {
		sum += starRateInfo.starRate;
		starRateStatistic.value[starRateInfo.starRate - 1]++;
	});
	return sum ? round(sum / starRateList.value.length, 2) : "첫번째 별점을 등록해주세요";
})

const starRateRegisterForm = ref({
	content: "",
	starRate: 5,
});

const updateStarRate = (starRate) => {
	starRateRegisterForm.value.starRate = starRate;
}

// 같은 수준의 악보 랜덤으로 가져오기
const searchRandomSameLevelSheets = () => {
	searchSheetsByFilter(
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
const searchStarRateList = () => {
	searchStarRateListBySheetId(
		route.params.sheetId,
		({ data }) => {
			if (!data) return;
			starRateList.value = data;
		}
	)
};

// 별점 등록하기
const registerStarRate = () => {
	if (!starRateRegisterForm.value.content) {
		alert("평가 글을 작성해주세요")
		return;
	}

	if (!starRateRegisterForm.value.starRate) {
		alert("별점을 입력해주세요")
		return;
	}
	registerStarRateBySheetId(
		route.params.sheetId,
		starRateRegisterForm.value,
		(res) => {
			searchStarRateList()
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

watch(sheet, () => {
	searchRandomSameLevelSheets();
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
	<div class="flex w-full h-full justify-between flex-margin  bg-red-300">
		<!-- 왼쪽 -->
		<div class="flex flex-col gap-10 w-[49%] h-full rounded-xl">
			<!-- TODO: 현재 악보 불러오기 기능 미구현 -->
			<BigSheetCard :sheet="sheet" />
			<div>
				<div>비슷한 수준의 악보 추천</div>
				<div class="line"></div>
				<div class="scroll-x flex bg-white/50 rounded-xl h-[100px]">
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

			<div>
				<div>리뷰</div>
				<div class="line"></div>
				<div class=" bg-white/50 rounded-xl">
					<!-- 위 -->
					<div class="flex">
						<!-- 왼쪽 -->
						<div class="w-[20%] flex flex-col justify-center items-center">
							<div>{{ starRateAvg }}</div>
							<div class="rating rating-lg">
								<input type="radio" name="rating-10" class="mask mask-star-2 bg-green-500"/>
							</div>
							<div>{{ totalStarRateCount }} 개의 평가</div>
						</div>
						<!-- 중간 -->
						<div class="w-[30%] flex flex-col-reverse">
							<template v-for="(starRateCount, index) in starRateStatistic" :key="index">
								<div class="flex text-center">
									<div class="rating">
										<input type="radio" name="rating-10" class="mask mask-star-2  bg-green-500"/>
									</div>
									<div class="flex items-center">{{ index + 1 }}</div>
									<div class="flex items-center">
										<!-- 가장 높은 starRate의 그래프 길이는 일정하도록 설정 -->
										<div class="bg-blue-600" :style="{'width':  `${starRateCount / starRateMaxCount * 170}px`, 'height': '10px' }"></div>
									</div>
								</div>
							</template>
						</div>
						<!-- 오른쪽 -->
						<div class="w-[50%] h-full m-auto">
							<form @prevent.default="registerStarRate">
								<div class="rating rating-lg flex justify-center">
									<input type="radio" name="rating-10" class="rating-hidden" />
									<template v-for="i in 5">
										<input type="radio" name="rating-10" class="mask mask-star-2 bg-green-500" @change="updateStarRate(i)" :checked="{'checked': i == 5}"/>
									</template>
								</div>
								<div class="w-full flex">
									<textarea type="text" class="input input-bordered w-[90%]" placeholder="평가" v-model="starRateRegisterForm.content" />
									<div @click="registerStarRate" class="btn btn-primary">등록</div>
								</div>
							</form>
						</div>
					</div>
					<!-- 아래 -->
					<div class="flex w-full h-[100px] flex-col gap-3 scroll-y">
						<div class="flex w-full bg-white" v-for="(starRateInfo, index) in starRateList" :key="index">
							<div class="flex-1">{{ starRateInfo.nickname }}</div>
							<div class="flex-1">{{ starRateInfo.content }}</div>
							<div class="flex-1">{{ starRateInfo.starRate }}</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- 오른쪽 -->
		<div class="flex flex-col gap-5 w-[49%]  bg-white/50 rounded-xl mb-[10px]">
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
</style>
