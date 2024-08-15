<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const keyword = ref('');

const moveToSheetSearchPageWithKeyword = () => {
	router.push({ name: 'sheetSearch', query: { keyword: keyword.value } });
}

// 초기 페이지 이동 시, 쿼리 파라미터로 전달된 키워드 값을 실제 keyword로 전달
onMounted(() => {
	if (route.query.keyword) {
		keyword.value = route.query.keyword;
		moveToSheetSearchPageWithKeyword();
	}
})


</script>

<template>
	<div>
		<label class="input input-bordered flex items-center gap-2 rounded-badge custom-border" style="color: #3498db;">
			<input v-model="keyword" @keyup.enter="moveToSheetSearchPageWithKeyword" type="text" class="grow "
				placeholder="악보 검색" />
			<svg @click="moveToSheetSearchPageWithKeyword" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16"
				fill="currentColor" class="h-4 w-4 opacity-70 cursor-pointer">
				<path fill-rule="evenodd"
					d="M9.965 11.026a5 5 0 1 1 1.06-1.06l2.755 2.754a.75.75 0 1 1-1.06 1.06l-2.755-2.754ZM10.5 7a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0Z"
					clip-rule="evenodd" />
			</svg>
		</label>
	</div>
</template>

<style scoped>
.custom-border {
  border: 2px solid #3498db; /* 원하는 border 색상으로 변경 */
}
.input:focus {
  border-color: #3498db; /* 입력 포커스 시 테두리 색상 유지 */
  outline: none; /* 기본 아웃라인 제거 */
}

</style>