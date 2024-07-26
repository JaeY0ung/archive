<script setup>
import SearchBar from '@/common/search-bar/CommonSearchBar.vue'
</script>

<template>
    <div>
        <SearchBar class="search-bar "/>
        <RouterView style="width:100vw;"/>
    </div>
</template>

<style scoped>
.search-bar {
    min-width: 20vw; 
    position: fixed; 
    top: 10px; 
    left: 40vw; 
    z-index: 5;
}
</style>