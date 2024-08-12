<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { registerSheet } from "@/api/sheet";
import { searchSongsByFilter } from "@/api/song";
import SongRegisterModal from "@/common/modal/SongRegisterModal.vue";
import { tierInfo } from "@/util/tier-info";
import SmallSongCard from "@/common/sheet/SmallSongCard.vue";

const router = useRouter();

const keyword = ref("");
const fileInput = ref(null);
const modalVisibility = ref(false);
const songs = ref([
    {
        id: "",
        title: "",
        composer: "",
        genreTitle: "",
        img: "",
        imageUrl: "",
    },
]);

const selectedSong = ref({
    id: "",
    title: "",
    composer: "",
    genreTitle: "",
    img: "",
    imageUrl: "",
});

const fileInfo = ref({
    file: "",
    title: "",
    level: "",
});

// 파일이 바뀔 때마다 파일 ref값 변경
const handleFileChange = (event) => {
    if (!["audio/mid", "audio/midi"].includes(event.target.files[0].type)) {
        fileInput.value.value = "";
        alert(".mid 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.file = event.target.files[0];
};

const searchSongsByKeyword = async () => {
    await searchSongsByFilter({ keyword: keyword.value }, ({ data }) => {
        if (!data) return;
        songs.value = data;
    });
};

searchSongsByKeyword();

const uploadFile = async () => {
    if (!fileInfo.value.file) {
        alert("파일을 선택해 주세요");
        return;
    }

    if (!fileInfo.value.title) {
        alert("악보의 제목을 입력해 주세요");
        return;
    }

    if (!selectedSong.value || !selectedSong.value.id) {
        alert("악보의 곡을 선택해 주세요");
        return;
    }

    const formData = new FormData();

    formData.append("files", fileInfo.value.file);
    formData.append(
        "title",
        new Blob([fileInfo.value.title], { type: "application/json" })
    );
    formData.append(
        "level",
        new Blob([fileInfo.value.level], { type: "application/json" })
    );
    formData.append(
        "songId",
        new Blob([selectedSong.value.id], { type: "application/json" })
    );

    registerSheet(formData, ({ data }) => {
        // 성공 시, 악보 디테일 페이지로 이동
        router.push({ name: "sheetDetail", params: { sheetId: data } });
    });
};

const showSongRegisterModal = () => {
    modalVisibility.value = true;
};

const closeSongRegisterModal = () => {
    modalVisibility.value = false;
};
</script>

<template>
    <div class="flex flex-col form w-full max-w-[60vw] mx-auto mt-10 p-2 bg-white bg-opacity-80 rounded-lg text-center">
        <SongRegisterModal v-if="modalVisibility" @closeModalEvent="closeSongRegisterModal" />
        <div class="text-3xl font-semibold mt-4 mb-2">파일 업로드</div>
        <div class="text-xs mb-2">원하는 악보를 업로드해보세요.</div>
        <hr class="border-gray-300 mb-8" />
        <div class="flex flex-col gap-4">
            <label class="form-control flex items-center justify-center w-full">
                <div class="label">
                    <span class="text-left ml-4 font-medium mb-1 w-[50vw] ">악보 파일</span>
                </div>
                <div class="flex justify-center w-full">
                    <input id="file" @change="handleFileChange" type="file"
                        class=" file-input w-[50vw]  border border-gray-300 rounded-lg" ref="fileInput" />
                </div>

            </label>

            <label class="flex flex-col items-center w-full">
                <div class="text-left ml-4 font-medium mb-1 w-[50vw]">악보 제목</div>
                <input v-model="fileInfo.title" type="text" class="w-[50vw] p-2 border border-gray-300 rounded-lg" />
            </label>

            <label class="flex flex-col items-center w-full">
                <div class="text-left ml-4 font-medium mb-1 w-[50vw]">악보 티어</div>
                <select v-model="fileInfo.level" class="w-[50vw] p-2 border border-gray-300 rounded-lg">
                    <template v-for="tier in tierInfo" :key="tier.level">
                        <option v-if="tier.level" :value="tier.level">{{ tier.title }}</option>
                    </template>
                </select>
            </label>

            <div class="flex justify-center items-center w-full">
                <div class="flex justify-center items-center w-[50vw] gap-2">
                    <label class="w-full">
                        <div class="text-left font-medium mb-1">이 악보의 곡</div>
                        <input @submit.prevent="searchSongsByKeyword" v-model="keyword" type="text"
                            class="w-full p-2 border border-gray-300 rounded-lg" @input="searchSongsByKeyword"
                            placeholder="검색" />
                    </label>

                    <template v-if="selectedSong.id">
                        <SmallSongCard :song="selectedSong" class="max-w-[400px] w-full">
                            <img :src="require('@/assets/img/close.png')" alt="체크 표시" @click="selectedSong = ''"
                                class="absolute top-1 right-1 w-4 h-4" />
                        </SmallSongCard class="max-w-[400px] w-full">
                    </template>
                    <template v-else>
                        <div
                            class="mt-3 ml-4 mr-2 mb-0 pt-2  bg-opacity-0 shadow-md rounded-lg w-[13vw] flex flex-col justify-center items-center">
                            <div class="btn bg-gray-800 text-gray-100 w-full" @click="showSongRegisterModal">
                                추가
                            </div>
                        </div>

                    </template>
                </div>
            </div>

            <div v-if="songs.length === 0" class="text-center text-gray-500">
                검색 결과가 없다면, 곡을 추가해주세요.
            </div>

            <div v-else class="flex w-full h-[100px] overflow-x-scroll cursor-pointer gap-2"
                style="-ms-overflow-style:none; scrollbar-width:none;">
                <template v-for="song in songs">
                    <SmallSongCard @click="selectedSong = song" :song class="max-w-[400px] w-full">
                        <template v-if="selectedSong?.id == song.id">
                            <img :src="require('@/assets/img/check.png')" alt="체크 표시"
                                class="absolute top-1 right-1 w-4 h-4" />
                        </template>
                    </SmallSongCard>
                </template>
            </div>

            <div class="flex flex-row gap-4 justify-end items-center">
                <div class="btn cursor-pointer text-gray-500 shadow-md" @click="router.go(-1)">
                    취소하기
                </div>
                <button class="btn bg-gray-800 text-gray-100" @click="uploadFile">제출하기</button>
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

.sheet-info>* {
    padding: 5px 0;
    font-size: 5vw;
}

.card>* {
    flex: 1;
    margin: 3px;
}

ul {
    list-style-type: none;
    background: #fff;
    cursor: pointer;
}
</style>