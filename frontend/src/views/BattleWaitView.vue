<script setup>
import Sheet from '@/common/sheet/Sheet.vue';


</script>

<template>
    <div>
        <!-- <h1 style="color: white;">대기 페이지</h1> -->
        <Sheet v-on:measure-changed="mChange" :width="'45vw'" height="'100vh'"/>
    </div>
</template>

<style scoped>

</style>