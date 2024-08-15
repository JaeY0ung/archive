<template>
    <Transition name="modal">
        <div
            v-if="showModal"
            class="fixed inset-0 z-50 overflow-y-auto bg-black bg-opacity-50 backdrop-blur-sm"
            aria-labelledby="modal-title"
            role="dialog"
            aria-modal="true"
        >
            <div class="flex items-center justify-center min-h-screen">
                <div
                    class="modal-content bg-white rounded-2xl overflow-hidden shadow-2xl w-full max-w-md mx-4"
                >
                    <div class="px-6 pt-6 pb-4">
                        <h3 class="text-2xl font-semibold text-gray-900 mb-6" id="modal-title">
                            방 만들기
                        </h3>
                        <div class="mb-6">
                            <input
                                type="text"
                                name="roomTitle"
                                id="roomTitle"
                                v-model="roomTitle"
                                @keyup.enter="createEnterRoom"
                                class="w-full px-4 py-3 rounded-lg bg-gray-100 border-transparent focus:border-blue-500 focus:bg-white focus:ring-0 text-sm transition duration-150 ease-in-out"
                                placeholder="방 제목을 입력하세요"
                            />
                        </div>
                    </div>
                    <div class="px-6 py-2 bg-gray-50 flex justify-end space-x-3">
                        <button
                            type="button"
                            @click="closeModal"
                            class="px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 rounded-lg transition duration-150 ease-in-out"
                        >
                            취소
                        </button>
                        <button
                            type="button"
                            @click="createEnterRoom"
                            class="px-4 py-2 text-sm font-medium text-white bg-blue-500 hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 rounded-lg transition duration-150 ease-in-out"
                        >
                            확인
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </Transition>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { usePlayStore } from "@/stores/play";
import { useRouter } from "vue-router";

const router = useRouter();
const playStore = usePlayStore();
const { showModal, roomTitle } = storeToRefs(playStore);

const closeModal = () => {
    playStore.setShowModal(false);
    roomTitle.value = "";
};

const createEnterRoom = async () => {
    if (!roomTitle.value.trim()) {
        alert("방 제목을 입력해주세요.");
        return;
    }
    await playStore.createRoom();
    const roomId = playStore.rooms[playStore.rooms.length - 1].id;
    router.push({ path: `/room/multi/${roomId}/wait` });
    roomTitle.value = "";
    closeModal();
};
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;700&display=swap");

body {
    font-family: "Noto Sans KR", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica,
        Arial, sans-serif;
}

.modal-enter-active,
.modal-leave-active {
    transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
    opacity: 0;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translate3d(0, 40px, 0);
    }
    to {
        opacity: 1;
        transform: translate3d(0, 0, 0);
    }
}

.modal-content {
    animation: fadeInUp 0.5s ease-out;
}
</style>
