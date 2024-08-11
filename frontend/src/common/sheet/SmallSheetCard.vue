<script setup>
import { watch } from 'vue'
import { getTitleByLen } from '@/util/string-util';
import { useRouter, useRoute } from 'vue-router';
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const route = useRoute();

const props = defineProps({
	sheet: Object,
	restrictTitle: {
		type: Boolean,
		default: true
	}
});

watch(() => props.sheet, () => {
	props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
})
props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');

</script>

<template>
	<div class="h-[80px] m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg "
		style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">

		<div class="h-full flex justify-start gap-3">
			<!-- (왼쪽) 악보 사진 -->
			<div class="h-full w-[70px] flex justify-center">
				<img class="rounded-lg" :src="sheet.imageUrl" alt="원본 곡 이미지">
			</div>

			<!-- (오른쪽) 악보 정보 -->
			<div class="min-w-[160px] m-auto flex flex-col gap-1">
				<div class="bold flex justify-between" style="font-size: 18px;">
					<div class="flex items-center truncate w-[12rem]">
						{{ restrictTitle? getTitleByLen(sheet.title, 12) : sheet.title  }}
					</div>
					<div class="flex items-center">
						<Tier class="w-[18px] h-[18px]" :level="sheet.level" />
					</div>
				</div>
				<div class="medium mb-3" style="font-size: 12px;">{{ getTitleByLen(sheet.songComposer,20) }}</div>
				<div class="medium flex items-center gap-1" style="font-size: 12px;">
					업로더 {{ sheet.uploaderNickname }}
					<span class="ml-5" v-if="route.name === 'order'"> {{ sheet.price }}원</span>
				</div>
			</div>

			<div class="m-auto flex flex-col items-center justify-center">
				<slot class="h-full"/>
			</div>
		</div>
	</div>
</template>

<style scoped></style>
