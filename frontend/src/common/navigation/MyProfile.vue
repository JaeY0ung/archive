<script setup>
import { onMounted, computed } from 'vue';
import Profile from '@/common/icons/Profile.vue'
import { RouterLink, useRouter } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { storeToRefs } from 'pinia';

const router = useRouter();
const userStore = useUserStore();
const { isLogin, userInfo } = storeToRefs(userStore);

const userNickname = computed(() => userInfo.value?.nickname || '사용자');

const goToMyPage = () => {
  router.push({ name: 'mypage' });
};
</script>

<template>
  <div class="profile-div">
    <div class="profile-content">
      <!-- 로그인 완료 시 -->
      <template v-if="isLogin && userInfo">
        <div class="user-info" @click="goToMyPage">
          <Profile class="profile-icon"/>
          <span class="user-name">{{ userNickname }}</span>
        </div>
      </template>

      <!-- 비 로그인 시 -->
      <RouterLink v-else :to="{ name : 'login' }" class="login-link">
        <span>로그인하기</span>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.profile-div {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 10;
}

.profile-content {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 20px;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.profile-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.profile-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.user-name {
  font-weight: 500;
  color: #333;
  pointer-events: none;
}

.login-link {
  text-decoration: none;
  color: #3498db;
  font-weight: 500;
  transition: color 0.3s ease;
  padding: 4px 0; 
  display: inline-block;  
}

.login-link:hover {
  color: #2980b9;
}

.login-link span {
  pointer-events: none;
}
</style>