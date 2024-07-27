<script setup>
import Profile from '@/common/icons/Profile.vue'
import { RouterLink } from 'vue-router';
defineProps({
    username : {type: String, required: false, default: "김싸피"}
})
</script>

<template>
    <div class="profile-div">
        <div style="display: flex; align-items: center; margin: 0 20px;">
            <!-- 로그인 완료 시 -->
            <!-- <Profile style="display: flex; align-items: center; margin-right: 5px;"/> -->
            <!-- <span>{{ username }} 님</span> -->

            <!-- 비 로그인 시 -->
            <RouterLink :to="{ name : 'login' }">
                <span>로그인하세요.</span>
            </RouterLink>
        </div>
    </div>
</template>

<style scoped>
.profile-div {
    position: fixed; 
    z-index: 10;
    height: 40px; 
    margin: 10px; 
    display: flex; 
    justify-content: end; 
    width:fit-content;
}
</style>