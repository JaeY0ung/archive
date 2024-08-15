<script setup>
import { ref } from 'vue';
import { getAllGenres } from "@/api/genre"
import { useRouter } from 'vue-router';
import { registerSong } from '@/api/song';
import { showUnselectedWarningAlert } from '@/util/alert';

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
    if (event.target.files[0] && event.target.files[0].type != "image/jpeg" && event.target.files[0].type != "image/png") {
        fileInput.value.value = "";
        showUnselectedWarningAlert(router, "jpg나 png 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.file = event.target.files[0];
};

const uploadSongAndFile = async () => {
    if (!fileInfo.value.file) {
        if (!confirm("곡 이미지 파일이 없습니다. 제출할까요?")) {
            return;
        }
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
            router.go(0);
            // console.log(fileInfo.value)
        }
    )
};
</script>

<template>
    <div class="fixed inset-0 flex items-center justify-center z-20 bg-black bg-opacity-50">
        <div class="fixed inset-0 flex items-center justify-center z-20">
            <div class="h-[70vh] w-[80vw] lg:max-w-[50vw] bg-gray-100 bg-opacity-95 shadow-md rounded-2xl p-6">
                <!-- 위 -->
                <div class="w-full flex justify-between items-center mb-6">
                    <div class="flex-1"></div>
                    <div class="flex-1 w-full  lg:text-5xl">
                        <div class="p-2 text-2xl font-semibold">곡 등록</div>
                        <div class="text-xs">내가 원하는 곡을 등록해보세요.</div>
                    </div>
                    
                    
                    <div class="flex-1 flex justify-end relative">
                        <div class="btn btn-circle" @click="emit('closeModalEvent')">
                            <svg xmlns="http://www.w3.org/2000/svg"  class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round"  stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </div>
                    </div>
                    
                </div>

                <div class="w-full flex justify-center">
                    <hr class="border-gray-300 w-[95%] mb-8" />
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

                        <div class="flex flex-row gap-4 mt-4 mb-4 justify-end items-center">
                            <div class="cursor-pointer btn bg-white text-gray-500  border-none shadow-md" @click="router.go(-1)">취소하기</div>
                            <button class="btn bg-gray-800 text-gray-100 shadow-md" @click="uploadSongAndFile">제출하기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>