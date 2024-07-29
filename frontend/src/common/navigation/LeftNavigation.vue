<script setup>
import Home from '@/common/icons/Home.vue'
import My from '@/common/icons/My.vue'
import Search from '@/common/icons/Search.vue'
import Card from '@/common/icons/Card.vue'
import Upload from '@/common/icons/Upload.vue'
import NotePixel from '@/common/icons/NotePixel.vue'

import { ref } from 'vue';
import { storeToRefs } from "pinia";
import { useUserStore } from '@/stores/user'
import { useLeftNavigationStore } from '@/stores/leftNavigation'
import { useRouter } from 'vue-router';  // router import 추가

const router = useRouter();  // router 인스턴스 생성
const userStore = useUserStore();
const leftNavigationStore = useLeftNavigationStore();
const { navVisibility } = storeToRefs(leftNavigationStore);

const pages = ref([
  { name: 'main', title: '메인 페이지' },
  { name: 'mypage', title: '마이페이지' },
  { name: 'sheetSearch', title: '악보 검색' },
  { name: 'payment', title: '충전하기' },
  { name: 'sheetUpload', title: '악보 업로드' },
  { name: 'waitBattle', title: '배틀하기' },
])

const goLogout = async () => {
  try {
    await userStore.userLogout();  // store에서 직접 액션 호출
    router.push({ name: 'main' });
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error);
    // 사용자에게 에러 메시지 표시 (예: alert 또는 toast 메시지)
    alert('로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.');
  }
}
</script>

<template>
	<nav class="left-nav-container">
		<br><br><br><br><br><br><br>

		<div class="nav-main">
		<div v-for="page in pages" :key="page.name">
			<RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav">
				<div style="width: 25%;">
					<Home v-if="page.name == 'main'"/>
					<My v-if="page.name == 'mypage'"/>
					<Search v-if="page.name == 'sheetSearch'"/>
					<Card v-if="page.name == 'payment'"/>
					<Upload v-if="page.name == 'sheetUpload'"/>
					<NotePixel v-if="page.name == 'waitBattle'"/>
				</div>
				<span>{{ page.title }}</span>
			</RouterLink>
		</div>
		
		<div @click="goLogout" class="logout-button">
			<span>로그아웃</span>
		</div>
		</div>
	</nav>
</template>

<style scoped>
.left-nav-container {
	background-color: rgb(114, 160, 208);
	border-top-right-radius: 10px;
border-bottom-right-radius: 10px;
}

.nav-main {
	margin-left: 20px;
}

.nav-main > div > * {
	display: flex;
}
.logout-button {
  margin-top: 20px;
  padding: 10px;
  color: #fff;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

</style>