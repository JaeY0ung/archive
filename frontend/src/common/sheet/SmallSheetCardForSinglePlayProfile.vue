<script setup>
import { computed } from "vue";
import { getTitleByLen } from "@/util/string-util";
import Tier from "@/common/icons/Tier.vue";

const props = defineProps({
    sheet: Object,
});

const isSuccess = computed(() => props.sheet.singleScore >= 80);
const textColor = computed(() =>
    isSuccess.value ? "text-green-500" : "text-red-500"
);

props.sheet.imageUrl = props.sheet.songImg
    ? `data:image/jpeg;base64,${props.sheet.songImg}`
    : require("@/assets/img/default/song_img.png");
</script>

<template>
    <div
        class="m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg"
        style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3)"
    >
        <div class="flex justify-start gap-3">
            <!-- (왼쪽) 악보 사진 -->
            <div class="h-[80px] w-[80px] flex justify-center">
                <img
                    class="rounded-lg"
                    :src="sheet.imageUrl"
                    alt="원본 곡 이미지"
                />
            </div>

            <!-- (중앙) 악보 정보 -->
            <div class="min-w-[110px] pt-[5px] flex flex-col gap-1">
                <div class="bold flex justify-between" style="font-size: 18px">
                    <div class="flex items-center">
                        {{ getTitleByLen(sheet.title, 10) }}
                    </div>
                    <div class="flex items-center">
                        <Tier class="w-[18px] h-[18px]" :level="sheet.level" />
                    </div>
                </div>
                <div class="medium mb-3" style="font-size: 12px">
                    {{ sheet.songComposer }}
                </div>
                <div
                    class="medium flex items-center gap-1"
                    style="font-size: 12px"
                >
                    업로더 {{ sheet.uploaderNickname }}
                </div>
            </div>
        </div>

        <!-- (오른쪽) 점수 및 추가 기능 -->
        <div class="p-[5px] flex flex-col items-end justify-center gap-2">
            <slot />
            <div
                v-if="sheet.singleScore !== undefined"
                :class="[
                    'p-2 rounded-full flex items-center justify-center',
                    isSuccess ? 'bg-green-100' : 'bg-red-100',
                ]"
            >
                <span :class="['font-bold text-2xl', textColor]">
                    {{ sheet.singleScore }}
                </span>
            </div>
        </div>
    </div>
</template>

<style scoped></style>