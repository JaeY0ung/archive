<script setup>
import Home from "@/common/icons/Home.vue";
import My from "@/common/icons/My.vue";
import Search from "@/common/icons/Search.vue";
import Order from "@/common/icons/Order.vue";
import Upload from "@/common/icons/Upload.vue";
import NotePixel from "@/common/icons/NotePixel.vue";

import { ref, computed } from "vue";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user";
import { useLeftNavigationStore } from "@/stores/leftNavigation";
import { useRouter } from "vue-router";
import Record from "@/common/icons/Record";

const router = useRouter();
const userStore = useUserStore();
const { isLogin } = storeToRefs(userStore);
const leftNavigationStore = useLeftNavigationStore();
const { navVisibility } = storeToRefs(leftNavigationStore);

const pages = ref([
    { name: "main", title: "메인 페이지" },
    { name: "mypage", title: "마이페이지" },
    { name: "sheetSearch", title: "악보 검색" },
    { name: "order", title: "장바구니" },
    { name: "sheetUpload", title: "악보 업로드" },
    { name: "multiRoomList", title: "배틀하기" },
  {name: "recording", title: "음악 녹음"},
]);

const goLogout = async () => {
    try {
        await userStore.userLogout();
        leftNavigationStore.closeNav();
        router.push({ name: "main" });
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
const authButtonClass = computed(() => isLogin.value ? 'logout-button' : 'login-button');
</script>

<template>
    <nav class="left-nav-container">
        <br /><br /><br /><br /><br /><br /><br />

        <div class="nav-main">
            <div v-for="page in pages" :key="page.name">
                <RouterLink :to="{ name: page.name }" @click="leftNavigationStore.closeNav">
                    <div class="icon-container">
                        <Home v-if="page.name === 'main'" />
                        <My v-else-if="page.name === 'mypage'" />
                        <Search v-else-if="page.name === 'sheetSearch'" />
                        <Record v-else-if="page.name === 'recording'" />
                        <Upload v-else-if="page.name === 'sheetUpload'" />
                        <NotePixel v-else-if="page.name === 'multiRoomList'" />
                        <Order v-else-if="page.name === 'order'" />
                    </div>
                    <span>{{ page.title }}</span>
                </RouterLink>
            </div>
            <div 
                @click="isLogin ? goLogout() : goLogin()" 
                class="auth-button"
                :class="authButtonClass"
            >
                <span>{{ authButtonText }}</span>
            </div>
        </div>
    </nav>
</template>


<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap");

.left-nav-container {
    background: linear-gradient(135deg, rgba(255, 182, 193, 0.95), rgba(133, 193, 233, 0.95));
    backdrop-filter: blur(10px);
    border-top-right-radius: 15px;
    border-bottom-right-radius: 15px;
    padding: 22px;
    font-family: "Poppins", sans-serif;
    box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.nav-main {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.nav-main > div > a {
    display: flex;
    align-items: center;
    text-decoration: none;
    color: rgba(0, 0, 0, 0.8);
    padding: 10px 12px;
    border-radius: 8px;
    transition: all 0.3s ease;
    font-weight: 500;
    font-size: 0.9rem;
    background-color: rgba(255, 255, 255, 0.2);
}

.nav-main > div > a:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateX(3px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.icon-container {
    width: 24px;
    height: 24px;
    margin-right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
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