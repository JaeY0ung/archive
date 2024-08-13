<script setup>
import { watch } from 'vue';
import { getTitleByLen } from '@/util/string-util';
import Tier from "@/common/icons/Tier.vue";

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
</script>

<template>
    <div class="flex flex-col items-center bg-white rounded-lg shadow-lg overflow-hidden">
        <!-- 큰 악보 사진 -->
        <div class="w-full h-[300px] overflow-hidden">
            <img class="w-full h-full object-cover" :src="sheet.imageUrl" alt="악보 이미지">
        </div>

        <!-- 악보 정보 -->
        <div class="p-4 w-full">
            <div class="flex justify-between items-center mb-2">
                <!-- 악보 제목 -->
                <div class="font-bold text-lg truncate" :title="sheet.title">
                    {{ restrictTitle ? getTitleByLen(sheet.title, 20) : sheet.title }}
                </div>
                <!-- 티어 정보 -->
                <Tier :level="sheet.level" class="w-6 h-6" />
            </div>
            <!-- 작곡가 및 업로더 정보 -->
            <div class="text-sm text-gray-600">
                작곡가: {{ getTitleByLen(sheet.songComposer, 20) }}
            </div>
            <div class="text-sm text-gray-600">
                업로더: {{ sheet.uploaderNickname }}
            </div>
            <!-- 가격 정보 -->
            <div v-if="sheet.price" class="text-sm text-gray-600 mt-1">
                가격: {{ sheet.price }}원
            </div>
        </div>
    </div>
</template>

<style scoped>
.flex {
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.object-cover {
    object-fit: cover;
}
.overflow-hidden {
    overflow: hidden;
}
.shadow-lg {
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
