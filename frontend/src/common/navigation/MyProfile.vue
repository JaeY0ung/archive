<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faUserCircle } from "@fortawesome/free-solid-svg-icons";

const router = useRouter();
const userStore = useUserStore();
const { isLogin, userInfo } = storeToRefs(userStore);

const userNickname = computed(() => userInfo.value?.nickname || "사용자");
const userImg = computed(() => {
  if (userInfo.value?.userImg) {
    return `data:image/jpeg;base64,${userInfo.value.userImg}`;
  }
  return null;
});

const isDropdownOpen = ref(false);

const toggleDropdown = (event) => {
  event.stopPropagation();
  isDropdownOpen.value = !isDropdownOpen.value;
};

const closeDropdown = (event) => {
  if (!event.target.closest(".user-profile-dropdown")) {
    isDropdownOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", closeDropdown);
});

onUnmounted(() => {
  document.removeEventListener("click", closeDropdown);
});

const goToEditProfile = () => {
  router.push({ name: "mypage" });
  isDropdownOpen.value = false;
};

const goToUserProfile = () => {
  if (userInfo.value && userInfo.value.nickname) {
    router.push({
      name: "userProfile",
      params: { nickName: userInfo.value.nickname },
    });
    isDropdownOpen.value = false;
  } else {
    alert("사용자 정보를 불러올 수 없습니다.");
  }
};

const logout = async () => {
  try {
    await userStore.userLogout();
    router.push({ name: "login" });
  } catch (error) {
    console.error("로그아웃 중 오류 발생:", error);
    alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
};
</script>

<template>
  <div class="user-profile-dropdown">
    <div
      class="user-profile-button"
      @click="isLogin ? toggleDropdown($event) : router.push({ name: 'login' })"
    >
      <img
        v-if="isLogin && userImg"
        :src="userImg"
        alt="User Profile"
        class="user-image"
      />
      <FontAwesomeIcon v-else :icon="faUserCircle" class="user-icon" />
      <span class="user-name">
        {{ isLogin ? userNickname : "로그인하기" }}
      </span>
    </div>

    <transition name="dropdown">
      <div v-if="isLogin && isDropdownOpen" class="user-profile-menu">
        <a @click="goToUserProfile" class="menu-item profile-view">
          <span class="menu-text">프로필 보기</span>
        </a>
        <a @click="goToEditProfile" class="menu-item edit-view">
          <span class="menu-text">정보 수정</span>
        </a>
        <a @click="logout" class="menu-item logout">
          <span class="menu-text">로그아웃</span>
        </a>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.user-profile-dropdown {
  position: relative;
  display: flex;
  justify-content: flex-end;
  width: 100%;
  padding-right: 15px;
}

.user-profile-button {
  display: flex;
  align-items: center;
  padding: 6px 14px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 9999px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.user-profile-button:hover {
  background-color: rgba(255, 255, 255, 1);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.user-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
}

.user-icon {
  width: 32px;
  height: 32px;
  color: #4b5563;
  margin-right: 12px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  padding: 0 4px;
}

.user-profile-menu {
  position: absolute;
  right: 15px;
  top: 100%;
  margin-top: 8px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 50;
  overflow: hidden;
  width: auto;
  min-width: 120px;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 16px;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.menu-item:hover {
  background-color: #f3f4f6;
}

.menu-item.profile-view:hover {
  background-color: #e8f4fd;
  color: #2196f3;
}

.menu-item.edit-view:hover {
  background-color: #e8f5e9;
  color: #4caf50;
}

.menu-item.logout:hover {
  background-color: #ffebee;
  color: #f44336;
}

.menu-text {
  text-align: center;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
