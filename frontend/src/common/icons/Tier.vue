<script setup>
defineProps({
    level: Number
})
</script>

<template>
    <div class="flex">
        <img v-if="level == 1" :src="require('@/assets/img/level/bronze.svg')" alt="">
        <img v-else-if="level == 2" :src="require('@/assets/img/level/silver.svg')" alt="">
        <img v-else-if="level == 3" :src="require('@/assets/img/level/gold.svg')" alt="">
        <img v-else-if="level == 4" :src="require('@/assets/img/level/platinum.svg')" alt="">
        <img v-else-if="level == 5" :src="require('@/assets/img/level/diamond.svg')" alt="">
    </div>
</template>

<style scoped>

</style>