<script setup>
import axios from 'axios';
import { useRoute } from 'vue-router'
import SheetPage from '@/common/sheet/SheetPage.vue';
import SheetPlayNavigation from '@/common/sheet/SheetPlayNavigation.vue';
import { ref } from 'vue';
import BigSheetCard from '@/common/sheet/BigSheetCard.vue';
import { localAxios } from '@/util/http-common';
const route = useRoute();
const local = localAxios();

const sheet = ref({})
const searchSheetDetailById = () => {
    local.get("/sheets/" + route.params.sheetId)
        .then(({data}) => {
            sheet.value = data;
            sheet.value.imageUrl =`data:image/jpeg;base64,${data.songImg}`
            console.log(sheet.value)
        }).catch((err) => {
            alert(err)
        })
}

searchSheetDetailById()

const isPlay = ref("stop")

</script>

<template>
    <div class="flex justify-between flex-margin h-full">
        <div class="w-[49%] h-full bg-white/50 rounded-xl">
            <BigSheetCard :sheet/>
        </div>

        <div class="flex flex-col gap-5 w-[49%] h-full p-3 bg-white/50 rounded-xl">
            <SheetPlayNavigation class="flex-none h-[30px]" @play="isPlay='play'" @pause="isPlay='pause'" @stop="'stop'"/>
            <div class="bg-black rounded-xl h-full" ></div>
            <!-- <SheetPage class="rounded-xl h-full" :isPlay /> -->
        </div>
    </div>
</template>

<style scoped>
</style>