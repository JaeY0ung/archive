<script setup>
import { useRoute } from "vue-router";
import { ref, watch } from "vue";
import { localAxios } from "@/util/http-common";
import Sheet from "@/common/sheet/Sheet.vue";
import SheetPlayNavigation from "@/common/sheet/SheetPlayNavigation.vue";
import BigSheetCard from "@/common/sheet/BigSheetCard.vue";
import SmallSheetCard from  "@/common/sheet/SmallSheetCard.vue";
const route = useRoute();
const local = localAxios();
const isPlay = ref("stop");

const sheet = ref({});
const sameLevelSheets = ref([]);
const starRateList = ref([]);
const starRateAvg = ref(0.0); // 별점 평균
const starRateStatistic = ref([0, 0, 0, 0, 0, 0]); // 별점 0,1,2,3,4,5 인 리뷰들의 수
const starRateRegisterForm = ref({
	content: "",
	starRate: 0,
});

const updateStarRate = (starRate) => {
	starRateRegisterForm.value.starRate = starRate;
}

// 악보 세부 정보 가져오기
const searchSheetDetail = () => {
	local
		.get(`/sheets/${route.params.sheetId}`)
		.then(({ data }) => {
			sheet.value = data;
			sheet.value.imageUrl = `data:image/jpeg;base64,${data.songImg}`;
		})
		.catch((err) => {
			alert(err);
		});
};

// 같은 수준의 악보 랜덤으로 가져오기
const searchRandomSameLevelSheets = () => {
	let params = {
		level: sheet.value.level,
		sort: "RANDOM",
		keyword: "",
	};

	local.get("/sheets", { params })
		.then(({ data }) => {
			sameLevelSheets.value = data;
			sameLevelSheets.value.map((s) =>
				s.songImg ? (s.imageUrl = `data:image/jpeg;base64,${s.songImg}`) : "기본 이미지"
			);
		})
		.catch((err) => {
			alert(err);
		});
};

// 별점 가져오기
const searchStarRateList = () => {
	local
		.get(`/sheets/${route.params.sheetId}/star-rates`)
		.then(({ data }) => {
			console.log("starRateList: ", data);
			starRateList.value = data;
			let sum = 0;
			starRateList.value.map((starRateInfo) => {
				sum += starRateInfo.starRate;
				starRateStatistic.value[starRateInfo.starRate]++;
			});
			starRateAvg.value = round(sum / starRateList.value.length, 2);
		})
		.catch((err) => {
			alert(err);
		});
};

// 별점 등록하기
const registerStarRate = () => {
	local
		.post(`/sheets/${route.params.sheetId}/star-rates`, starRateRegisterForm)
		.then(({ data }) => {
			console.log("starRateList: ", data);
			starRateList.value = data;
			let sum = 0;
			starRateList.value.map((starRateInfo) => {
				sum += starRateInfo.starRate;
				starRateStatistic.value[starRateInfo.starRate]++;
			});
			starRateAvg.value = round(sum / starRateList.value.length, 2);
		})
		.catch((err) => {
			alert(err);
		});
};

function round(number, place) {
	return Math.round(number * 10 ** place) / 10 ** place;
}

searchSheetDetail();

watch(sheet, (newValue, oldValue) => {
	searchRandomSameLevelSheets();
});

searchStarRateList();
</script>

