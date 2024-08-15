<script setup>
import { updateAllUsersScore } from '@/api/admin';

const updateAllUsersScoreByClick = () => {
    updateAllUsersScore(
        (res) => {
            alert("업데이트 완료!")
        }
    )
}
</script>

<template>
    <div>
        <button class="btn btn-primary" @click="updateAllUsersScoreByClick">모든 유저의 점수 업데이트</button>
    </div>
</template>

<style scoped>

</style>