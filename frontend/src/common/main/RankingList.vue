<script setup>
import { ref } from 'vue';

const isSingleRank = ref(false);

const rankings = ref([
	{ rank: 1, name: 'User1', wins: 60, losses: 20 },
	{ rank: 2, name: 'User2', wins: 55, losses: 25 },
	{ rank: 3, name: 'User3', wins: 50, losses: 30 },
	{ rank: 4, name: 'User4', wins: 45, losses: 35 },
    { rank: 5, name: 'User5', wins: 40, losses: 40 },
    { rank: 6, name: 'User6', wins: 40, losses: 40 },
    { rank: 7, name: 'User7', wins: 35, losses: 40 },
]);
</script>

<template>
    <div class="h-full min-w-[220px] flex flex-col justify-center bg-white bg-opacity-50 rounded-2xl p-3 m-1 ">
				
        <div class="w-full flex justify-between mb-2">
            <button @click="isSingleRank = true"
                :class="{ 'bg-purple-500 text-white': isSingleRank, 'bg-gray-200 text-gray-600': !isSingleRank }"
                class="w-1/2 p-2 rounded-l-lg">
                싱글 랭킹
            </button>
            <button @click="isSingleRank = false" :class="{ 'bg-purple-500 text-white': !isSingleRank, 'bg-gray-200 text-gray-600': isSingleRank }"
                class="w-1/2 p-2 rounded-r-lg">
                멀티 랭킹
            </button>
        </div>

        <div class="w-full flex flex-col flex-grow overflow-hidden">
            <template v-if="isSingleRank">
                <div class="flex-grow overflow-y-auto scroll-y">
                    <ul class="space-y-2">
                        <li v-for="(item, index) in rankings" :key="index"
                            class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
                            {{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
                        </li>
                    </ul>
                </div>
            </template>
            <template v-else>
                <div class="h-full flex-grow overflow-y-auto scroll-y">
                    <ul class="space-y-2">
                        <li v-for="(item, index) in rankings" :key="index"
                            class="bg-white bg-opacity-70 rounded-lg p-4 text-sm font-bold shadow-sm">
                            {{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
                        </li>
                    </ul>
                </div>
            </template>
        </div>
    </div>
</template>

<style scoped>

</style>