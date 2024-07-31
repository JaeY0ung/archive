<script setup>
import SearchBar from '@/common/search-bar/CommonSearchBar.vue'
import {useRouter} from "vue-router";
const router = useRouter();
const searchSheetsByKeyword = (keyword)  => {
  router.push({ name: 'sheetSearch', query: { keyword: keyword } });
};
</script>

<template>
    <div class="h-full w-full">
        <SearchBar class="search-bar " @send-keyword="searchSheetsByKeyword"/>
        <div style="height: 30px;"></div>
        <RouterView class="h-full w-full"/>
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