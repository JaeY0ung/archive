<script setup>
import { ref } from 'vue';
import { localAxios } from '@/util/http-common';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';

const local = localAxios();
const popularSheets = ref([]); // 인기 악보 리스트

const getPopularsheets = async () => {
	const params = { sort: "POPULAR" }
	await local.get("/sheets", { params } )
		.then(({ data }) => {
			popularSheets.value = data;
			popularSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지'); // TODO: songImg가 없으면 기본 로고로.
		}).catch((err)=> console.log(err))
}
getPopularsheets();

const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const getnewsheets = async () => {
	const params = { sort: "LATEST" }
	await local.get("/sheets", { params })
		.then(({ data }) => {
			newSheets.value = data;
			newSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		}).catch((err)=> console.log(err))
}
getnewsheets();

const recommendSheets = ref([]); // 추천 악보 리스트
const getRecommendsheets = async () => {
	const params = {
		level: 1, // 유저의 티어
		sort: "RANDOM"
	}
	await local.get("/sheets", { params })
		.then(({ data }) => {
			recommendSheets.value = data;
			recommendSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지'); 
		}).catch((err)=> console.log(err))
}
getRecommendsheets();

const recentChallengedSheet = ref({}); // 최근에 도전했던 악보
// const getRecentChallengedsheets = async () => {
// 	await axios.get("http://localhost:8080/sheets/recent-challenge")
// 		.then(({ data }) => {
// 			recentChallengedSheet.value = data;
// 			recentChallengedSheet.imageUrl = recentChallengedSheet.songImg ? `data:image/jpeg;base64,${recentChallengedSheet.songImg}` : '기본 이미지'; 
// 		}).catch((err)=> alert(err))
// }
// getRecentChallengedsheets();
</script>

<template>
	<div class="container">
		<div class="box-div full-box up-div">
			<div>
				<p class="bold">인기</p>
			</div>
			<div class="scroll-x flex">
				<template v-if="popularSheets">
					<SmallSheetCard v-for="sheet in popularSheets" :key="sheet.id" :sheet="sheet"/>
				</template>
			</div>

			<div style="height: 30px;"></div>

			<div>
				<p class="bold">최신</p>
			</div>
			<div class="scroll-x flex">
				<template v-if="newSheets">
					<SmallSheetCard v-for="sheet in newSheets" :key="sheet.id" :sheet="sheet" />
				</template>
			</div>
		</div>

		<div class="down-div">
			<div class="box-div recommend-box">
				<div>
					<p class="bold">추천</p>
				</div>
				<div class="scroll-x flex">
					<template v-if="recommendSheets">
						<SmallSheetCard v-for="sheet in recommendSheets" :key="sheet.id" :sheet="sheet" />
					</template>
				</div>
			</div>

			<div class="box-div challenged-box">
				<div>
					<div><p class="bold">도전 중인 악보</p></div>
				</div>

				
			</div>
		</div>
	</div>
</template>

<style scoped>
.container {
	margin: 0 auto;
	align-items: center;
	width: 100%;
}

.box-div  {
	padding: 10px;
	background-color: rgb(255, 255, 255, 0.5);
	border-radius: 15px;
}

.full-box {
	width: 100%;
}

.recommend-box {
	width: 70%;
}

.challenged-box {
	width: 25%;
}

.up-div {
	margin-bottom: 50px;
}

.down-div {
	display: flex;
	justify-content: space-between;
}

</style>