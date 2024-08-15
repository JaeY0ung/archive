<script setup>
import { watch } from 'vue';
import { getTitleByLen } from '@/util/string-util';
import Tier from "@/common/icons/Tier.vue";
import { useRouter } from 'vue-router';

const router = useRouter();
const props = defineProps({
    sheet: Object,
    restrictTitle: {
        type: Boolean,
        default: true
    }
});

watch(() => props.sheet, () => {
    props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
});
props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');

const goToSinglePlay = () => {
    router.push({ name: 'singlePlay', params: { sheetId: props.sheet.id } });
}
</script>

<template>
    <div class="h-full max-w-[200px] w-[20vw] flex flex-col">
        <div class="w-full p-3">
            <img class="w-[200px] h-[200px]" :src="sheet?.imageUrl" alt="악보 이미지">
        </div>

        <div class="p-4">
            <div class="flex flex-row mb-2 justify-center gap-3 items-center">
                <div class="font-bold text-lg">
                    {{ restrictTitle ? getTitleByLen(sheet?.title, 12) : sheet?.title }}
                </div>
                <Tier :level="sheet?.level" class="w-6 h-6" />
            </div>

            <div class="text-sm text-gray-600">
                아티스트: {{ restrictTitle ? getTitleByLen(sheet?.songComposer, 12) : sheet?.songComposer }}
            </div>

            <div class="text-sm text-gray-600 mt-1">
                가격: {{ sheet.price ? sheet.price  + '원' : '무료'}}
            </div>
            <div class="btn btn-primary" @click="goToSinglePlay">연습하기</div>
        </div>
    </div>
</template>

<style scoped>
</style>
