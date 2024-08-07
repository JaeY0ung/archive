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
const userImg = computed(() => {
  if (userInfo.value?.userImg) {
    return `data:image/jpeg;base64,${userInfo.value.userImg}`;
  }
  return null;
});

const goToMyPage = () => {
  if (router.currentRoute.value.name === 'mypage') {
    router.go(-1);
  } else {
    router.push({ name: 'mypage' });
  }
};
</script>

<template>
  <div class="flex justify-end ml-auto mr-[15px] ">
    <div class="profile-content">
      <!-- 로그인 완료 시 -->
      <template v-if="isLogin && userInfo">
        <div class="user-info" @click="goToMyPage">
          <img v-if="userImg" :src="userImg" alt="User Profile" class="user-image">
          <Profile v-else class="profile-icon"/>
          <span class="user-name">{{ userNickname }}</span>
        </div>
      </template>
      <!-- 비 로그인 시 -->
      <RouterLink v-else :to="{ name : 'login' }" class="login-link">
        <Profile class="profile-icon"/>
          <span>로그인하기</span>
      </RouterLink>
    </div>
  </div>
</template>

<style scoped>
.profile-content {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 240, 240, 0.9));
  border-radius: 25px;
  padding: 0 20px;
  display: flex;
  width:160px;
  align-items: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  height: 48px;
}

.profile-content:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(245, 245, 245, 0.95));
}

.user-info, .login-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  height: 100%;
}

.profile-icon {
  width: 28px;
  height: 28px;
  margin-right: 12px;
  fill: #333;
  transition: fill 0.3s ease;
  flex-shrink: 0; 
}

.user-info:hover .profile-icon, .login-link:hover .profile-icon {
  fill: #3498db;
}

.user-name, .login-link span {
  font-weight: 600;
  color: #333;
  font-size: 0.95rem;
  transition: color 0.3s ease;
  white-space: nowrap;
}

.user-info:hover .user-name, .login-link:hover span {
  color: #3498db;
}

.login-link {
  text-decoration: none;
}

.user-image {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  flex-shrink: 0;
}
</style>