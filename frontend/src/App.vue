<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import LeftNavigation from '@/common/navigation/LeftNavigation.vue'
import MyProfile from './common/navigation/MyProfile.vue';
import Hamburger from '@/common/icons/Hamburger.vue'
import { useLeftNavigationStore } from '@/stores/leftNavigation'

const leftNavigationStore = useLeftNavigationStore();
const route = useRoute();
</script>

<template>
    <div class="flex flex-1 h-screen bg-gradient-to-br from-yellow-100 via-pink-200 to-blue-200">
        <div class="left-and-hamburger" :class="{ 'show' : leftNavigationStore.navVisibility }">
            <LeftNavigation class="left-nav"/>
            <div>
                <!-- 위 -->
                <div style="height:50px; display:flex; justify-content:end;">
                    <div class="h-[50px] flex justify-center items-center">
                        <Hamburger class="hamburger" @click="leftNavigationStore.changeNavVisibility"/>
                        <RouterLink class="logo" :to="{ name : 'main'}">
                            <img class="mr-2" :src="require('@/assets/img/piano.svg')" alt="피아노">
                            <div class="text-xl">악카이브</div>
                        </RouterLink>
                    </div>
                    <MyProfile style="height:40px;"/>
                </div>

                <!-- 아래 -->
                <div class="router-view-parent">
                    <RouterView @click="leftNavigationStore.closeNav" :key="route.fullPath"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.left-and-hamburger {
    display: flex;
    position: absolute;
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
    max-height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 5px;
}

.logo {
    cursor: pointer;
    height: 25px;
    max-height: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.show {
  left: 0; /* 애니메이션 종료 위치 */
}

.container {
    width: 100vw;
    margin-top: 20px;
    min-height: calc(100vh - 70px);
}

.router-view-parent {
    margin: 0 20px;
    height: calc(100% - 100px);
}

</style>
