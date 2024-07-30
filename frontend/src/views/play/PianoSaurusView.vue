<script setup>

const pages = ref([
  { name: 'sheetUpload', title: '혼자 연습하기 방' },
  { name: 'pianoSaurus', title: '새로운 방 만들기 방' },
])

</script>

<template>
   <div>pianoSaurusView</div>
   <div v-for="page in pages" :key="page.name">
			<RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav">
				<div style="width: 25%;">
					<Home v-if="page.name == 'main'"/>
					<My v-if="page.name == 'mypage'"/>
					<Search v-if="page.name == 'sheetSearch'"/>
					<Card v-if="page.name == 'payment'"/>
					<Upload v-if="page.name == 'sheetUpload'"/>
					<NotePixel v-if="page.name == 'pianoSaurus'"/>
				</div>
				<span>{{ page.title }}</span>
			</RouterLink>
		</div>
		

</template>


<style scoped>
</style>