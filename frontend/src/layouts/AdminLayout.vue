<script setup>

</script>

<template>
    <div class="w-full m-auto">
        <div class="text-3xl text-center">관리자 페이지</div>
        <div class="flex flex-row gap-3">
            <!-- 향후 네비게이션 바에 추가 예정 -->
            <RouterLink :to="{ name : 'manageSheet'}">악보 관리</RouterLink>
            <RouterLink :to="{ name : 'manageSong'}">곡 관리</RouterLink>
            <RouterLink :to="{ name : 'adminUploadSheet'}">악보 일괄 업로드</RouterLink>
            
        </div>
        <RouterView/>
    </div>
</template>

<style scoped>

</style>