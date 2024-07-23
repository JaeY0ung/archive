<script setup>
import Profile from '@/common/icons/Profile.vue'
import { RouterLink } from 'vue-router';
defineProps({
    username : {type: String, required: false, default: "김싸피"}
})
</script>

<template>
    <div style="position: fixed; height: 40px; margin: 10px; display:flex; justify-content:end; width:fit-content;">
        <Profile style="display: flex; align-items: center; margin-right: 5px;"/>
        <div style="display: flex; align-items: center;">
            <!-- 로그인 완료 시 -->
            <!-- <span>{{ username }} 님</span> -->

            <!-- 비 로그인 시 -->
            <RouterLink :to="{ name : 'login' }">
                <span>로그인하세요.</span>
            </RouterLink>
        </div>
    </div>
</template>

<style scoped>

</style>