<template>
	<div class="flex justify-between flex-margin h-full">
		<!-- 왼쪽 -->
		<div class="flex flex-col gap-10 w-[49%] h-full rounded-xl">
			<BigSheetCard :sheet />

			<div>
				<div>비슷한 수준의 악보 추천</div>
				<div class="line"></div>
				<div class="scroll-x flex h-full bg-white/50 rounded-xl">
					<SmallSheetCard v-for="(sheet, index) in sameLevelSheets" :key="index" :sheet="sheet"/>
					<!-- <BigSheetCard v-for="(sheet, index) in sameLevelSheets" :key="index" :sheet="sheet" /> -->
				</div>
			</div>

			<div>
				<div>리뷰</div>
				<div class="line"></div>
				<div class="h-full bg-white/50 rounded-xl flex justify-between">
					<div class="flex flex-1 justify-center items-center">
						<div>
							<img :src="require('@/assets/img/star-fill.svg')" alt="별" width="30px" />
						</div>
						<div>{{ starRateAvg }} / 5.00</div>
					</div>

					<div class="flex flex-1 items-center flex-row gap-3">
						<div v-for="(starRateCount, index) in starRateStatistic" :key="index">
							<template v-if="index != 0"> {{ index }} : {{ starRateCount }} </template>
						</div>
					</div>
				</div>
				<div class="flex">
					<div class="w-[60%]">
						<input v-model="starRateRegisterForm.content" type="text" class="input input-bordered w-full" placeholder="평가" />
					</div>
					<div class="starpoint_wrap w-[30%]">
						<div class="starpoint_box w-full">
							<label for="starpoint_1" class="label_star" title="0.5"><span class="blind">0.5점</span></label>
							<label for="starpoint_2" class="label_star" title="1"><span class="blind">1점</span></label>
							<label for="starpoint_3" class="label_star" title="1.5"><span class="blind">1.5점</span></label>
							<label for="starpoint_4" class="label_star" title="2"><span class="blind">2점</span></label>
							<label for="starpoint_5" class="label_star" title="2.5"><span class="blind">2.5점</span></label>
							<label for="starpoint_6" class="label_star" title="3"><span class="blind">3점</span></label>
							<label for="starpoint_7" class="label_star" title="3.5"><span class="blind">3.5점</span></label>
							<label for="starpoint_8" class="label_star" title="4"><span class="blind">4점</span></label>
							<label for="starpoint_9" class="label_star" title="4.5"><span class="blind">4.5점</span></label>
							<label for="starpoint_10" class="label_star" title="5"><span class="blind">5점</span></label>

							<input type="radio" name="starpoint" id="starpoint_1" class="star_radio" @change="updateStarRate(0.5)">
							<input type="radio" name="starpoint" id="starpoint_2" class="star_radio" @change="updateStarRate(1)">
							<input type="radio" name="starpoint" id="starpoint_3" class="star_radio" @change="updateStarRate(1.5)">
							<input type="radio" name="starpoint" id="starpoint_4" class="star_radio" @change="updateStarRate(2)">
							<input type="radio" name="starpoint" id="starpoint_5" class="star_radio" @change="updateStarRate(2.5)">
							<input type="radio" name="starpoint" id="starpoint_6" class="star_radio" @change="updateStarRate(3)">
							<input type="radio" name="starpoint" id="starpoint_7" class="star_radio" @change="updateStarRate(3.5)">
							<input type="radio" name="starpoint" id="starpoint_8" class="star_radio" @change="updateStarRate(4)">
							<input type="radio" name="starpoint" id="starpoint_9" class="star_radio" @change="updateStarRate(4.5)">
							<input type="radio" name="starpoint" id="starpoint_10" class="star_radio" @change="updateStarRate(5)">
							<span class="starpoint_bg"></span>
						</div>
					</div>
					<div>
						<button @click="registerStarRate" class="btn btn-primary">등록</button>
					</div>
				</div>
				<span>{{ starRateRegisterForm.starRate }}</span>
				<div class="flex flex-col gap-3">
					<div class="flex gap-10" v-for="(starRateInfo, index) in starRateList" :key="index">
						<div>{{ starRateInfo.nickname }}</div>
						<div>{{ starRateInfo.content }}</div>
						<div>{{ starRateInfo.starRate }}</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 오른쪽 -->
		<div class="flex flex-col gap-5 w-[49%] h-full p-3 bg-white/50 rounded-xl">
			<SheetPlayNavigation class="flex-none h-[30px]" @play="isPlay = 'play'" @pause="isPlay = 'pause'"
				@stop="isPlay = 'stop'" />
			<div class="bg-black rounded-xl h-full"></div>
			<!-- <Sheet class="rounded-xl h-full" :isPlay="isPlay" /> -->
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

.starpoint_wrap{display:inline-block;}
.starpoint_box{position:relative;background:url(https://ido-archive.github.io/svc/etc/element/img/sp_star.png) 0 0 no-repeat;font-size:0;}
.starpoint_box .starpoint_bg{display:block;position:absolute;top:0;left:0;height:18px;background:url(https://ido-archive.github.io/svc/etc/element/img/sp_star.png) 0 -20px no-repeat;pointer-events:none;}
.starpoint_box .label_star{display:inline-block;width:10px;height:18px;box-sizing:border-box;}
.starpoint_box .star_radio{opacity:0;width:0;height:0;position:absolute;}
.starpoint_box .star_radio:nth-of-type(1):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(1):checked ~ .starpoint_bg{width:10%;}
.starpoint_box .star_radio:nth-of-type(2):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(2):checked ~ .starpoint_bg{width:20%;}
.starpoint_box .star_radio:nth-of-type(3):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(3):checked ~ .starpoint_bg{width:30%;}
.starpoint_box .star_radio:nth-of-type(4):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(4):checked ~ .starpoint_bg{width:40%;}
.starpoint_box .star_radio:nth-of-type(5):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(5):checked ~ .starpoint_bg{width:50%;}
.starpoint_box .star_radio:nth-of-type(6):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(6):checked ~ .starpoint_bg{width:60%;}
.starpoint_box .star_radio:nth-of-type(7):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(7):checked ~ .starpoint_bg{width:70%;}
.starpoint_box .star_radio:nth-of-type(8):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(8):checked ~ .starpoint_bg{width:80%;}
.starpoint_box .star_radio:nth-of-type(9):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(9):checked ~ .starpoint_bg{width:90%;}
.starpoint_box .star_radio:nth-of-type(10):hover ~ .starpoint_bg,
.starpoint_box .star_radio:nth-of-type(10):checked ~ .starpoint_bg{width:100%;}

.blind{position:absolute;clip:rect(0 0 0 0);margin:-1px;width:1px;height: 1px;overflow:hidden;}
</style>
