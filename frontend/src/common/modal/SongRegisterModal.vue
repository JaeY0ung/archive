<script setup>
import { ref } from 'vue';
import { getAllGenres } from "@/api/genre"
import { useRouter } from 'vue-router';
import { registerSong } from '@/api/song';

const router = useRouter();
const emit = defineEmits(["closeModalEvent"])

const genres = ref([])
const fileInput = ref(null);

getAllGenres(
    ({ data }) => {
        genres.value = data;
    }
);

const fileInfo = ref({
    file: "",
    title: "",
    composer: "",
    genreId: 0,
});

const handleFileChange = (event) => {
    if (event.target.files[0].type != "image/jpeg") {
        fileInput.value.value = "";
        alert(".jpg 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.file = event.target.files[0];
};

const uploadSongAndFile = async () => {
    // 파일 선택 안하고 제출 시 + 파일 선택했다 취소하고 제출 시
    if (!fileInfo.value.file) {
        alert("파일을 선택해 주세요");
        return;
    }

    if (!fileInfo.value.title) {
        alert("제목을 입력해주세요");
        return;
    }


    if (!fileInfo.value.composer) {
        alert("작곡가를 입력해주세요");
        return;
    }

    if (!fileInfo.value.genreId || fileInfo.value.genreId == 0) {
        alert("장르를 선택해주세요");
        return;
    }

    const formData = new FormData();

    formData.append( "file", fileInfo.value.file );
    formData.append( "title", new Blob([fileInfo.value.title], { type: "application/json" }) );
    formData.append( "composer", new Blob([fileInfo.value.composer], { type: "application/json" }) );
    formData.append( "genreId", new Blob([fileInfo.value.genreId], { type: "application/json" }) );
    
    registerSong(
        formData,
        (res) => {
            // router.go(0);
            console.log(fileInfo.value)
        }
    )
};
</script>

<template>
    <div class="h-[70vh] w-[50vw] bg-white rounded-3xl fixed z-20 left-[25vw] top-[18vh] p-3">
        <!-- 위 -->
        <div class="w-full flex justify-between">
            <div class="flex-1"></div>
            <div class="flex-1 text-5xl mb-10">곡 등록</div>
            <div class="flex-1 flex justify-end">
                <div class="btn btn-circle" @click="emit('closeModalEvent')">
                    <svg xmlns="http://www.w3.org/2000/svg"  class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round"  stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </div>
            </div>
        </div>

        <div class="form h-full w-[500px] text-center m-auto">
    
        <div class="scroll-x h-[90%] flex flex-col gap-1">
            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">곡 앨범 이미지</span>
                </div>
                <input @change="handleFileChange" type="file" class="file-input input-bordered w-full" ref="fileInput"/>
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">곡 제목</span>
                </div>
                <input v-model="fileInfo.title" type="text" class="input input-bordered w-full" />
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">곡 작곡가</span>
                </div>
                <input v-model="fileInfo.composer" type="text" class="input input-bordered w-full" />
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">곡 장르</span>
                </div>
                <select v-model="fileInfo.genreId"  class="select select-bordered w-full">
                    <option v-for="genre in genres" :value="genre.id">{{ genre.title }}</option>
                </select>
            </label>

            <div class="flex flex-row gap-10 justify-end items-center">
                <div class="cursor-pointer" @click="router.go(-1)">취소하기</div>
                <button class="btn btn-primary" @click="uploadSongAndFile">제출하기</button>
            </div>
        </div>
    </div>
    </div>
</template>

<style scoped>

</style>