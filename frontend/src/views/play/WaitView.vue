<script setup>

import { userConfirm, findById, tokenRegeneration, logout } from "@/api/user"
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRoute, useRouter, onBeforeRouteLeave } from 'vue-router';
import { useUserStore } from '@/stores/user';
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import { usePlayStore } from "@/stores/play";

const props = defineProps({
    roomId: String
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

    if(to.name == "play" || to.name == "popular"){
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
        <RouterView />
    </div>
</template>

<style scoped>

#card-box{
    display: flex;
    flex-wrap: nowrap;
    justify-content: space-around;
    margin-top: 20px;
}

.card{
    width: 19vw;
    height: 58vh;
    cursor: pointer;
}

#card-box:has(div:hover) > div:not(:hover){
    filter: blur(4px)
}

#card-box:has(div:hover) > div:hover{
    width: 20vw;
    height: 59vh;
}

div{
    transition: all 150ms ease-in-out;
}


</style>
