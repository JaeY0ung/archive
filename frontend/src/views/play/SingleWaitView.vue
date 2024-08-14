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
        class="flex flex-col flex-grow w-full items-center justify-center h-[calc(100vh-80px)] overflow-hidden py-4 rounded-xl"
    >
        <div
            class="flex flex-col w-full max-w-[95%] h-full max-h-[95%] rounded-xl shadow-xl relative "
        >
            <!--위 -->
            <div
                class="relative w-full h-3/4 rounded-t-xl bg-white shadow-2xl overflow-hidden"
            >
                <div
                    class="absolute inset-0 bg-cover bg-center opacity-70"
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
            <!-- 아래 -->
            <div
                class="flex flex-grow h-[200px] w-full justify-between items-center rounded-b-xl mt-1 "
                style="border-top: 2px solid #fef9c3 ;"
            >
                <UserCardForPlay 
                    class="custom-shadow w-[40vw]"
                    :user="loginUser"
                />
                <div class="flex w-[40vw] h-full p-[5px] gap-3 m-1 bg-white bg-opacity-0 font-bold">

                    <div class="custom-shadow w-[50%] bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-2xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        @click="onClickStart"
                        >
                        연주하기
                    </div>
                    <div class="custom-shadow w-[50%] bg-[#f3f7fd] text-[#4A90E2] flex justify-center items-center text-2xl font-bold py-2 px-4 rounded-xl shadow-md hover:bg-[#e6f0fb] active:transform active:scale-95 transition-transform cursor-pointer"
                        @click="onClickQuit"
                        >
                        나가기
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.btn {
    @apply font-bold py-2 px-4 rounded transition duration-300 ease-in-out;
}


.button-style {
    @apply flex flex-grow flex-1 items-center justify-center cursor-pointer rounded-xl;
    transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.button-style:active {
    transform: scale(0.7);
    box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
}

.custom-shadow {
    @apply rounded-xl bg-white;
    box-shadow: 0 4px 10px rgba(0, 123, 255, 0.2), 0 2px 4px rgba(0, 123, 255, 0.15);
}
</style>
