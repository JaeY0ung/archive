<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { usePlayStore } from "@/stores/play";
import RoomCreateModal from "./RoomCreateModal.vue";
import { useMusicStore } from "@/stores/sheet";

const musicStore = useMusicStore();
const router = useRouter();
const playStore = usePlayStore();
const isLoading = ref(true);

const getAllRooms = async () => {
    await playStore.fetchAllRooms();
    isLoading.value = false;
};

onMounted(() => {
    getAllRooms();
    musicStore.f1 = [];
    musicStore.jaccard = [];
});

const selectMode = (mode) => {
    playStore.setMode(mode);
    if (mode === "multi") {
        playStore.setShowModal(true);
    } else {
        router.push({ name: "singleWait" });
    }
};

const enterRoom = async (roomId) => {
    await playStore.enterRoom(roomId);
    router.push({ name: "multiWait", params: { roomId } });
};

const rankings = ref([
    { rank: 1, name: "User1", wins: 60, losses: 20 },
    { rank: 2, name: "User2", wins: 55, losses: 25 },
    { rank: 3, name: "User3", wins: 50, losses: 30 },
    { rank: 4, name: "User4", wins: 45, losses: 35 },
    { rank: 5, name: "User5", wins: 40, losses: 40 },
    { rank: 6, name: "User6", wins: 35, losses: 45 },
    { rank: 7, name: "User7", wins: 30, losses: 50 },
    { rank: 8, name: "User8", wins: 25, losses: 55 },
    { rank: 9, name: "User9", wins: 20, losses: 60 },
    { rank: 10, name: "User10", wins: 15, losses: 65 },
    { rank: 11, name: "User11", wins: 15, losses: 65 },
    { rank: 12, name: "User12", wins: 15, losses: 65 },
    { rank: 13, name: "User13", wins: 15, losses: 65 },
    { rank: 14, name: "User14", wins: 15, losses: 65 },
    { rank: 15, name: "User15", wins: 15, losses: 65 },
]);
</script>

<template>
    <div class="min-h-screen p-2">
        <div class="container mx-auto bg-white bg-opacity-90 rounded-xl shadow-lg overflow-hidden">
            <div class="flex flex-col md:flex-row">
                <!-- 랭킹 섹션 -->
                <div class="w-full md:w-1/4 bg-gray-50 p-6">
                    <h2 class="text-3xl font-bold mb-6 text-center text-gray-800">RANKING</h2>
                    <div class="h-[70vh] overflow-y-auto pr-4 custom-scrollbar">
                        <ul class="space-y-4">
                            <li
                                v-for="(item, index) in rankings"
                                :key="index"
                                class="bg-white rounded-lg p-4 shadow-sm transition duration-300 hover:shadow-md"
                            >
                                <div class="flex items-center">
                                    <span class="text-2xl font-bold mr-4 text-blue-600">{{
                                        item.rank
                                    }}</span>
                                    <div>
                                        <p class="font-semibold text-gray-800">{{ item.name }}</p>
                                        <p class="text-sm text-gray-600">
                                            {{ item.wins }}승 {{ item.losses }}패
                                        </p>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>

                <!-- 메인 콘텐츠 -->
                <div class="flex-1 p-6">
                    <!-- 모드 선택 버튼 -->
                    <div class="flex gap-6 mb-8">
                        <button
                            @click="selectMode('single')"
                            class="flex-1 bg-blue-500 text-white py-4 px-6 rounded-lg text-xl font-bold shadow-md transition duration-300 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
                        >
                            싱글 플레이
                        </button>
                        <button
                            @click="selectMode('multi')"
                            class="flex-1 bg-green-500 text-white py-4 px-6 rounded-lg text-xl font-bold shadow-md transition duration-300 hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
                        >
                            멀티 방 만들기
                        </button>
                    </div>

                    <!-- 방 리스트 -->
                    <div
                        class="bg-gray-50 rounded-xl shadow-inner p-6 h-[calc(100vh-300px)] overflow-y-auto custom-scrollbar"
                    >
                        <h2 class="text-2xl font-bold mb-6 text-gray-800">방 리스트</h2>
                        <div v-if="isLoading" class="text-center py-8 text-gray-600">
                            <svg
                                class="animate-spin h-10 w-10 mx-auto mb-4 text-blue-500"
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 24 24"
                            >
                                <circle
                                    class="opacity-25"
                                    cx="12"
                                    cy="12"
                                    r="10"
                                    stroke="currentColor"
                                    stroke-width="4"
                                ></circle>
                                <path
                                    class="opacity-75"
                                    fill="currentColor"
                                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                                ></path>
                            </svg>
                            Loading...
                        </div>
                        <ul v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <li
                                v-for="room in playStore.getRooms"
                                :key="room.id"
                                @click="enterRoom(room.id)"
                                class="bg-white rounded-lg shadow-sm p-4 cursor-pointer transition duration-300 hover:shadow-md hover:bg-gray-50"
                            >
                                <div class="flex items-center">
                                    <img
                                        src="@/assets/img/default/song_img.png"
                                        class="w-16 h-16 rounded-md object-cover mr-4"
                                        alt="Song image"
                                    />
                                    <div class="flex-1">
                                        <h3 class="text-lg font-semibold text-gray-800 truncate">
                                            {{ room.title }}
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span class="text-sm text-gray-600 mr-4"
                                                >{{ room.occupancy }}/{{ room.capacity }}</span
                                            >
                                            <span
                                                :class="[
                                                    'w-3 h-3 rounded-full mr-2',
                                                    room.isPlaying ? 'bg-red-500' : 'bg-green-500',
                                                ]"
                                            ></span>
                                            <span class="text-sm text-gray-600">{{
                                                room.isPlaying ? "게임중" : "대기중"
                                            }}</span>
                                        </div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <RoomCreateModal />
</template>

<style scoped>
.custom-scrollbar {
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.custom-scrollbar::-webkit-scrollbar {
    display: none;
}
</style>
