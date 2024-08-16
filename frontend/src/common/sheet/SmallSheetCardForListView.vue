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
	<div class="h-[100px] m-[5px] p-[5px] pt-2 pb-2 flex flex-row justify-between gap-3 bg-white rounded-lg "
		style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">

		<div class="h-full w-full flex justify-start gap-3">
			<!-- (왼쪽) 악보 사진 -->
			<div class="h-full w-[70px] flex justify-center">
				<img class="rounded-lg" :src="sheet.imageUrl" alt="원본 곡 이미지">
			</div>

			<!-- (오른쪽) 악보 정보 -->
			<div class="flex justify-between w-full items-center">
				 <!-- 제목 및 티어 -->
				 <div class="flex flex-row items-center gap-2">
                    <div class="font-bold text-lg truncate">
                        {{ restrictTitle ? getTitleByLen(sheet.title, 12) : sheet.title }}
                    </div>
                    <Tier class="w-[18px] h-[18px]" :level="sheet.level" />
                </div>

                <!-- 두 번째 그룹: 아티스트 및 업로더 정보 -->
                <div class="flex items-center gap-4 ml-auto">
					<div class="text-gray-800 text-sm font-bold">아티스트</div>
                    <div class="text-gray-600 text-sm">
                        {{ getTitleByLen(sheet.songComposer, 20) }}
                    </div>
					<div class="text-gray-800 text-sm font-bold">업로더</div>
                    <div class="text-gray-500 text-sm">
                    	{{ sheet.uploaderNickname }}
                        <span v-if="route.name === 'order'"> {{ sheet.price }}원</span>
                    </div>
                </div>
			</div>

			<div class="m-auto flex flex-col items-center justify-center">
				<slot class="h-full"/>
			</div> 
		</div>
	</div>
</template>

<style scoped></style>
