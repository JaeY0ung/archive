<script setup>
import {localAxios} from "@/util/http-common";
import {onMounted, ref, watch} from "vue";
import {useRoute} from "vue-router";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";

const route = useRoute();
const local = localAxios();
const sheets = ref([]);
const keyword = ref('');

const searchSheetsByKeyword =  ()  => {
  console.log(keyword);
  local.get(`/sheets`, {
	params: { keyword: keyword.value }
  })
	  .then(({data}) => {
		sheets.value = data;
		sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		console.log(data);
	  })
	  .catch((err) => alert(err));
};

watch(() => route.query.keyword,(newValue) => {
  keyword.value = newValue || '';
  searchSheetsByKeyword();
})
onMounted(() => {
  keyword.value = route.query.keyword;
  searchSheetsByKeyword();
})
</script>

<template>

  <div>
    <h2>Search Results for </h2>
    <div class="w-1/2 m-auto">
	  <template v-if="sheets.length">
		<div class="scroll-y flex-col">
		  <SmallSheetCard v-for="sheet in sheets" :key="sheet.id" :sheet/>
		</div>
	  </template>
	  <template v-else>
		<p>No results found.</p>
	  </template>
	</div>
  </div>

</template>

<style scoped>
.container {
  display: flex;
  height: 100vh; /* 화면 전체 높이로 설정 */
  margin: 10px 10px;
  gap: 20px; /* 패널 간의 간격 설정 (예: 20px) */
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

.left-panel, .right-panel {
  padding: 20px;
}
.left-panel {
  flex: 2; /* 왼쪽 패널은 전체의 2/10 */
  background-color: #f0f0f0; /* 왼쪽 패널 배경색 */
}
.right-panel {
  flex: 8; /* 오른쪽 패널은 전체의 8/10 */
  background-color: #ffffff; /* 오른쪽 패널 배경색 */
}
</style>