<script setup>
import ThemeSwitch from '@/common/ui/ThemeSwitch.vue'

import Home from '@/common/icons/Home.vue'
import My from '@/common/icons/My.vue'
import Search from '@/common/icons/Search.vue'
import Card from '@/common/icons/Card.vue'
import Upload from '@/common/icons/Upload.vue'
import NotePixel from '@/common/icons/NotePixel.vue'

import { ref } from 'vue';
import { useLeftNavigationStore } from '@/stores/leftNavigation'
import { storeToRefs } from "pinia";

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
</script>

<template>
	<nav class="left-nav-container">
		<br><br>
		<ThemeSwitch/>
		<br><br><br><br><br>

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
		
		<div>
			로그아웃
		</div>
		</div>
	</nav>
</template>

<style scoped>
.left-nav-container {
	background-color: rgba(82, 78, 78, 1);
	border-top-right-radius: 10px;
border-bottom-right-radius: 10px;
}

.nav-main {
	margin-left: 20px;
}

.nav-main > div > * {
	display: flex;
}

</style>