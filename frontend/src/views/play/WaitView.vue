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
    <div id="card-box">
        <div class="card card-compact h-64"> <!-- W-72 -->
            <figure>
                <img
                src="https://newsimg.hankookilbo.com/cms/articlerelease/2021/01/17/fb3de445-6b62-49df-9dab-0fa8efa9cc8c.jpg"
                alt="car!" />
            </figure>
            <div class="card-body">
                <h2 class="card-title">인기 악보</h2>
                <p>인기 악보들을 플레이하세요!</p>
                <div class="card-actions justify-end">
                </div>
            </div>
        </div>
        <div class="card card-compact h-64">
            <figure>
                <img
                src="https://img.hankyung.com/photo/202405/01.36880695.1.jpg"
                alt="car!" />
            </figure>
            <div class="card-body">
                <h2 class="card-title">이전 악보</h2>
                <p>자주 플레이했던 악보를 플레이하세요!</p>
                <div class="card-actions justify-end">
                </div>
            </div>
        </div>
        <div class="card card-compact h-64">
            <figure>
                <img
                src="https://images.saramingig.co.kr/product/F/0/X/F0Xdpaep3WJVpIR.jpeg/o2j/resize/900"
                alt="car!" />
            </figure>
            <div class="card-body">
                <h2 class="card-title">티어 악보</h2>
                <p>현재 티어와 맞는 악보를 플레이하세요!</p>
                <div class="card-actions justify-end">
                </div>
            </div>
        </div>
        <div class="card card-compact h-64">
            <figure>
                <img
                src="https://images.saramingig.co.kr/product/i/D/e/iDepWckeH5rM57A.jpeg/o2j/resize/900"
                alt="car!" />
            </figure>
            <div class="card-body">
                <h2 class="card-title">랜덤 악보</h2>
                <p>랜덤한 악보를 플레이하세요!</p>
                <div class="card-actions justify-end">
                </div>
            </div>
        </div>
    
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
}

</style>
