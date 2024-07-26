<script setup>
import axios from 'axios';
import { ref } from 'vue';
import MainSheetCardInfo from '@/common/sheet/MainSheetCardInfo.vue';

const popularSheets = ref([]); // 인기 악보 리스트
const getPopularsheets = async () => {
	await axios.get("http://localhost:8080/sheets?sort=popular")
		.then(({ data }) => {
			popularSheets.value = data;
			popularSheets.value.map(sheet => sheet.songImg ? sheet.imageUrl = `data:image/jpeg;base64,${sheet.songImg}` : '기본 이미지'); // TODO: songImg가 없으면 기본 로고로.
		}).catch((err)=> alert(err))
}
getPopularsheets();

const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const getnewsheets = async () => {
	await axios.get("http://localhost:8080/sheets?sort=new")
		.then(({ data }) => {
			newSheets.value = data;
			newSheets.value.map(sheet => sheet.songImg ? sheet.imageUrl = `data:image/jpeg;base64,${sheet.songImg}` : '기본 이미지');
		}).catch((err)=> alert(err))
}
getnewsheets();

// TODO : 요청 url 바꾸기
const recommendSheets = ref([]); // 추천 악보 리스트
const getRecommendsheets = async () => {
	await axios.get("http://localhost:8080/sheets?sort=new")
		.then(({ data }) => {
			recommendSheets.value = data;
			recommendSheets.value.map(sheet => sheet.songImg ? sheet.imageUrl = `data:image/jpeg;base64,${sheet.songImg}` : '기본 이미지'); 
		}).catch((err)=> alert(err))
}
getRecommendsheets();

// TODO : 요청 url 바꾸기
const recentChallengedSheet = ref({}); // 최근에 도전했던 악보
const getRecentChallengedsheets = async () => {
	await axios.get("http://localhost:8080/sheets?sort=new")
		.then(({ data }) => {
			recentChallengedSheet.value = data;
			recentChallengedSheet.imageUrl = recentChallengedSheet.songImg ? `data:image/jpeg;base64,${recentChallengedSheet.songImg}` : '기본 이미지'; 
		}).catch((err)=> alert(err))
}
getRecentChallengedsheets();
</script>

<template>
	<div class="container">
		<div class="box-div full-box up-div">
			<div>
				<p class="bold">인기</p>
			</div>
			<div class="scroll-x">
				<template v-if="popularSheets">
					<MainSheetCardInfo v-for="sheet in popularSheets" :key="sheet.id" :sheet="sheet"/>
				</template>
			</div>

			<div style="height: 30px;"></div>

			<div>
				<p class="bold">최신</p>
			</div>
			<div class="scroll-x">
				<template v-if="newSheets">
					<MainSheetCardInfo v-for="sheet in newSheets" :key="sheet.id" :sheet="sheet" />
				</template>
			</div>
		</div>

		<div class="down-div">
			<div class="box-div recommend-box">
				<div>
					<p class="bold">추천</p>
				</div>
				<div class="scroll-x">
					<template v-if="recommendSheets">
						<MainSheetCardInfo v-for="sheet in recommendSheets" :key="sheet.id" :sheet="sheet" />
					</template>
				</div>
			</div>

			<div class="box-div challenged-box">
				<div>
					<div><p class="bold">도전 중인 악보</p></div>
				</div>

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

.scroll-x {
	display: flex;
	overflow-x: scroll;

	-ms-overflow-style: none; /* 인터넷 익스플로러 */
  	scrollbar-width: none; /* 파이어폭스 */
}

.scroll-x::-webkit-scrollbar {
    display: none; /* 웹킷 브라우저에서 스크롤바 숨기기 */
}

</style>