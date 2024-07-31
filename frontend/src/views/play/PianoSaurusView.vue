<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { usePlayStore } from "@/stores/play";

const router = useRouter();
const playStore = usePlayStore();

const selectMode = (mode) => {
    playStore.setMode(mode);
  router.push({ name: "wait" });
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
  { rank: 10, name: "User10", wins: 15, losses: 65 }
]);

const rooms = ref([
  { id: 1, name: "Room 1", status: "available" },
  { id: 2, name: "Room 2", status: "available" },
  { id: 3, name: "Room 3", occupancy: 1, capacity: 2 },
  { id: 4, name: "Room 4", occupancy: 2, capacity: 2 },
  { id: 5, name: "Room 5", status: "available" },
  { id: 6, name: "Room 6", status: "available" },
  { id: 7, name: "Room 7", occupancy: 1, capacity: 2 },
  { id: 8, name: "Room 8", occupancy: 2, capacity: 2 },
  { id: 9, name: "Room 9", status: "available" },
  { id: 10, name: "Room 10", status: "available" }
]);
</script>

<template>
  <header>
    <h2>피아노사우루스</h2>
  </header>
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
        <button @click="() => selectMode('single')">혼자 연습하기</button>
        <button @click="() => selectMode('multi')">방 만들기</button>
      </section>
      <article class="room-list">
        <h2>방 리스트</h2>
        <ul>
          <li v-for="room in rooms" :key="room.id" class="room-item">
            <span>{{ room.name }}</span>
            <span v-if="room.capacity" class="capacity">{{ room.occupancy }}/{{ room.capacity }}</span>
            <span v-else class="status" :class="room.status"></span>
          </li>
        </ul>
      </article>
    </main>
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
