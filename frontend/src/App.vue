<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { useLeftNavigationStore } from '@/stores/leftNavigation'
import Hamburger from './common/icons/Hamburger.vue';
import MyProfile from './common/navigation/MyProfile.vue';
import LeftNavigation from './common/navigation/LeftNavigation.vue';
import CommonSearchBar from './common/search-bar/CommonSearchBar.vue';

const leftNavigationStore = useLeftNavigationStore();
const route = useRoute();

</script>

<template>
    <div class="w-screen h-screen bg-gradient-to-br from-yellow-100 via-pink-200 to-blue-200">
        <div class="flex w-full h-full">
            <LeftNavigation class="flex flex-col flex-shrink-1 h-screen" :class="{ 'w-[200px]' : leftNavigationStore.navVisibility, 'w-[0px]': !leftNavigationStore.navVisibility }"
                style="transition: width 0.5s ease;"
            />
            <div class="flex flex-col flex-grow h-full">
                <div class="flex flex-row align-middle w-full h-[60px] items-center"> 
                    <div class="flex-1 flex h-full items-center cursor-pointer">
                        <Hamburger class="w-[50px] h-50px max-h-[50px] flex justify-center items-center m-[5px]" @click="leftNavigationStore.changeNavVisibility"/>
                        <RouterLink class="h-[25px] max-h-[50px] flex justify-center items-center" :to="{ name : 'main'}">
                            <img class="mr-2" :src="require('@/assets/img/piano.svg')" alt="피아노">
                            <div class="text-xl">악카이브</div>
                        </RouterLink>
                    </div>

                    <div class="flex-1">
                        <CommonSearchBar v-if="['userProfile','sheetDetail','sheetSearch','main'].includes(route.name)"/>
                    </div>
                    
                    <div class="flex-1">
                        <MyProfile/>
                    </div>
                </div>

                <div class="flex flex-grow flex-shrink-1  pl-[55px] pr-[15px]" @click="leftNavigationStore.closeNav">
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


</style>
