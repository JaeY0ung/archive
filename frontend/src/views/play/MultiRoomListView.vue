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
    <div class="container">
    <aside class="ranking">
      <h2>RANKING</h2>
      <ul>
        <li v-for="(item, index) in rankings" :key="index">
          {{ item.rank }}위 {{ item.name }} 님 {{ item.wins }}승 {{ item.losses }}패
        </li>
      </ul>
    </aside>

    <main>
      <section class="menu">
        <button @click="selectMode('single')">혼자 연습하기</button>
        <button @click="selectMode('multi')">방 만들기</button>
      </section>
      <article class="room-list">
        <h2>방 리스트</h2>
        <ul>
          <li v-if="isLoading.value">Loading...</li>
          <li 
            v-else 
            v-for="room in playStore.getRooms" 
            :key="room.id" 
            class="room-item" 
            @click="enterRoom(room.id)">
            <span>{{ room.title }}</span>
            <span class="capacity">{{ room.occupancy }}/{{ room.capacity }}</span>
            <span>{{ room.status }}</span>
          </li>
        </ul>
      </article>
    </main>
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

.menu {
  display: flex;
  gap: 20px;
}

.container {
  display: flex;
  justify-content: space-between;
  padding: 20px;
}

.ranking {
  width: 20%;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
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

main {
  width: 75%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.menu {
  display: flex;
  flex-direction: row;
  gap: 20px;
  margin-bottom: 20px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: #2196f3;
  color: #fff;
  border: none;
  border-radius: 5px;
}

.room-list {
  width: 100%;
  padding: 20px;
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
  background-color: #f9f9f9;
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
