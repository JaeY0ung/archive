<script setup>
import { ref } from 'vue';
import { localAxios } from '@/util/http-common';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import { searchSheetsByFilter } from '@/api/sheet';
const local = localAxios();
const popularSheets = ref([]); // 인기 악보 리스트


const getPopularsheets = async () => {

    searchSheetsByFilter(
        { sort: "POPULAR" },
        ({ data }) => {
			popularSheets.value = data;
			popularSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지'); // TODO: songImg가 없으면 기본 로고로.
		}
    )
}
getPopularsheets();
</script>

<template>
    <div>
        <h1 style="color: white;">대기 페이지</h1>
    </div>
    <div id="card-box">
        <div class="card card-compact h-64" v-for="popularSheet in popularSheets" :key="popularSheet.id" :popularSheet="popularSheet">
            <figure>
                <img
                :src="popularSheet.imageUrl"
                :alt="popularSheet.songImg" />
            </figure>
            <div class="card-body">
                <h2 class="card-title">{{ popularSheet.title }}</h2>
                <p>{{ popularSheet.songComposer }}</p>
                <div class="card-actions justify-end">
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

img{
    width: 100%;
    height: 100%;
}

#card-box{
    display: flex;
    flex-wrap: nowrap;
    justify-content: space-around;
    margin-top: 20px;
}

.card{
    width: 19vw;
    height: 58vh;
    cursor: pointer;
}

#card-box:has(div:hover) > div:not(:hover){
    filter: blur(4px)
}

#card-box:has(div:hover) > div:hover{
    width: 20vw;
    height: 59vh;
}

div{
    transition: all 150ms ease-in-out;
}


</style>