<script setup>
import { ref } from 'vue';
import LeftNavigation from '@/common/navigation/LeftNavigation.vue'
import MyProfile from './common/navigation/MyProfile.vue';
import Hamburger from '@/common/icons/Hamburger.vue'
import { useLeftNavigationStore } from '@/stores/leftNavigation'

const leftNavigationStore = useLeftNavigationStore();

</script>

<template>
  <div>
    <div class="left-and-hamburger" :class="{ 'show' : leftNavigationStore.navVisibility }">
      <LeftNavigation class="left-nav"/>
      <Hamburger class="hamburger" @click="leftNavigationStore.changeNavVisibility"/>
    </div>
    <div style="height:50px; display: flex; justify-content:end;">
      <MyProfile style="height:40px;"/>
    </div>
    <RouterView @click="leftNavigationStore.closeNav" />
  </div>
</template>

<style scoped>
.left-and-hamburger {
  display: flex;
  position: fixed;
  z-index: 3;
  left: -200px; /* 애니메이션 시작 위치 */
  transition: left 0.5s ease;
  height: 100vh;
}

.left-nav {
  height: 100vh;
  width: 200px; /* TODO : 아이패드, 모바일, 데탑 마다 각각 고정 설정 */
}

.hamburger {
  cursor: pointer;
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 5px;
}

.show {
  left: 0; /* 애니메이션 종료 위치 */
}

.container {
  width: 100vw;
  margin-top: 20px;
  min-height: calc(100vh - 70px);
}
</style>
