<template>
<div v-if="showModal" class="room-modal">
    <div class="room-modal-content">
    <span class="close" @click="closeModal">&times;</span>
    <h2>방 만들기</h2>
    <input v-model="roomTitle" @keyup.enter="createEnterRoom" placeholder="방 제목을 입력하세요" />
    <button @click="createEnterRoom">확인</button>
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
};

const createEnterRoom = async () => {
    await playStore.createRoom();
    const roomId = playStore.rooms[playStore.rooms.length - 1].id;
    router.push({ path: `/room/multi/${roomId}/wait` }); // wait 페이지로 이동
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
