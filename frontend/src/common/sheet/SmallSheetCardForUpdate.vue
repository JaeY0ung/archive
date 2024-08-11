<script setup>
import { ref, watch } from 'vue'
import { getTitleByLen } from '@/util/string-util';
import Tier from "@/common/icons/Tier.vue";
import { tierInfo } from '@/util/tier-info';
import { updateSheet } from '@/api/sheet';

const emit = defineEmits(['update-sheet-event'])

const props = defineProps({
	sheet: Object
});

const updateForm = ref({
	title: props.sheet.title,
	level: props.sheet.level,
})

const updateSheetById = () => {
	updateSheet(
		props.sheet.id,
		updateForm.value,
		(res) => {
			emit('update-sheet-event', props.sheet.id)
		}
	)
}

watch(() => props.sheet, () => {
	props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
})
props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
</script>

<template>
	<div class="h-[80px] m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg max-w-[400px]"
		style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">

		<div class="h-full flex justify-start gap-3">
			<!-- (왼쪽) 악보 사진 -->
			<div class="h-full w-[70px] flex justify-center">
				<img class="rounded-lg" :src="sheet.imageUrl" alt="원본 곡 이미지">
			</div>

			<!-- (오른쪽) 악보 정보 -->
			<div class="min-w-[160px] m-auto flex flex-col gap-1">
				<div class="bold flex justify-between gap-3" style="font-size: 18px;">
					<!-- 제목 -->
					<div class="flex items-center w-32">
						<input type="text" v-model="updateForm.title" class="w-full"/>
					</div>
					<!-- 티어 -->
					<div class="flex items-center">
						<Tier class="w-[18px] h-[18px]" :level="updateForm.level" />
						<select v-model="updateForm.level">
							<option v-for="tier in tierInfo" :value="tier.level">{{ tier.title }}</option>
						</select>
					</div>
				</div>
				<div class="medium mb-3" style="font-size: 12px;">
					{{ getTitleByLen(sheet.songComposer,20) }}
				</div>
				<div class="medium flex items-center gap-1" style="font-size: 12px;">
					업로더 {{ sheet.uploaderNickname }}
					<span class="ml-5">{{ sheet.price }}원</span>
				</div>
			</div>

			<div class="m-auto flex flex-col items-center justify-center">
				<div class="h-full flex flex-col items-center gap-1">
					<div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updateSheetById">완료</div>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped></style>
