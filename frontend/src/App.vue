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
    <div class="w-screen h-screen bg-gradient-to-br from-yellow-100 via-pink-200 to-blue-200">
        <div class="absolute flex h-screen bg-gradient-to-br" :class="{ 'left-0' : leftNavigationStore.navVisibility, 'left-[-200px]': !leftNavigationStore.navVisibility }"  style="transition: left 0.5s ease;">
            <div>
                <LeftNavigation class="h-screen w-[200px]"/>
            </div>

            <div :class="{'w-screen' : !leftNavigationStore.navVisibility, 'w-screen-except-nav' : leftNavigationStore.navVisibility}">
                <!-- 위 -->
                <div style="height:50px; display:flex; justify-content:start;">

                    <div class="h-[50px] flex items-center cursor-pointer">
                        <Hamburger class="w-[50px] h-50px max-h-[50px] flex justify-center items-center m-[5px]" @click="leftNavigationStore.changeNavVisibility"/>
                        <RouterLink class="h-[25px] max-h-[50px] flex justify-center items-center" :to="{ name : 'main'}">
                            <img class="mr-2" :src="require('@/assets/img/piano.svg')" alt="피아노">
                            <div class="text-xl">악카이브</div>
                        </RouterLink>
                    </div>
                    
                    <MyProfile style="height:40px;"/>
                </div>

                <!-- 아래 -->
                <div class="router-view-parent" @click="leftNavigationStore.closeNav">
                    <RouterView :key="route.fullPath"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.w-screen-except-nav {
    width: calc(100vw - 200px)
}
.router-view-parent {
    margin: 0 20px;
    height: calc(100% - 150px);
}

</style>
