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
const { isLogin } = storeToRefs(userStore);
const leftNavigationStore = useLeftNavigationStore();
const { navVisibility } = storeToRefs(leftNavigationStore);

const pages = ref([
    { name: "main", title: "메인 페이지", src: HomeSvg },
    { name: "mypage", title: "마이페이지" ,src:MySvg},
    { name: "sheetSearch", title: "악보 검색",src:SearchSvg },
    { name: "order", title: "장바구니",src:OrderSvg },
    { name: "sheetUpload", title: "악보 업로드",src:UploadSvg },
    { name: "multiRoomList", title: "배틀하기" ,src:NotePixelSvg},
    { name: "recording", title: "음악 녹음",src:RecordSvg},
]);

const visiblePages = computed(() => {
    if (isLogin.value) {
        return pages.value;
    } else {
        return pages.value.filter((page) => ["main", "sheetSearch"].includes(page.name));
    }
});

const goLogout = async () => {
    try {
        await userStore.userLogout();
        leftNavigationStore.closeNav();
        router.push({ name: "login" });
    } catch (error) {
        console.error("로그아웃 중 오류 발생:", error);
        alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
};

const goLogin = () => {
    router.push({ name: "login" });
    leftNavigationStore.closeNav();
};

const authButtonText = computed(() => (isLogin.value ? "로그아웃" : "로그인"));
const authButtonClass = computed(() => (isLogin.value ? "logout-button" : "login-button"));
</script>

<template>
    <nav class="left-nav-container flex flex-shrink-0" >
        <br /><br /><br /><br /><br /><br /><br />
        <div v-for="page in pages" :key="page.name" class="flex contents-center items-center w-full h-[50px] mt-5 ">
            <RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav" class="flex items-center w-full h-full">
                <div class="flex w-[24px] h-[24px] ml-[20px]">
                    <img :src="page.src" />
                </div>
                <div class="flex flex-shrink ml-auto mr-auto overflow-hidden text-nowrap">{{ page.title }}</div>
            </RouterLink>
        </div>
        
        <div 
            @click="isLogin ? goLogout() : goLogin()" 
            class="auth-button"
            :class="authButtonClass"
        >
            <span>{{ authButtonText }}</span>
        </div> 
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
