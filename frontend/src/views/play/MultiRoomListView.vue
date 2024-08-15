<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { usePlayStore } from "@/stores/play";
import RoomCreateModal from "./RoomCreateModal.vue";
import { useMusicStore } from "@/stores/sheet";
import RankingList from "@/common/main/RankingList.vue";

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
</script>

<template>
    <div class="min-h-screen p-2">
        <div
            class="container mx-auto bg-white bg-opacity-90 rounded-xl shadow-lg overflow-hidden flex flex-col md:flex-row h-[calc(100vh-1rem)]"
        >
            <!-- 랭킹 섹션 -->
            <div
                class="w-full md:w-1/4 bg-gray-50 p-6 flex flex-col overflow-hidden"
            >
                <h2 class="text-2xl font-bold mb-4 text-gray-800">랭킹</h2>
                <div class="flex-grow overflow-y-auto custom-scrollbar">
                    <RankingList class="w-full" />
                </div>
            </div>

            <!-- 메인 콘텐츠 -->
            <div class="flex-1 p-6 flex flex-col overflow-hidden">
                <!-- 모드 선택 버튼 -->
                <div class="flex gap-6 mb-8">
                    <button
                        @click="selectMode('single')"
                        class="flex-1 text-white py-4 px-6 rounded-lg text-xl font-bold shadow-md transition duration-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50"
                        :class="{ 'custom-blue-hover': true }"
                        :style="{ backgroundColor: 'rgb(52, 152, 219)' }"
                    >
                        싱글 플레이
                    </button>
                    <button
                        @click="selectMode('multi')"
                        class="flex-1 bg-green-500 text-white py-4 px-6 rounded-lg text-xl font-bold shadow-md transition duration-300 hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
                    >
                        멀티 방 생성
                    </button>
                </div>

                <!-- 방 리스트 -->
                <div class="flex-grow overflow-y-auto custom-scrollbar">
                    <div class="bg-gray-50 rounded-xl shadow-inner p-6 h-full">
                        <h2 class="text-2xl font-bold mb-6 text-gray-800">
                            방 리스트
                        </h2>
                        <div
                            v-if="isLoading"
                            class="text-center py-8 text-gray-600"
                        >
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
                        <ul
                            v-else
                            class="grid grid-cols-1 md:grid-cols-2 gap-4"
                        >
                            <li
                                class="bg-white rounded-lg shadow-sm p-4 cursor-pointer transition duration-300 hover:shadow-md hover:bg-gray-50"
                            >
                                <div class="flex items-center">
                                    <img
                                        src="https://images.genius.com/61dd48b7d985535163c8c6fa4e63a4f4.300x300x1.jpg"
                                        class="w-16 h-16 rounded-md object-cover mr-4"
                                        alt="Song image"
                                    />
                                    <div class="flex-1">
                                        <h3
                                            class="text-lg font-semibold text-gray-800 truncate"
                                        >
                                            초보만
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span
                                                class="text-sm text-gray-600 mr-4"
                                                >2/2</span
                                            >
                                            <span
                                                class="
                                                    w-3 h-3 rounded-full mr-2 bg-red-500"
                                            ></span>
                                            <span
                                                class="text-sm text-gray-600"
                                                >게임중</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </li>    
                            <li
                                class="bg-white rounded-lg shadow-sm p-4 cursor-pointer transition duration-300 hover:shadow-md hover:bg-gray-50"
                            >
                                <div class="flex items-center">
                                    <img
                                        src="@/assets/img/default/song_img.png"
                                        class="w-16 h-16 rounded-md object-cover mr-4"
                                        alt="Song image"
                                    />
                                    <div class="flex-1">
                                        <h3
                                            class="text-lg font-semibold text-gray-800 truncate"
                                        >
                                            캐논 한 판 하실분
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span
                                                class="text-sm text-gray-600 mr-4"
                                                >1/2</span
                                            >
                                            <span
                                                class="
                                                    w-3 h-3 rounded-full mr-2 bg-green-500"
                                            ></span>
                                            <span
                                                class="text-sm text-gray-600"
                                                >대기중</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </li>    
                            <li
                                class="bg-white rounded-lg shadow-sm p-4 cursor-pointer transition duration-300 hover:shadow-md hover:bg-gray-50"
                            >
                                <div class="flex items-center">
                                    <img
                                        src="https://image.bugsm.co.kr/album/images/500/40993/4099372.jpg"
                                        class="w-16 h-16 rounded-md object-cover mr-4"
                                        alt="Song image"
                                    />
                                    <div class="flex-1">
                                        <h3
                                            class="text-lg font-semibold text-gray-800 truncate"
                                        >
                                            2000점 이상만
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span
                                                class="text-sm text-gray-600 mr-4"
                                                >2/2</span
                                            >
                                            <span
                                                class="
                                                    w-3 h-3 rounded-full mr-2 bg-red-500"
                                            ></span>
                                            <span
                                                class="text-sm text-gray-600"
                                                >게임중</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </li>    
                            <li
                                class="bg-white rounded-lg shadow-sm p-4 cursor-pointer transition duration-300 hover:shadow-md hover:bg-gray-50"
                            >
                                <div class="flex items-center">
                                    <img
                                        src="https://cdn.imweb.me/thumbnail/20210627/81c6dd19d96a2.jpg"
                                        class="w-16 h-16 rounded-md object-cover mr-4"
                                        alt="Song image"
                                    />
                                    <div class="flex-1">
                                        <h3
                                            class="text-lg font-semibold text-gray-800 truncate"
                                        >
                                            콜드플레이 완주방
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span
                                                class="text-sm text-gray-600 mr-4"
                                                >2/2</span
                                            >
                                            <span
                                                class="
                                                    w-3 h-3 rounded-full mr-2 bg-red-500"
                                            ></span>
                                            <span
                                                class="text-sm text-gray-600"
                                                >게임중</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </li>    
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
                                        <h3
                                            class="text-lg font-semibold text-gray-800 truncate"
                                        >
                                            {{ room.title }}
                                        </h3>
                                        <div class="flex items-center mt-2">
                                            <span
                                                class="text-sm text-gray-600 mr-4"
                                                >{{ room.occupancy }}/{{
                                                    room.capacity
                                                }}</span
                                            >
                                            <span
                                                :class="[
                                                    'w-3 h-3 rounded-full mr-2',
                                                    room.isPlaying
                                                        ? 'bg-red-500'
                                                        : 'bg-green-500',
                                                ]"
                                            ></span>
                                            <span
                                                class="text-sm text-gray-600"
                                                >{{
                                                    room.isPlaying
                                                        ? "게임중"
                                                        : "대기중"
                                                }}</span
                                            >
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

.custom-blue-hover:hover {
    background-color: rgb(34, 131, 196) !important;
}
</style>
