<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import Tier from "@/common/icons/Tier.vue";

const router = useRouter();
const showScore = ref(false);

const props = defineProps({
    sheet: Object,
});

const isSuccess = computed(() => props.sheet.singleScore >= 80);
const textColor = computed(() => (isSuccess.value ? "text-green-500" : "text-red-500"));

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

const navigateToDetail = () => {
    router.push({ name: "sheetDetail", params: { sheetId: props.sheet.id } });
};
</script>

<template>
    <div
        class="w-[330px] h-[90px] m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-lg overflow-hidden cursor-pointer"
        style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3)"
        @click="navigateToDetail"
    >
        <div class="flex justify-start gap-3">
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

        <!-- (오른쪽) 점수 및 추가 기능 -->
        <div class="p-[5px] flex flex-col items-end justify-center gap-2 flex-shrink-0">
            <slot />
            <div
                v-if="sheet.singleScore !== undefined"
                class="relative"
                @mouseenter="showScore = true"
                @mouseleave="showScore = false"
            >
                <div
                    :class="[
                        'p-2 rounded-full flex items-center justify-center',
                        isSuccess ? 'bg-green-100' : 'bg-red-100',
                    ]"
                >
                    <span 
                        :class="['font-bold text-lg', textColor]"
                        class="w-[52px] text-center"
                    >
                        {{ showScore ? sheet.singleScore : (isSuccess ? "GOOD" : "BAD") }}
                    </span>
                </div>
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

.custom-title {
    white-space: pre-line;
    word-break: break-word;
    max-width: 100%;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
</style>
