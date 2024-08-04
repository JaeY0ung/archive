<script setup>

import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user"
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import { usePlayStore } from "@/stores/play";

const props = defineProps({
    roomId: Number
})

const canLeaveSite = ref(false);

const userStore = useUserStore();

const router = useRouter();
const route = useRoute();

const playStore = usePlayStore();

let eventSource;

let score = ref(0);
let email = ref("");

const user = userStore

// TODO 악보 고르기
// TODO 친구 초대
// TODO 나가기 버튼

onBeforeRouteLeave( async (to, from, next) => {
    console.log('이동할 라우트:', to);
    console.log('현재 라우트:', from);

    if(to.name == "play" && from.name == "wait"){
        next();
    }else{
        if(confirm('방을 나가시겠습니까?\n메인 페이지로 돌아가게 됩니다. ')){
            // delete axios
            await playStore.exitRoom(props.roomId);
            window.location.href = "http://localhost:3000/pianosaurus"
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