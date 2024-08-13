<script setup>
import { watch } from "vue";
import { getTitleByLen } from "@/util/string-util";
import { useRouter, useRoute } from "vue-router";
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const route = useRoute();

const props = defineProps({
    sheet: Object,
    restrictTitle: {
        type: Boolean,
        default: true,
    },
});

watch(
    () => props.sheet,
    () => {
        props.sheet.imageUrl = props.sheet.songImg
            ? `data:image/jpeg;base64,${props.sheet.songImg}`
            : require("@/assets/img/default/song_img.png");
    }
);
props.sheet.imageUrl = props.sheet.songImg
    ? `data:image/jpeg;base64,${props.sheet.songImg}`
    : require("@/assets/img/default/song_img.png");

const formatTitle = (title) => {
    if (title.length <= 6) {
        return title;
    } else if (title.length <= 12) {
        const splitIndex = title.substring(0, 7).lastIndexOf(" ");
        if (splitIndex > 0) {
            return title.slice(0, splitIndex) + "\n" + title.slice(splitIndex + 1);
        } else {
            return title.slice(0, 6) + "\n" + title.slice(6);
        }
    } else {
        const truncated = title.substring(0, 12);
        const lastSpaceIndex = truncated.lastIndexOf(" ");
        if (lastSpaceIndex > 0) {
            return title.slice(0, lastSpaceIndex) + "...";
        } else {
            return truncated.slice(0, 11) + "...";
        }
    }
};
</script>

<template>
    <div
        class="w-[330px] h-[90px] m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg overflow-hidden"
        style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3)"
    >
        <div class="h-full flex justify-start gap-3">
            <!-- (왼쪽) 악보 사진 -->
            <div class="h-[80px] w-[80px] flex-shrink-0 flex justify-center">
                <img class="rounded-lg object-cover" :src="sheet.imageUrl" alt="원본 곡 이미지" />
            </div>

            <!-- (중앙) 악보 정보 -->
            <div class="min-w-0 flex-grow pt-[5px] flex flex-col gap-1">
                <div class="bold flex justify-between items-start" style="font-size: 16px">
                    <div class="custom-title mr-2">
                        {{ formatTitle(sheet.title) }}
                    </div>
                    <Tier class="w-[16px] h-[16px] flex-shrink-0" :level="sheet.level" />
                </div>
                <div class="medium mb-1 truncate" style="font-size: 12px">
                    {{ sheet.songComposer }}
                </div>
                <div class="medium flex items-center gap-1 truncate" style="font-size: 12px">
                    업로더 {{ sheet.uploaderNickname }}
                </div>
            </div>
        </div>

        <!-- (오른쪽) 추가 기능 -->
        <div class="flex flex-col items-center justify-center">
            <slot />
            <div
                v-if="sheet.singleScore !== undefined"
                :class="[
                    'p-1 rounded-full flex items-center justify-center',
                    sheet.singleScore > 80 ? 'bg-green-100' : 'bg-red-100',
                ]"
            >
                <span
                    :class="[
                        'font-bold text-sm',
                        sheet.singleScore > 80 ? 'text-green-600' : 'text-red-600',
                    ]"
                >
                    {{ sheet.singleScore > 80 ? "GOOD" : "BAD" }}
                </span>
            </div>
        </div>
    </div>
</template>

<style scoped>
.truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.bold {
    font-weight: bold;
}

.medium {
    font-weight: 500;
}
</style>
