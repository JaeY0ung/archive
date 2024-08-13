<script setup>
import { ref } from 'vue';

const props = defineProps({
  visible: Boolean,
  title: String,
  message: String,
});

const emit = defineEmits(['close']);

const closeModal = () => {
  emit('close');
};
</script>

<template>
  <div v-if="visible" class="modal-overlay">
	<div class="modal-content">
	  <h2>{{ title }}</h2>
	  <p>{{ message }}</p>
	  <button @click="closeModal">확인</button>
	</div>
  </div>
</template>


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
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 300px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

button {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #1976d2;
}
</style>
