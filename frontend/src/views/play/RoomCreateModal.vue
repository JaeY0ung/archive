<template>
    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 shadow-md">
        <div class="bg-white rounded-lg shadow-xl w-full max-w-md p-6 space-y-4 relative">
            <div class="flex justify-between items-center">
                <h2 class="text-2xl font-semibold text-center w-full">방 만들기</h2>
                <button class="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-2xl font-bold focus:outline-none" @click="closeModal">&times;</button>
            </div>
            <hr class="border-gray-300 mt-2"/>
            <div>
                <label for="roomTitle" class="block text-lg font-medium text-gray-700">방장님 환영합니다!</label>
                <input 
                    id="roomTitle"
                    v-model="roomTitle"
                    @keyup.enter="createEnterRoom"
                    class="mt-2 w-full p-3 border text-2xl border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-200"
                    placeholder="방 제목을 입력하세요"
                />
            </div>
            <div class="flex justify-center">
            <button 
                @click="createEnterRoom"
                class="w-full py-3 mt-4 pt-4 pb-4 bg-sky-100 text-gray-600 rounded-lg text-2xl hover:bg-gray-100 transition-colors duration-200 shadow-md"
            >
                확인
            </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { storeToRefs } from 'pinia';
import { usePlayStore } from '@/stores/play';
import { localAxios } from "@/util/http-common";
import { useRouter } from 'vue-router';

const router = useRouter();

const local = localAxios();

const playStore = usePlayStore();
const { showModal, roomTitle } = storeToRefs(playStore);

const closeModal = () => {
    playStore.setShowModal(false);
    roomTitle.value = ''; 
};

const createEnterRoom = async () => {
    await playStore.createRoom();
    const roomId = playStore.rooms[playStore.rooms.length - 1].id;
    router.push({ path: `/room/multi/${roomId}/wait` }); // wait 페이지로 이동
    roomTitle.value = ''; 
};
</script>

<style scoped>
.room-modal {
position: fixed;
z-index: 1;
left: 0;
top: 0;
width: 100%;
height: 100%;
overflow: auto;
background-color: rgb(0,0,0);
background-color: rgba(0,0,0,0.4);
display: flex;
justify-content: center;
align-items: center;
}

.room-modal-content {
background-color: #fefefe;
padding: 20px;
border: 1px solid #888;
width: 80%;
max-width: 400px;
border-radius: 10px;
}

.close {
color: #aaa;
float: right;
font-size: 28px;
font-weight: bold;
}

.close:hover,
.close:focus {
color: black;
text-decoration: none;
cursor: pointer;
}
</style> 
