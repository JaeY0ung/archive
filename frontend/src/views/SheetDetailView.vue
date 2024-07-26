<script setup>
import axios from 'axios';
import { useRoute } from 'vue-router'
import SheetPage from '@/common/sheet/SheetPage.vue';
import SheetPlayNavigation from '@/common/sheet/SheetPlayNavigation.vue';
import RecordButton from '@/common/RecordButton.vue';
import { ref } from 'vue';

const route = useRoute();

const sheet = ref({})
const searchSheetDetailById = () => {
    axios.get("http://localhost:8080/sheets/" + route.params.sheetId)
        .then(({data}) => {
            sheet.value = data;
            sheet.value.imageUrl =`data:image/jpeg;base64,${data.songImg}`
        }).catch((err) => {
            alert(err)
        })
}

searchSheetDetailById()

const isPlay = ref("stop")

</script>

<template>
    <div class="flex flex-margin">
        <div class="left w-1/2">
            <div class="sheet-detail">
                <div style="width: 100px; height: 100px; background-color: aliceblue;">
                    <img :src="sheet.imageUrl" alt="악보 이미지">
                </div>
                <div>
                    <div>제목: {{ sheet.title }}</div>
                    <div>작곡가: {{ sheet.songComposer }}</div>
                    <div>업로더: {{ sheet.uploaderNickname }}</div>
                    <div>가격: {{ sheet.price }}</div>
                    <div>
                        티어: {{ sheet.level }}
                        <img v-if="sheet.level === 1" src="@/assets/img/level/bronze.svg" alt="Bronze">
                        <img v-else-if="sheet.level === 2" src="@/assets/img/level/silver.svg" alt="Silver">
                        <img v-else-if="sheet.level === 3" src="@/assets/img/level/gold.svg" alt="Gold">
                    </div>
                    <div>view: {{ sheet.viewCount }}</div>
                    <div><RouterLink :to="{name: 'waitBattle'}">곡 연습하러 가기</RouterLink></div>
                </div>
            </div>
        </div>

        <div>
            <SheetPlayNavigation @play="isPlay='play'" @pause="isPlay='pause'" @stop="'stop'"/>
            <SheetPage :isPlay height="80vh" width="45vw"/>
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