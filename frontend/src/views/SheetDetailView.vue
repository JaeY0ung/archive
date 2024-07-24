<script setup>
import axios from 'axios';
import { useRoute } from 'vue-router'
import Sheet from '@/common/sheet/Sheet.vue';
import RecordButton from '@/common/RecordButton.vue';
import { ref } from 'vue';
const route = useRoute();

const searchSheetDetailById = () => {
    axios.get("http://localhost:8080/sheets/" + route.params.sheetId)
        .then(({data}) => {
            console.log(data);
            sheet.value = data;
        }).catch((err) => {
            alert(err)
        })
}

const sheet = ref({})
searchSheetDetailById()

</script>

<template>
    <div class="flex flex-margin">
        <div class="left w-1/2">
            <div class="sheet-detail">
                <div style="width: 100px; height: 100px; background-color: aliceblue;">
                    <img :src="`C:/SSAFY/Archive/file/sheet-img/${sheet.song?.imgName}`" alt="악보 이미지">
                </div>
                <div>
                    <div>제목: {{ sheet.title }}</div>
                    <div>작곡가: {{ sheet.song?.composer }}</div>
                    <div>업로더: {{ sheet.uploader?.nickname }}</div>
                    <div>가격: {{ sheet.price }}</div>
                    <div>티어: {{ sheet.level }}</div>
                    <div>view: {{ sheet.viewCount }}</div>
                    <div><RouterLink :to="{name: 'waitBattle'}">곡 연습하러 가기</RouterLink></div>
                </div>
            </div>

        </div>
        <div>
            <Sheet v-on:measure-changed="mChange" :width="'45vw'" height="'80vh'"/>
            <RecordButton ref="recordComponent" :triggerSplit="triggerSplit"/>
        </div>
    </div>
</template>

<style scoped>
.left {
    background-color: rgb(255, 255, 255);
    border-radius: 15px;
}

.sheet-detail {
    background-color: rgb(218, 250, 250);
    border-radius: 15px;
    display: flex;
}

.flex-margin > div {
    padding:px
}
</style>