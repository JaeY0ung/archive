<script setup>
import { getTitleByLen } from '@/util/string-util'

defineProps({
    sheet: Object
})


</script>

<template>
    <div class="card">
        <!-- {{ sheet }} -->
        <div class="img">
            <img class="song-img" :src="sheet.imageUrl" alt="원본 곡 이미지" >
        </div>

        <div class="sheet-info">
            <div>
                <RouterLink :to="{ name: 'sheetDetail', params: { sheetId :  sheet.id} }">
                    <span>
                        <p class="bold" style="font-size: 18px;">{{ getTitleByLen(sheet.title, 10) }}</p>
                    </span>
                </RouterLink>
                <div>
                    <p class="medium" style="font-size: 12px;">{{ sheet.songComposer }}</p>
                </div>
            </div>
            <div>
                <div>
                    <p class="regular" style="font-size: 12px;">티어 {{ sheet.level }}</p>
                </div>
                <div>
                    <p class="medium" style="font-size: 12px;">업로더 {{ sheet.uploaderNickname }}</p>
                </div>
            </div>
        </div>

    </div>
</template>

<style scoped>

.card {
    margin: 5px;
    padding: 5px;

	display: flex;
    flex-direction: row;
    justify-content: start;

	background-color: rgb(255, 255, 255);
	box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);
}

.img {
    height: 80px;
    width: 80px;
    display: flex;
    justify-content: center;
}

.sheet-info {
    min-width: 150px;
}

.sheet-info > * {
    padding: 5px 0;
    font-size: 5vw;
}

.card > * {
    flex: 1;
    margin:3px;
}
</style>