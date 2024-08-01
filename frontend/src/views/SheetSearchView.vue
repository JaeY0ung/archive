<script setup>
import { localAxios } from "@/util/http-common";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";

const route = useRoute();
const local = localAxios();
const sheets = ref([]);
const keyword = ref('');
const scrollMode = ref('scroll-y');

// TODO: 검색 필터에서 값을 여러 개 받을 수 있으므로 수정해야할듯.
const selectedTiers = ref([]);

const searchSheetsByKeyword = () => {
  console.log(keyword.value);
  local.get(`/sheets`, {
	params: { keyword: keyword.value }
  })
	  .then(({ data }) => {
		sheets.value = data;
		sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		console.log(data);
	  })
	  .catch((err) => alert(err));
};

const handleSelectChange = (event) => {
  const view = event.target.value;
  console.log(view);
  local.get(`/sheets`, {
	params: { keyword: keyword.value, sort: view }
  })
	  .then(({ data }) => {
		sheets.value = data;
		sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		console.log(data);
	  })
	  .catch((err) => alert(err));
}

const handleViewChange = (event) => {
  const view = event.target.value;
  if (view === 'card') {
	scrollMode.value = 'scroll-x';
  } else {
	scrollMode.value = 'scroll-y';
  }
  console.log(keyword.value);
  local.get(`/sheets`, {
	params: { keyword: keyword.value }
  })
	  .then(({ data }) => {
		sheets.value = data;
		sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		console.log(data);
	  })
	  .catch((err) => alert(err));
}

// 선택된 티어를 기반으로 요청을 생성
const searchSheetsByFiltering = () => {
  console.log(selectedTiers.value)
  console.log(selectedTiers.value.join(','));
  local.get('/sheets', {
	params: { level: selectedTiers.value.join(',')} // 배열을 문자열로 변환
  })
	  .then(({ data }) => {
		sheets.value = data;
		sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
		console.log(data);
	  })
	  .catch(err => alert(err));
};

// 티어 필터의 변화를 감지하고 필터링 함수 호출
watch(selectedTiers, () => {
  // console.log(selectedTiers);
  searchSheetsByFiltering();
});

watch(() => route.query.keyword, (newValue) => {
  keyword.value = newValue || '';
  searchSheetsByKeyword();
})
onMounted(() => {
  keyword.value = route.query.keyword;
  searchSheetsByKeyword();
})
</script>

<template>
  <div class="container">
	<div class="top-bar">
	  <div class="top-bar-left">
		<p><span class="highlight">{{ keyword }}</span> 악보 ( {{ sheets.length }}개의 결과 )</p>
	  </div>
	  <div class="top-bar-right">
		<select @change="handleSelectChange">
		  <option value="accuracy">정확도순</option>
		  <option value="LATEST">최신순</option>
		  <option value="POPULAR">인기순</option>
		  <option value="play">플레이순</option>
		  <option value="starRating">별점순</option>
		  <option value="level">난이도순</option>
		</select>
		<select @change="handleViewChange">
		  <option value="list">리스트</option>
		  <option value="card">카드</option>
		</select>
	  </div>
	</div>

	<div class="panel-container">
	  <div class="left-panel">
		<span class="text-filter highlight">검색 필터</span>
		<div class="filter-group">
		  <div class="filter-item">
			<span class="filter-category highlight">티어</span>
			<hr class="filter-divider">
			<div class="filter-value">
			  <label for="bronze">브론즈</label>
			  <input type="checkbox" id="bronze" value="1" v-model="selectedTiers">
			</div>
			<div class="filter-value">
			  <label for="silver">실버</label>
			  <input type="checkbox" id="silver" value="2" v-model="selectedTiers">
			</div>
			<div class="filter-value">
			  <label for="gold">골드</label>
			  <input type="checkbox" id="gold" value="3" v-model="selectedTiers">
			</div>
			<div class="filter-value">
			  <label for="platinum">플레티넘</label>
			  <input type="checkbox" id="platinum" value="4" v-model="selectedTiers">
			</div>
			<div class="filter-value">
			  <label for="diamond">다이아</label>
			  <input type="checkbox" id="diamond" value="5" v-model="selectedTiers">
			</div>
		  </div>
		  <div class="filter-item">
			<span class="filter-category highlight">무료/유료</span>
			<hr class="filter-divider">
			<div class="filter-value">
			  <label for="free">무료</label>
			  <input type="checkbox" id="free">
			</div>
			<div class="filter-value">
			  <label for="paid">유료</label>
			  <input type="checkbox" id="paid">
			</div>
		  </div>
		  <div class="filter-item">
			<span class="filter-category highlight">장르</span>
			<hr class="filter-divider">
			<div class="filter-value">
			  <label for="ost">OST</label>
			  <input type="checkbox" id="ost">
			</div>
			<div class="filter-value">
			  <label for="classical">클래식</label>
			  <input type="checkbox" id="classical">
			</div>
			<div class="filter-value">
			  <label for="jazz">재즈</label>
			  <input type="checkbox" id="jazz">
			</div>
			<div class="filter-value">
			  <label for="pop">가요</label>
			  <input type="checkbox" id="pop">
			</div>
			<div class="filter-value">
			  <label for="hiphop">힙합</label>
			  <input type="checkbox" id="hiphop">
			</div>
			<div class="filter-value">
			  <label for="etc">기타</label>
			  <input type="checkbox" id="etc">
			</div>
		  </div>
		  <div class="filter-item">
			<span class="filter-category highlight">성공 여부:</span>
			<hr class="filter-divider">

			<div class="filter-value">
			  <label for="success">성공</label>
			  <input type="checkbox" id="success">
			</div>
			<div class="filter-value">
			  <label for="fail">실패</label>
			  <input type="checkbox" id="fail">
			</div>
		  </div>
		</div>
	  </div>
	  <div class="right-panel">
		<h2>Search Results for "{{ keyword }}"</h2>
		<div :class="[scrollMode]">
		  <template v-if="sheets.length">
			<div class="scroll-y flex-col">
			  <SmallSheetCard v-for="sheet in sheets" :key="sheet.id" :sheet="sheet"/>
			</div>
		  </template>
		  <template v-else>
			<p>No results found.</p>
		  </template>
		</div>
	  </div>
	</div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-sizing: border-box;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #e0e0e0;
  box-sizing: border-box;
}

.top-bar-left {
  flex: 1;
  text-align: left;
}

.top-bar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.panel-container {
  display: flex;
  flex: 1;
  gap: 20px;
  padding: 20px;
  box-sizing: border-box;
}

.left-panel, .right-panel {
  padding: 20px;
}

.left-panel {
  flex: 2;
  background-color: #f0f0f0;
}

.right-panel {
  flex: 8;
  background-color: #ffffff;
}

.text-filter {
  font-size: 1.5em; /* Larger font size for the "검색 필터" text */
  font-weight: bold;
}

.highlight {
  color: #8A8ECD;
}

.filter-group {
  margin-top: 10px;
}

.filter-item {
  margin-bottom: 16px; /* Increased margin for better spacing */
}

.filter-category {
  font-size: 1.2em; /* Larger font size for categories */
  font-weight: bold;
}

.filter-value {
  display: flex;
  align-items: center;
  margin-left: 10px;
}

.filter-divider {
  margin: 10px 0;
  border: 0;
  border-bottom: 1px solid #ccc;
}

.scroll-y {
  overflow-y: auto;
  height: calc(100vh - 200px);
}

.scroll-x {
  display: flex;
  overflow-x: auto;
  height: calc(100vh - 200px);
}

.scroll-x > .flex-col {
  flex-direction: row;
}
</style>
