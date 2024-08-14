<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { usePlayStore } from '@/stores/play';
import RoomCreateModal from './RoomCreateModal.vue';
import { useMusicStore } from '@/stores/sheet';

const musicStore = useMusicStore();
const router = useRouter();
const playStore = usePlayStore();
const isLoading = ref(true);

// 방 목록 가져오기
const getAllRooms = async () => {
await playStore.fetchAllRooms();
isLoading.value = false;
};

onMounted(() => {
getAllRooms();
musicStore.f1 = [];
musicStore.jaccard = [];
});

// 배틀 모드 선택
const selectMode = (mode) => {
playStore.setMode(mode);
if (mode === 'multi') {
playStore.setShowModal(true);
} else {
router.push({ name: 'singleWait' });
}
};



// 방 입장
const enterRoom = async (roomId) => {
await playStore.enterRoom(roomId);
router.push({ name: 'multiWait', params: { roomId } }); // wait 페이지로 이동
}; 

const rankings = ref([
{ rank: 1, name: 'User1', wins: 60, losses: 20 },
{ rank: 2, name: 'User2', wins: 55, losses: 25 },
{ rank: 3, name: 'User3', wins: 50, losses: 30 },
{ rank: 4, name: 'User4', wins: 45, losses: 35 },
{ rank: 5, name: 'User5', wins: 40, losses: 40 },
{ rank: 6, name: 'User6', wins: 35, losses: 45 },
{ rank: 7, name: 'User7', wins: 30, losses: 50 },
{ rank: 8, name: 'User8', wins: 25, losses: 55 },
{ rank: 9, name: 'User9', wins: 20, losses: 60 },
{ rank: 10, name: 'User10', wins: 15, losses: 65 },
{ rank: 11, name: 'User11', wins: 15, losses: 65 },
{ rank: 12, name: 'User12', wins: 15, losses: 65 },
{ rank: 13, name: 'User12', wins: 15, losses: 65 },
{ rank: 14, name: 'User12', wins: 15, losses: 65 },
{ rank: 15, name: 'User12', wins: 15, losses: 65 }
]);

</script>

<template>
<div class="flex flex-grow h-[87vh] w-full bg-opacity-0">
    
    <!-- 랭킹 -->
    <div class="flex h-full flex-col items-center justify-center rounded-lg bg-opacity-50 bg-white rounded-1g shadow-md p-4 w-1/4">
    <div class="text-2xl font-bold mb-4 text-center">RANKING</div>
    <hr class="border-gray-800" />
    <div class="flex flex-grow w-full h-full relative justify-center items-center">
        <ul class="h-full absolute overflow-y-scroll">
        <li v-for="(item, index) in rankings" :key="index" class="bg-white bg-opacity-80 rounded-lg p-4 mb-2 text-xl font-bold shadow-sm">
            {{ item.rank }}위 | {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
        </li>
        </ul>
    </div>
    </div>

    <!-- 방 리스트 -->
    <div class="overflow-hidden flex flex-col flex-grow ml-4 bg-white bg-opacity-0 rounded-lg ">
        <div class="flex gap-4 justify-center items-center bg-opacity-0 bg-blue-200 p-4 rounded-t-lg">
            <!-- 혼자 연습하기 버튼 -->
            <div class="flex-1 flex flex-grow bg-white bg-opacity-60 h-32 justify-center items-center rounded-md text-gray-900 text-2xl font-bold shadow-md cursor-pointer transform transition-transform duration-200 hover:scale-105 hover:bg-gradient-to-r hover:from-blue-200 hover:via-yellow-100 hover:to-pink-200 hover:bg-opacity-50 "
         @click="selectMode('single')"
        >
      혼자 연습하기
    </div>
            <!-- 방 만들기 버튼 -->
            <div class="flex-1 flex flex-grow bg-white bg-opacity-60 h-32 justify-center items-center rounded-md text-gray-900 text-2xl font-bold shadow-md cursor-pointer transform transition-transform duration-200 hover:scale-105 hover:bg-gradient-to-r hover:from-blue-200 hover:via-yellow-100 hover:to-pink-200 hover:bg-opacity-50"
         @click="selectMode('multi')"
         :class="{ 'hover:bg-gradient-to-r hover:from-blue-200 hover:via-yellow-100 hover:to-pink-200 hover:bg-opacity-30': true }">
      방 만들기
    </div>
        </div>
    <div class="flex flex-grow flex-col h-full mt-4 mb-4" >
        <h2 class="text-2xl font-bold p-4 bg-gray-200 bg-opacity-70 text-center rounded-t-lg shadow-md">방 리스트</h2>
        <div class="relative flex flex-grow w-full h-full overflow-x-hidden overflow-y-scroll bg-white bg-opacity-60 p-4 rounded-b-lg ">
        <ul class="grid grid-cols-2 gap-4 w-full absolute ">
            <div v-if="isLoading.value">Loading...</div>
            <li v-else v-for="room in playStore.getRooms" :key="room.id" class="flex w-full h-[100px] justify-between items-center p-6 bg-white bg-opacity-40 rounded-lg shadow cursor-pointer hover:bg-gray-100 transform transition-transform duration-200 hover:scale-105" @click="enterRoom(room.id)">
            
            <div class="flex items-center w-1/2">
                <img src="@/assets/img/default/song_img.png"  class="w-16 h-16 rounded-md object-cover mr-4">
                <div class="truncate">
                    <span class="text-xl font-bold text-gray-800">{{ room.title }}</span>
                </div>
            </div>
            

            <div class="w-1/4 flex justify-end items-center space-x-2">
                <span class="text-xl text-gray-700">{{ room.occupancy }}/{{ room.capacity }}</span>
            </div>

            <div class="w-1/4 flex justify-end items-center space-x-2">
                <!-- <span :class="{
                        'bg-green-500': room.status,
                        'bg-yellow-500': room.status
                    }"
                    class="w-4 h-4 rounded-full"></span> -->
                <span v-if="room.status" class="text-sm text-gray-600"> 게임중 </span>
                <span v-else class="text-sm text-gray-600"> 대기중 </span>
                <span :class="{
                        'bg-green-500': room.status,
                        'bg-yellow-500': !room.status
                    }"
                    class="w-4 h-4 rounded-full"></span>
            </div>
            </li>
        </ul>
        </div>
    </div>
    </div> 
    <RoomCreateModal />
</div>
</template>

<style scoped>
.hover:bg-opacity-30:hover {
  background-opacity: 0.3;
}

.ranking {
width: 20%;
border: 1px solid #ccc;
}

.ranking h2 {
font-size: 24px;
}

.ranking ul {
list-style: none;
padding: 0;
}

.ranking li {
margin: 10px 0;
padding: 10px;
background-color: #f9f9f9;
border-radius: 5px;
}



.room-list {
width: 100%;
background-color: #fff;
border: 1px solid #ccc;
border-radius: 10px;
}

.room-list h2 {
font-size: 24px;
margin-bottom: 10px;
}

.room-list ul {
list-style: none;
padding: 0;
}

.room-item {
display: flex;
justify-content: space-between;
align-items: center;
padding: 10px;
margin: 10px 0;
border-radius: 5px;
}

.room-item .status {
width: 10px;
height: 10px;
border-radius: 50%;
}

.room-item .status.available {
background-color: green;
}

.room-item .status.full {
background-color: blue;
}
</style>