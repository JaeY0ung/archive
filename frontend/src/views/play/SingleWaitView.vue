<script setup>
import { useRouter } from "vue-router";
import { ref, onMounted, onUnmounted } from "vue";
import { useUserStore } from "@/stores/user";
// import defaultProfileImage from "@/assets/img/common/default_profile.png";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import SelectCategory from "@/common/sheet/SelectCategory.vue";
import SelectSheet from "@/common/sheet/SelectSheet.vue";
import { searchSheetsByFilter } from "@/api/sheet";
const router = useRouter();
const userStore = new useUserStore();

const isInCategoryView = ref(true);

const user = ref({
    userImg: "",
    nickname: "악카이브1",
    score: "0",
    isEmpty: true,
});

const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;

const onClickStart = () => {
    if (!selectedSheetId.value) {
        alert("악보를 고르세요");
        return;
    }
    router.push({
        name: "singlePlay",
        params: { sheetId: selectedSheetId.value },
    });
};

const onClickQuit = () => {
    router.push({ name: "multiRoomList" });
};

const sheets = ref([]);
const selectedSheetId = ref();

const getPopularsheets = async () => {
    searchSheetsByFilter({ sort: "POPULAR" }, ({ data }) => {
        if (!data) return;
        sheets.value = data;
    });
};

const getNewsheets = async () => {
    searchSheetsByFilter({ sort: "LATEST" }, ({ data }) => {
        if (!data) return;
        sheets.value = data;
    });
};

const getRandomsheets = async () => {
    searchSheetsByFilter({ sort: "RANDOM" }, ({ data }) => {
        if (!data) return;
        sheets.value = data;
    });
};

const getUserLevelsheets = async () => {
    searchSheetsByFilter({ levels: [1] }, ({ data }) => {
        if (!data) return;
        sheets.value = data;
    });
};

const getSheetsByCategory = (sort) => {
    isInCategoryView.value = false;
    if (sort == "RANDOM") getRandomsheets();
    else if (sort == "POPULAR") getPopularsheets();
    else if (sort == "LATEST") getNewsheets();
    else if (sort == "LEVEL") getUserLevelsheets();
};

const setSheetId = (sheetId) => {
    selectedSheetId.value = sheetId;
};

onMounted(() => {
    // 강제 리플로우 유도
    document.body.style.height = "100.1%";
    setTimeout(() => {
        document.body.style.height = "100%";
    }, 0);
});

onUnmounted(() => {});
</script>

<template>
    <div
        class="flex flex-col flex-grow w-full items-center justify-center h-[calc(100vh-60px)] overflow-hidden py-4"
    >
        <div
            class="flex flex-col w-full max-w-[95%] h-full max-h-[95%] rounded-xl shadow-xl relative bg-gradient-to-br from-yellow-100 via-pink-200 to-blue-200"
        >
            <div
                class="relative w-full h-3/4 rounded-t-xl bg-black border-gray-700 shadow-2xl overflow-hidden"
            >
                <div
                    class="absolute inset-0 bg-cover bg-center opacity-70"
                    :style="{
                        backgroundImage: `url(${require('@/assets/img/sheet_play/play-background.jpg')})`,
                        backgroundBlendMode: 'multiply',
                    }"
                ></div>
                <SelectCategory
                    v-if="isInCategoryView"
                    class="absolute inset-0 overflow-auto opacity-80"
                    @send-sheet-category="getSheetsByCategory"
                />
                <SelectSheet
                    v-else
                    class="absolute inset-0 overflow-auto"
                    :sheets="sheets"
                    @send-go-to-back="isInCategoryView = true"
                    @send-sheet-id="setSheetId"
                />
            </div>
            <div
                class="flex flex-grow w-full justify-evenly items-center rounded-b-xl bg-yellow-100 p-4"
            >
                <button class="btn btn-primary w-24" @click="onClickStart">
                    연주하기
                </button>
                <UserCardForPlay
                    :user="loginUser"
                    @onClickStart="onClickStart"
                />
                <button class="btn btn-primary w-24" @click="onClickQuit">
                    나가기
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.btn {
    @apply font-bold py-2 px-4 rounded transition duration-300 ease-in-out;
}
.btn-primary {
    @apply bg-blue-500 text-white hover:bg-blue-700;
}
</style>
