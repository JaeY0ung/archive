<script setup>
import { ref, computed } from "vue";

const activeRank = ref("single");

const singleRankings = ref([
    { rank: 1, name: "김철수", successes: 65, failures: 15 },
    { rank: 2, name: "이영희", successes: 60, failures: 20 },
    { rank: 3, name: "박민수", successes: 55, failures: 25 },
    { rank: 4, name: "최지훈", successes: 50, failures: 30 },
    { rank: 5, name: "정수현", successes: 45, failures: 35 },
    { rank: 6, name: "한가람", successes: 40, failures: 40 },
    { rank: 7, name: "오준호", successes: 35, failures: 45 },
]);

const multiRankings = ref([
    { rank: 1, name: "이슬기", wins: 70, losses: 10 },
    { rank: 2, name: "장미라", wins: 65, losses: 15 },
    { rank: 3, name: "윤상현", wins: 60, losses: 20 },
    { rank: 4, name: "배성우", wins: 55, losses: 25 },
    { rank: 5, name: "김나연", wins: 50, losses: 30 },
    { rank: 6, name: "서윤아", wins: 45, losses: 35 },
    { rank: 7, name: "고범준", wins: 40, losses: 40 },
]);

const sortedRankings = computed(() => {
    const rankingsToSort =
        activeRank.value === "single" ? singleRankings : multiRankings;
    return [...rankingsToSort.value].sort((a, b) => {
        const aSuccessRate = a.successes / (a.successes + a.failures);
        const bSuccessRate = b.successes / (b.successes + b.failures);
        return bSuccessRate - aSuccessRate;
    });
});

const getRate = (successes, failures) => {
    const total = successes + failures;
    return total > 0 ? ((successes / total) * 100).toFixed(1) : "0.0";
};

const getWinRate = (wins, losses) => {
    const total = wins + losses;
    return total > 0 ? ((wins / total) * 100).toFixed(1) : "0.0";
};
</script>

<template>
    <div
        class="w-full h-full min-w-[300px] flex flex-col bg-transparent rounded-2xl"
    >
        <div class="w-full flex justify-between mb-6">
            <button
                @click="activeRank = 'single'"
                :class="{
                    'bg-[#3498db] text-white': activeRank === 'single',
                    'bg-gray-200 text-[#3498db]': activeRank !== 'single',
                }"
                class="w-1/2 py-2 px-4 rounded-l-lg font-semibold transition duration-300 ease-in-out"
            >
                싱글 랭킹
            </button>
            <button
                @click="activeRank = 'multi'"
                :class="{
                    'bg-[#3498db] text-white': activeRank === 'multi',
                    'bg-gray-200 text-[#3498db]': activeRank !== 'multi',
                }"
                class="w-1/2 py-2 px-4 rounded-r-lg font-semibold transition duration-300 ease-in-out"
            >
                멀티 랭킹
            </button>
        </div>

        <div class="flex-grow overflow-y-auto pr-2 scrollbar-hide">
            <ul class="space-y-3">
                <li
                    v-for="(item, index) in sortedRankings"
                    :key="index"
                    class="bg-gray-100 rounded-lg p-4 shadow-sm transition duration-300 ease-in-out hover:bg-gray-200"
                >
                    <div class="flex items-center justify-between">
                        <div class="flex items-center space-x-3">
                            <span
                                :class="[
                                    'font-bold text-lg',
                                    index === 0
                                        ? 'text-[#3498db]'
                                        : 'text-gray-600',
                                ]"
                            >
                                {{ index + 1 }}
                                <span v-if="index === 0">🏆</span>
                            </span>
                            <span class="font-semibold text-gray-800">{{
                                item.name
                            }}</span>
                        </div>
                        <div class="text-right">
                            <p
                                v-if="activeRank === 'single'"
                                class="text-sm font-medium text-[#3498db]"
                            >
                                성공률:
                                {{ getRate(item.successes, item.failures) }}%
                            </p>
                            <p
                                v-else
                                class="text-sm font-medium text-[#3498db]"
                            >
                                승률: {{ getWinRate(item.wins, item.losses) }}%
                            </p>
                            <p
                                v-if="activeRank === 'single'"
                                class="text-xs text-gray-500"
                            >
                                {{ item.successes }} 성공
                                {{ item.failures }} 실패
                            </p>
                            <p v-else class="text-xs text-gray-500">
                                {{ item.wins }} 승리 {{ item.losses }} 패배
                            </p>
                        </div>
                    </div>
                    <div
                        class="mt-2 bg-gray-300 rounded-full h-2 overflow-hidden"
                    >
                        <div
                            class="bg-[#3498db] h-full rounded-full"
                            :style="{
                                width: `${
                                    activeRank === 'single'
                                        ? getRate(item.successes, item.failures)
                                        : getWinRate(item.wins, item.losses)
                                }%`,
                            }"
                        ></div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<style scoped>
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.bg-gray-100 {
    background-color: rgba(243, 244, 246, 0.7);
}

.hover\:bg-gray-200:hover {
    background-color: rgba(229, 231, 235, 0.8);
}
</style>
