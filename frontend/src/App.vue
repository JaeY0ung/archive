<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useLeftNavigationStore } from "@/stores/leftNavigation";
import Hamburger from "./common/icons/Hamburger.vue";
import MyProfile from "./common/navigation/MyProfile.vue";
import LeftNavigation from "./common/navigation/LeftNavigation.vue";
import CommonSearchBar from "./common/search-bar/CommonSearchBar.vue";
import NotificationBubble from "@/common/alert/NotificationBubble.vue";
import { storeToRefs } from "pinia";

const leftNavigationStore = useLeftNavigationStore();
const { changeNavVisibility, openNav, closeNav } = leftNavigationStore;
const { navVisibility } = storeToRefs(leftNavigationStore);

const route = useRoute();
</script>

<template>
    <div class="h-screen w-screen overflow-hidden bg-gradient-to-b from-[#EDF4FF] to-[#E1EDFF]">
        <div class="flex h-full w-full">
            <LeftNavigation
                class="flex h-full flex-shrink-0 flex-col"
                :class="{ 'w-[200px]': navVisibility, 'w-0': !navVisibility }"
                style="transition: width 0.5s ease"
            />
            <div class="flex flex-grow flex-col min-h-0 overflow-hidden">
                <!-- 위 -->
                <div class="flex h-[60px] w-full flex-row items-center">
                    <div class="flex h-full flex-1 items-center cursor-pointer">
                        <Hamburger
                            class="m-[5px] flex h-[50px] w-[50px] items-center justify-center"
                            @click="changeNavVisibility"
                            @touchend="changeNavVisibility"
                        />
                        <RouterLink
                            class="flex h-[25px] items-center justify-center"
                            :to="{ name: 'main' }"
                        >
                            <img
                                height="50"
                                width="100"
                                class="mr-2"
                                :src="require('@/assets/img/common/archive_logo_img.png')"
                                alt="피아노"
                            />
                        </RouterLink>
                    </div>

                    <div
                        class="flex-1"
                        v-if="
                            ['userProfile', 'sheetDetail', 'sheetSearch', 'main'].includes(
                                route.name
                            )
                        "
                    >
                        <CommonSearchBar />
                    </div>

                    <div class="flex-1">
                        <MyProfile />
                    </div>
                </div>
                <!-- 실제 뷰 -->
                <div
                    class="h-[90%] overflow-hidden px-[55px] pl-[55px] pt-[20px] pb-[20px] scrollbar-hide"
                    @click="closeNav"
                >
                    <RouterView :key="route.fullPath" />
                </div>
            </div>
        </div>
        <NotificationBubble />
    </div>
</template>

<style>
html,
body {
    height: 100%;
    overflow: hidden;
}

.scrollbar-hide {
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
</style>
