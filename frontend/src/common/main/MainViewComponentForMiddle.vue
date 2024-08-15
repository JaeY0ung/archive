<script setup>
import "vue3-carousel/dist/carousel.css";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import RecentPlaySheetCard from "@/common/sheet/RecentPlaySheetCard.vue";

defineProps({
    sheets: Array,
    title: String,
    recentPlayedSheet : Object,
});
const emit = defineEmits(["goToSheetDetail", "goToSheetSearchListView"]);
</script>

<template>
    <div
        class="container w-full h-full flex flex-col p-4 bg-white/50 rounded-2xl"
    >
        <div class="w-full flex flex-col items-center mb-2">
            <RecentPlaySheetCard :sheet="recentPlayedSheet" class="mb-2" />
            <div class="w-full text-right">
                <div class="text-l p-1 font-bold">{{ title }}</div>
            </div>
        </div>

        <div class="w-full h-full flex relative overflow-y-auto scrollbar-hide">
            <div class="w-full flex flex-col absolute gap-2">
                <template v-if="sheets">
                    <template v-for="sheet in sheets" :key="sheet.id">
                        <SmallSheetCard
                            :sheet="sheet"
                            @click="emit('goToSheetDetail', sheet.id)"
                        />
                    </template>
                </template>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    border: 1px solid #c9deff;
    border-radius: 12px;
    padding: 30px 28px;
    box-sizing: border-box;
    box-shadow: 0px 0px 6px 1px rgba(63, 128, 234, 0.2);
}

.bold {
    font-weight: bold;
}

.scrollbar-hide {
    -ms-overflow-style: none; /* IE and Edge */
    scrollbar-width: none; /* Firefox */
}

.scrollbar-hide::-webkit-scrollbar {
    display: none; /* Chrome, Safari and Opera */
}

.more-button {
    cursor: pointer;
}
</style>
