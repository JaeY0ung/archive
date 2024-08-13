<script setup>
import { ref, computed } from "vue";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user";
import { useLeftNavigationStore } from "@/stores/leftNavigation";
import { useRouter } from "vue-router";

import HomeSvg from "@/common/icons/home.svg"
import MySvg from "@/common/icons/my.svg"
import SearchSvg from "@/common/icons/search.svg"
import OrderSvg from "@/common/icons/order.svg"
import UploadSvg from "@/common/icons/upload.svg"
import NotePixelSvg from "@/common/icons/notepixel.svg"
import RecordSvg from "@/common/icons/record.svg"

const router = useRouter();
const userStore = useUserStore();
const { isLogin, userInfo } = storeToRefs(userStore);
const leftNavigationStore = useLeftNavigationStore();
const { navVisibility } = storeToRefs(leftNavigationStore);

const authRequiredRoutes = ["mypage", "order", "sheetUpload", "multiRoomList", "recording"];

const pages = ref([
    { name: "main", title: "메인 페이지", src: HomeSvg },
    { name: "sheetSearch", title: "악보 검색", src: SearchSvg },
    { name: "order", title: "장바구니", src: OrderSvg },
    { name: "sheetUpload", title: "악보 업로드", src: UploadSvg },
    { name: "multiRoomList", title: "배틀하기", src: NotePixelSvg },
    // { name: "recording", title: "음악 녹음", src: RecordSvg },
]);

const adminPages = ref([
    { name: "manageSheet", title: "악보 관리", src: UploadSvg },
    { name: "manageSong", title: "곡 관리", src: UploadSvg },
    { name: "adminUploadSheet", title: "악보 일괄 업로드", src: UploadSvg },
]);

const visiblePages = computed(() => {
    return pages.value.filter(page => 
        !authRequiredRoutes.includes(page.name) || isLogin.value
    );
});

</script>

<template>
    <nav class="left-nav-container flex flex-shrink-0" >
        <div class="h-20"></div>
        <template v-for="page in visiblePages" :key="page.name">
            <div class="flex contents-center items-center w-full h-[50px] mt-5 overflow-hidden">
                <RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav" class="flex items-center w-full h-full">
                    <div class="flex w-[24px] h-[24px] ml-[20px]"> <img :src="page.src" :alt="page.title" /> </div>
                    <div class="flex justify-center flex-shrink ml-auto mr-auto text-nowrap">{{ page.title }}</div>
                </RouterLink>
            </div>
        </template>

        <div class="h-10"></div>
        
        <template v-if="userInfo && userInfo.role == 'ROLE_ADMIN'">
            <div class="flex flex-shrink contents-center items-center w-full h-[25px] mt-5 overflow-hidden">
                <div class="flex flex-shrink m-3 text-nowrap text-sm">관리자 페이지</div>
            </div>
            <hr>
            <template v-for="page in adminPages" :key="page.name">
                <div class="flex flex-shrink contents-center items-center w-full h-[50px] mt-5 overflow-hidden">
                    <RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav" class="flex items-center w-full h-full">
                        <div class="flex w-[24px] h-[24px] ml-[20px]"> <img :src="page.src" :alt="page.title" /> </div>
                        <div class="flex flex-shrink ml-auto mr-auto text-nowrap">{{ page.title }}</div>
                    </RouterLink>
                </div>
            </template>
        </template>
    </nav>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap");

.left-nav-container {
    background: linear-gradient(135deg, rgba(255, 182, 193, 0.95), rgba(133, 193, 233, 0.95));
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border-top-right-radius: 15px;
    border-bottom-right-radius: 15px;
    font-family: "Poppins", sans-serif;
    border: 1px solid rgba(255, 255, 255, 0.2); 
}
    
.left-nav-container > div > a:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateX(3px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.auth-button {
    margin-top: 22px;
    padding: 10px;
    color: #ffffff;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: center;
    font-weight: 600;
    font-size: 0.85rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.login-button {
    background-color: rgba(52, 152, 219, 0.9);
}

.logout-button {
    background-color: rgba(220, 20, 60, 0.9);
}

.auth-button:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
}

.login-button:hover {
    background-color: rgba(41, 128, 185, 1);
}

.logout-button:hover {
    background-color: rgba(178, 34, 34, 1);
}

:deep(svg) {
    fill: rgba(0, 0, 0, 0.8);
    width: 20px;
    height: 20px;
}
</style>