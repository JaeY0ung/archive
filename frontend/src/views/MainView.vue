<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import {
    searchSheetsByFilter,
    getRecommendSheetByUserRecentPlay,
    searchRecentPlayedsheet,
} from "@/api/sheet";
import MainViewComponent from "@/common/main/MainViewComponent.vue";
import MainViewComponentForMiddle from "@/common/main/MainViewComponentForMiddle.vue";
import RankingList from "@/common/main/RankingList.vue";
import RecentPlaySheetCard from "@/common/sheet/RecentPlaySheetCard.vue";
import MainSwipeComponent from "@/common/main/MainSwipeComponent.vue";

const router = useRouter();
const recentPlayedSheet = ref();
const popularSheets = ref([]); // 인기 악보 리스트
const newSheets = ref([]); // 새로 나온(New) 악보 리스트
const recommendSheets = ref([]); // 추천 악보 리스트
const randomSheets = ref([]);

const getPopularsheets = async () => {
    await searchSheetsByFilter({ sort: "POPULAR" }, ({ data }) => {
        if (!data) return;
        popularSheets.value = data;
    });
    console.log(popularSheets.value);
};

const getNewSheets = async () => {
    await searchSheetsByFilter({ sort: "LATEST" }, ({ data }) => {
        if (!data) return;
        newSheets.value = data;
    });
    console.log(newSheets.value);
};

const getRandomSheets = async () => {
    await searchSheetsByFilter({ sort: "RANDOM" }, ({ data }) => {
        if (!data) return;
        randomSheets.value = data;
    });
    console.log(randomSheets.value);
};

const getRecommendSheets = async () => {
    await searchSheetsByFilter({ sort: "LATEST" }, ({ data }) => {
        if (!data) return;
        recommendSheets.value = data;
    });
    console.log(recommendSheets.value);
    // await getRecommendSheetByUserRecentPlay(
    // 	(res) => {
    // 		if (res && res.data) {
    // 			recommendSheets.value = res.data; // 객체 내 data에 접근해서 할당
    // 			return
    // 		}
    // 		console.error('No valid data structure returned from searchSheetRecommand');
    // 	}
    // )
};

searchRecentPlayedsheet(({ data }) => {
    console.log("받은 최근 악보", data);
    recentPlayedSheet.value = data;
});

getPopularsheets();
getNewSheets();
getRecommendSheets();
getRandomSheets();

const goToSheetDetail = (sheetId) => {
    router.push({ name: "sheetDetail", params: { sheetId } });
};

const goToSheetSearchListView = (sort) => {
    router.push({ name: "sheetSearch", query: { sort } });
};
</script>

<template>
    <div class="w-full h-full grid grid-cols-3 gap-4">
        <!-- Left column -->
        <div class="flex flex-col gap-4">
            <MainViewComponent
                :sheets="popularSheets"
                :title="'인기'"
                class="flex-1"
                @goToSheetDetail="goToSheetDetail"
                @goToSheetSearchListView="goToSheetSearchListView('POPULAR')"
            />
            <MainViewComponent
                :sheets="newSheets"
                :title="'최신'"
                class="flex-1"
                @goToSheetDetail="goToSheetDetail"
                @goToSheetSearchListView="goToSheetSearchListView('LATEST')"
            />
        </div>

        <!-- Middle column -->
        <div class="flex flex-col gap-2">
        <MainViewComponentForMiddle
                :recentPlayedSheet="recentPlayedSheet"
                :sheets="randomSheets"
                :title="'추천 리스트'"
                class="flex-1"
                @goToSheetDetail="goToSheetDetail"
                @goToSheetSearchListView="goToSheetSearchListView('RECOMMNED')"
            />
        </div>
        
        <!-- Right column -->
        <div
            class="container flex flex-col gap-2 h-full rounded-lg shadow-md p-4 overflow-y-auto scrollbar-hide"
        >
            <div class="flex-1 overflow-hidden">
                <RankingList class="h-full" />
            </div>
            <div class="overflow-hidden">
                <!-- <MainSwipeComponent
                    :sheets="newSheets"
                    :title="'나를 위한 추천'"
                    class="h-full"
                    @goToSheetDetail="goToSheetDetail"
                    @goToSheetSearchListView="
                        goToSheetSearchListView('RECOMMNED')
                    "
                /> -->
            </div>
        </div>
    </div>
</template>

<style scoped>
.text-title {
    color: rgb(52, 152, 219);
}

.container {
    border: 1px solid #c9deff;
    border-radius: 12px;
    padding: 30px 28px;
    box-sizing: border-box;
    box-shadow: 0px 0px 6px 1px rgba(63, 128, 234, 0.2);
}

.container:hover {
    border-color: #007aff;
    box-shadow: 0px 4px 18px rgba(0, 122, 255, 0.3);
    border-width: 2px;
}

.text-title {
    color: rgb(52, 152, 219);
}

.scrollbar-hide {
    scrollbar-width: none; /* Firefox */
    -ms-overflow-style: none; /* IE and Edge */
}

.scrollbar-hide::-webkit-scrollbar {
    display: none; /* Chrome, Safari, and Opera */
}
</style>
