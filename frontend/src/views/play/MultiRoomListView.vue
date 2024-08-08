<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { usePlayStore } from '@/stores/play';
import RoomCreateModal from './RoomCreateModal.vue';

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
  { rank: 10, name: 'User10', wins: 15, losses: 65 }
]);

</script>

<template>
    <div class="flex flex-grow w-full h-full bg-red-400">
    <div class="flex flex-col mt-[10px] mb-[10px] bg-yellow-400 ranking">
      <h2>RANKING</h2>
      <ul>
        <li v-for="(item, index) in rankings" :key="index">
          {{ item.rank }}위 {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
        </li>
      </ul>
    </div>

    <div class="flex flex-col flex-grow ml-[5px] bg-green-200">
      <div class="flex gap-[20px] justify-center items-center mt-[10px] h-[50px] bg-blue-200 p-[10px]">
        <div class="flex-1 flex-col flex-grow h-full justify-center text-center items-center rounded-md text-white text-3xl bg-blue-600 cursor-pointer" @click="selectMode('single')">혼자 연습하기</div>
        <div class="flex-1 flex-col flex-grow h-full justify-center text-center items-center rounded-md text-white text-3xl bg-blue-600 cursor-pointer" @click="selectMode('multi')">방 만들기</div>
      </div>
      <div class="flex flex-grow flex-col h-full mb-[10px]" >
        <h2 class="flex items-center h-[50px] mt-[5px] p-[10px] bg-blue-200">방 리스트</h2>
        <div class="flex flex-grow w-full h-full relative overflow-hidden">
          <ul class="flex flex-grow flex-col w-full h-full absolute scroll-y p-[10px] gap-[5px] bg-yellow-300">
            <div v-if="isLoading.value">Loading...</div>
            <li v-else v-for="room in playStore.getRooms" :key="room.id" class="flex w-full h-[80px] justify-between items-center p-[10px] rounded-md  cursor-pointer bg-red-300" @click="enterRoom(room.id)">
              <span>{{ room.title }}</span>
              <span class="capacity">{{ room.occupancy }}/{{ room.capacity }}</span>
              <span>{{ room.status }}</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <RoomCreateModal />
    </div>
</template>

<style scoped>
button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: #2196f3;
  color: #fff;
  border: none;
  border-radius: 5px;
  margin-top: 20px;
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
