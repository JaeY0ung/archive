<template>
  <div class="notification-icon">
      <img src="@/assets/img/common/bell.png" alt="Notifications" @click="toggleNotifications" />
      <div v-if="showBubble" class="notification-bubble">
          <div v-for="notification in notifications" :key="notification.title" class="notification">
              <div class="title">{{ notification.title }}</div>
              <div class="body">{{ notification.body }}</div>
          </div>
      </div>
      <div v-if="showBadge" class="notification-badge"></div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const notifications = ref([]);
const showBadge = ref(false);
const showBubble = ref(false); 

function toggleNotifications() {
    showBubble.value = !showBubble.value;
}

watch(notifications, () => {
    showBadge.value = true;
    showBubble.value = true; // 알림이 오면 말풍선 보이도록 설정
    setTimeout(() => {
        showBadge.value = false;
    }, 5000);
});

// Expose these functions for external use
window.showNotification = (title, body) => {
    notifications.value.push({ title, body });
    showBadge.value = true;
    showBubble.value = true; // 알림이 오면 말풍선 보이도록 설정
};
</script>

<style scoped>
.notification-icon {
  position: fixed;
  bottom: 10px;
  right: 10px;
  width: 50px;
  height: 50px;
  cursor: pointer;
}

.notification-icon img {
  width: 100%;
  height: 100%;
}

.notification-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  width: 20px;
  height: 20px;
  background-color: red;
  border-radius: 50%;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}

.notification-bubble {
  position: fixed;
  bottom: 70px;
  right: 10px;
  width: 250px;
  max-height: 400px;
  background: #e1f5fe;
  border-radius: 10px;
  padding: 10px;
  color: #000;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: block;
}

.notification-bubble .notification {
  margin-bottom: 10px;
}

.notification-bubble .title {
  font-weight: bold;
  margin-bottom: 5px;
}

.notification-bubble .body {
  font-size: 14px;
}
</style>
