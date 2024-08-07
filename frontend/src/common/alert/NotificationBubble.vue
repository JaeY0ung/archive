<template>
  <div class="notification-icon">
      <img src="@/assets/img/common/bell.png" alt="Notifications" />
      <div v-if="showBadge" class="notification-badge"></div>
      <div class="notification-bubble">
          <div v-for="notification in notifications" :key="notification.title" class="notification">
              <div class="title">{{ notification.title }}</div>
              <div class="body">{{ notification.body }}</div>
          </div>
      </div>
  </div>
</template>

<script setup>
import { defineProps, ref, watch } from 'vue';

const props = defineProps({
  notifications: Array,
  showBadge: Boolean
});

watch(props.notifications, () => {
  // Hide the badge after some time or user interaction
  setTimeout(() => {
      props.showBadge = false;
  }, 5000); // Hide after 5 seconds
});
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
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 10px;
  padding: 10px;
  color: white;
  z-index: 1000;
  display: none;
}

.notification {
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid white;
}

.title {
  font-weight: bold;
  margin-bottom: 5px;
}

.body {
  font-size: 14px;
}
</style>
