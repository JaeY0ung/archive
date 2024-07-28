<script setup>

import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user"
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, onBeforeRouteLeave } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";

const canLeaveSite = ref(false);

const userStore = useUserStore();

const router = useRouter();

let eventSource;

let score = ref(0);
let email = ref("");

const user = userStore




onBeforeRouteLeave((to, from, next) => {
    console.log('이동할 라우트:', to);
    console.log('현재 라우트:', from);

    if(to.name == "battle" && from.name == "waitBattle"){
        next();
    }else{
        if(confirm('방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. wait')){
            next();
        }else{
            next(false);
        }
    }
        
})




</script>

<template>
    <div>
        <h1 style="color: white;">대기 페이지</h1>
    </div>
</template>

<style scoped>

</style>