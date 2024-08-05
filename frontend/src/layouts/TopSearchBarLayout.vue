<script setup>
import SearchBar from '@/common/search-bar/CommonSearchBar.vue'
import { ref } from 'vue';
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const keyword = ref("");

const transferKeyword = (k) => {
    keyword.value = k // 같은 페이지일 때는 RouterView에 직접 값 전달.
    if (route.name !== "sheetSearch") {
        //! TODO : 다른 페이지에서 검색 키워드 입력하여 이동 시, SheetSearchView.vue의 props로 값이 전달되지 않음.
        router.push({ name: 'sheetSearch', params: { keyword: keyword.value } });
    }
};

</script>

<template>
    <div class="h-full w-full">
        <SearchBar class="search-bar " @send-keyword="transferKeyword"/>
        <div style="height: 30px;"></div>
        <RouterView class="h-full w-full" :keyword="keyword" />
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