<script setup>
import Profile from '@/common/icons/Profile.vue'
defineProps({
    username : {type: String, required: false, default: "김싸피"}
})
</script>

<template>
    <div style="position: fixed; height: 40px; margin: 10px; display:flex; justify-content:end; width:fit-content;">
        <Profile style="display: flex; align-items: center; margin-right: 5px;"/>
        <div style="display: flex; align-items: center;">{{ username }} 님</div>
    </div>
</template>

<style scoped>

</style>