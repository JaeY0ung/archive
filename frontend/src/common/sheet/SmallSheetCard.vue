<script setup>
import { getTitleByLen } from '@/util/string-util';
import { useRouter, useRoute } from 'vue-router';
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const route = useRoute();

const props = defineProps({
  sheet: Object
});

props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');

const goToSheetDetail = () => {
  router.push({ name: 'sheetDetail', params: { sheetId: props.sheet.id } });
};
</script>

<template>
  <div class="m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg"
	   style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">

	<div class="flex justify-start gap-3" @click="goToSheetDetail">
	  <!-- (왼쪽) 악보 사진 -->
	  <div class="h-[80px] w-[80px] flex justify-center">
		<img class="rounded-lg" :src="sheet.imageUrl" alt="원본 곡 이미지">
	  </div>

	  <!-- (오른쪽) 악보 정보 -->
	  <div class="min-w-[160px] pt-[5px] flex flex-col gap-1">
		<div class="bold flex justify-between" style="font-size: 18px;">
		  <div class="flex items-center">{{ getTitleByLen(sheet.title, 10) }}</div>
		  <div class="flex items-center">
			<Tier class="w-[18px] h-[18px]" :level="sheet.level"/>
		  </div>
		</div>
		<div class="medium mb-3" style="font-size: 12px;">{{ sheet.songComposer }}</div>
		<div class="medium flex items-center gap-1" style="font-size: 12px;">
		  업로더 {{ sheet.uploaderNickname }}
		  <span class="ml-5" v-if="route.name === 'order'"> {{ sheet.price }}원</span>
		</div>
	  </div>
	</div>
	<div class="h-full flex flex-col items-center justify-center">
	  <slot/>
	</div>
  </div>
</template>

<style scoped>
</style>
