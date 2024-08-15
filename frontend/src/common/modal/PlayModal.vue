<!-- Modal.vue -->
<template>
  <div v-if="isVisible" class="modal-overlay">
    <div class="modal-content">
      <h2>{{ title }}</h2>
      <p>{{ message }}</p>
      <p><strong>내 점수:</strong> {{ myScore }}점</p>
      <p><strong>{{ opponentNickname }}의 점수:</strong> {{ opponentScore }}점</p>
      <button @click="closeModal">확인</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const props = defineProps({
  title: {
    type: String,
    default: "알림",
  },
  message: {
    type: String,
    default: "",
  },
  isVisible: {
    type: Boolean,
    default: false,
  },
  myScore: {
    type: Number,
    default: 0,
  },
  opponentScore: {
    type: Number,
    default: 0,
  },
  opponentNickname: {
    type: String,
    default: "상대방",
  }
});

const emit = defineEmits(["close"]);

const closeModal = () => {
  emit("close");
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #1b7fd3;
}
</style>

<!--&lt;!&ndash; Modal.vue &ndash;&gt;-->
<!--<template>-->
<!--  <div v-if="isVisible" class="modal-overlay">-->
<!--    <div class="modal-content">-->
<!--      <h2>{{ title }}</h2>-->
<!--      <p>{{ message }}</p>-->
<!--      <p><strong>내 점수:</strong> {{ myScore }}점</p>-->
<!--      <p><strong>{{ opponentNickname }}의 점수:</strong> {{ opponentScore }}점</p>-->
<!--      <button @click="closeModal">확인</button>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from "vue";-->

<!--const props = defineProps({-->
<!--  title: {-->
<!--    type: String,-->
<!--    default: "알림",-->
<!--  },-->
<!--  message: {-->
<!--    type: String,-->
<!--    default: "",-->
<!--  },-->
<!--  isVisible: {-->
<!--    type: Boolean,-->
<!--    default: false,-->
<!--  },-->
<!--});-->

<!--const emit = defineEmits(["close"]);-->

<!--const closeModal = () => {-->
<!--  emit("close");-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.modal-overlay {-->
<!--  position: fixed;-->
<!--  top: 0;-->
<!--  left: 0;-->
<!--  width: 100vw;-->
<!--  height: 100vh;-->
<!--  background: rgba(0, 0, 0, 0.6);-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--}-->

<!--.modal-content {-->
<!--  background: white;-->
<!--  padding: 20px;-->
<!--  border-radius: 10px;-->
<!--  text-align: center;-->
<!--  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);-->
<!--}-->

<!--button {-->
<!--  margin-top: 20px;-->
<!--  padding: 10px 20px;-->
<!--  background-color: #2196f3;-->
<!--  color: white;-->
<!--  border: none;-->
<!--  border-radius: 5px;-->
<!--  cursor: pointer;-->
<!--}-->

<!--button:hover {-->
<!--  background-color: #1b7fd3;-->
<!--}-->
<!--</style>-->
