<script setup>
import axios from 'axios';
import { ref } from 'vue';

const popularSheets = ref([]); // 인기 악보 리스트

const newSheets = ref([]); // 새로 나온(New) 악보 리스트

const recommendSheets = ref([]); // 추천 악보 리스트

const recentChallengedSheet = ref({}); // 최근에 도전했던 악보

const getPopularsheets = async() => {
	await axios.get("http://localhost:8080/sheets?sort=new")
		.then(({data}) => {
			popularSheets.value = data;
		})
	console.log(popularSheets.value);
}

getPopularsheets()
</script>

<template>
	<div class="container">
		<div class="up-div">
			<div>인기</div>
			<div class="scroll-x">
				<template v-for="sheet in popularSheets">
					<div class="card">
						<div>
							<RouterLink :to="{ name: 'sheetDetail', params: { sheetId :  sheet.id} }">악보명: {{ sheet.title }}</RouterLink>
						</div>
						<div>파일 이름: {{ sheet.fileName }}</div>
						<!-- <img src="C:/SSAFY/Archive/file/user-img/img.jpg" alt=""> -->
						<div>티어: {{ sheet.level }}</div>
						<div>업로더 닉네임: {{ sheet.uploader.nickname }}</div>
					</div>
				</template>
			</div>
			<div>New</div>
			<div class="scroll-x">
				<template v-for="i in 10">
					<div class="card">
						카드{{ i }}
					</div>
				</template>
			</div>
		</div>

		<div class="down-div">
			<div class="down-left-div">
				<div>이런 악보는 어때요?</div>
				<div class="scroll-x">
					<template v-for="i in 10">
						<div class="card">
							카드{{ i }}
						</div>
					</template>
				</div>
			</div>

			<div>
				<div class="down-right-div">
					<div>도전 중인 악보</div>
				</div>

				<div class="down-right-player-div">
					<div>도전 중인 악보</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.container {
	margin: 30px auto;
	align-items: center;
}

.scroll-x {
	text-align: center;

	display: flex;
	overflow-x: scroll;

	-ms-overflow-style: none; /* 인터넷 익스플로러 */
  	scrollbar-width: none; /* 파이어폭스 */
}

.scroll-x::-webkit-scrollbar {
    display: none; /* 웹킷 브라우저에서 스크롤바 숨기기 */
}

.up-div {
	width: 85vw;
	padding: 10px;
	background-color: rgb(255, 255, 255, 0.3);
	border-radius: 15px;
	margin-bottom: 100px;
}

.down-div {
	display: flex;
	justify-content: flex-start;
}

.down-left-div {
	width: 60vw;
	padding: 10px;
	background-color: rgb(255, 255, 255, 0.3);
	border-radius: 15px;
}

.down-right-div {
	width: 20vw;
	padding: 10px;
	background-color: rgb(255, 255, 255, 0.3);
	border-radius: 15px;
}

.down-right-player-div {
	width: 20vw;
	padding: 10px;
	background-color: rgb(255, 255, 255, 0.3);
	border-radius: 15px;
}


.card {
	flex: 0 0 auto;
	height: 130px;
	width: 300px;

	display: flex;
    justify-content: center;
    align-items: center;

	background-color: rgb(255, 255, 255);
	margin: 10px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
</style>