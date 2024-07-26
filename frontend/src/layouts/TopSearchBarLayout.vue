<script setup>
import SearchBar from '@/common/search-bar/CommonSearchBar.vue'
</script>

<template>
    <div>
        <SearchBar class="search-bar "/>
        <div style="height: 30px;"></div>
        <RouterView style="width:100%;"/>
    </div>
</template>

<style scoped>
.search-bar {
    min-width: 20vw;
    position: fixed; 
    top: 10px; 
    left: 40vw; 
    z-index: 10;
}
</style>