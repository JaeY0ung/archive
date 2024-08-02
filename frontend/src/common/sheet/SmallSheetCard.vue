<script setup>
import { getTitleByLen } from '@/util/string-util'
import { useRouter } from 'vue-router';
import Tier from "@/common/icons/Tier.vue"

const router = useRouter();

const props = defineProps({
    sheet: Object
})

const goToSheetDetail = () => {
    router.push({ name: 'sheetDetail', params: { sheetId: props.sheet.id } });
}

</script>

<template>
    <div class="m-[5px] p-[5px] flex flex-row justify-start gap-3 bg-white rounded-lg" style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);" @click="goToSheetDetail">
        <!-- (왼쪽) 악보 사진 -->
        <div class="h-[80px] w-[80px] flex justify-center">
            <img class="rounded-lg" :src="sheet.imageUrl" alt="원본 곡 이미지">
        </div>
        <!-- (오른쪽) 악보 정보 -->
        <div class="min-w-[160px]">
            <div>
                <div class="bold flex justify-between" style="font-size: 18px;">
                    <div class="flex items-center">{{ getTitleByLen(sheet.title, 10) }}</div>
                    <div class="flex items-center">
                        <Tier class="w-[18px] h-[18px]" :level="sheet.level"/>
                    </div>
                </div>
                <div class="medium" style="font-size: 12px;">{{ sheet.songComposer }}</div>
            </div>
            <div class="pt-2">
                <div class="medium" style="font-size: 12px;">업로더 {{ sheet.uploaderNickname }}</div>
            </div>
        </div>
    </div>
</template>

<style scoped>
</style>