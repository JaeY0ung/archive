<template>
  <div class="notification-icon">
    <img src="@/assets/img/common/bell.png" alt="Notifications" @click="toggleNotifications" />
    <div v-if="showBubble" class="notification-bubble">
      <div v-for="notification in unreadNotifications" :key="notification.title" class="notification">
        <div class="title">{{ notification.title }}</div>
        <div class="body">{{ notification.body }}</div>
        <div v-if="notification.alertType === 1" class="actions">
          <button @click="acceptInvite(notification)">수락</button>
          <button @click="declineInvite(notification)">거절</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { usePlayStore } from '@/stores/play';
import { useUserStore } from '@/stores/user';


const notifications = ref([]);
// const showBadge = ref(false);
const showBubble = ref(false);
const router = useRouter();
const playStore = usePlayStore();
const userStore = useUserStore();

// 읽지 않은 알림만 필터링하여 반환
const unreadNotifications = computed(() => {
  return notifications.value.filter(notification =>
      !notification.readStatus && notification.senderId !== userStore.userInfo.id
  );
});

function toggleNotifications() {
  showBubble.value = !showBubble.value;
}

function updateNotifications() {
  // 읽지 않은 알림이 없으면 말풍선 닫기
  if (unreadNotifications.value.length === 0) {
    showBubble.value = false;
  }
}

watch(notifications, () => {
  showBubble.value = unreadNotifications.value.length > 0;
});

// 수락 버튼 클릭 시 해당 방으로 이동
async function acceptInvite(notification) {
  if (notification.roomId) {
    try {
      await playStore.enterRoom(notification.roomId);
      await router.push({ name: 'multiWait', params: { roomId: notification.roomId } });
      notification.readStatus = true;
      showBadge.value = false;
      updateNotifications();
    } catch (err) {
      console.error("방으로 이동 실패:", err);
    }
  } else {
    console.error("roomId가 undefined or null 입니다.");
  }
}

// 거절 버튼 클릭 시 말풍선만 닫기
function declineInvite(notification) {
  notification.readStatus = true;
  updateNotifications();
}

// 외부에서 알림을 추가하는 함수
window.showNotification = (title, body, alertType, roomId, senderId, readStatus) => {
  // 현재 사용자가 sender가 아닌 경우에만 알림을 추가
  if (senderId !== userStore.userInfo.id) {
    notifications.value.push({ title, body, alertType, roomId, senderId, readStatus });
    // showBadge.value = true;
    showBubble.value = true;
  }
};

watch(notifications, () => {
  updateNotifications();
});
</script>

<style scoped>
/* 스타일은 변경 없이 유지됩니다 */
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

.notification-bubble .actions {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.notification-bubble .actions button {
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.notification-bubble .actions button:first-of-type {
  background-color: #ff6b6b;
  color: white;
}

.notification-bubble .actions button:last-of-type {
  background-color: #1e90ff;
  color: white;
}
</style>
