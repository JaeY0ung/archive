<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { registerSheet } from "@/api/sheet";
import { searchSongsByFilter } from "@/api/song";
import SongRegisterModal from "@/common/modal/SongRegisterModal.vue"
const router = useRouter();

const keyword = ref("");
const fileInput = ref(null);
const modalVisibility = ref(false);
const songs = ref([{
    id: Number,
    title: String,
    composer: String,
    genreTitle: String,
    img: String,
    imageUrl: "",
}])

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
    if (event.target.files[0].type !== "audio/mid") {
        fileInput.value.value = "";
        alert(".mid 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.file = event.target.files[0];
};

const searchSongsByKeyword = () => {
    searchSongsByFilter(
        { keyword: keyword.value },
        ({data})=>{
            if (!data) return;
            songs.value = data;
            songs.value.map(s => s.img ? s.imageUrl = `data:image/jpeg;base64,${s.img}` : '기본 이미지'); 
        }
    )
}

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


    formData.append( "files", fileInfo.value.file );
    formData.append( "title", new Blob([fileInfo.value.title], { type: "application/json" }) );
    formData.append( "level", new Blob([fileInfo.value.level], { type: "application/json" }) );
    formData.append( "songId", new Blob([selectedSong.value.id], { type: "application/json" }) );
    
    registerSheet(formData, ({ data }) => { // 성공 시, 악보 디테일 페이지로 이동
        router.push({ name: 'sheetDetail', params: { sheetId: data } });
    })
};

const showSongRegisterModal = () => {
    modalVisibility.value = true;
}

const closeSongRegisterModal = () => {
    modalVisibility.value = false;
}
</script>

<template>
    <div class="form h-full w-[500px] text-center m-auto">
        <SongRegisterModal v-if="modalVisibility" @closeModalEvent="closeSongRegisterModal"/>
        <div class="text-5xl mb-10">파일 업로드</div>
        <div class="scroll-x h-[90%] flex flex-col gap-1">
            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">악보 파일</span>
                </div>
                <input id="file" @change="handleFileChange" type="file" class="file-input input-bordered w-full" ref="fileInput" />
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">악보 제목</span>
                </div>
                <input v-model="fileInfo.title" type="text" class="input input-bordered w-full" />
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">악보 티어</span>
                </div>
                <input v-model="fileInfo.level" type="text" class="input input-bordered w-full" />
            </label>

            <div class="flex flex-row items-center">
                <label class="form-control w-full mb-5">
                    <div class="label">
                        <span class="label-text">이 악보의 곡</span>
                    </div>
                    <input @submit.prevent="searchSongsByKeyword" v-model="keyword" type="text" class="input input-bordered w-full" @input="searchSongsByKeyword" placeholder="검색" />
                    
                </label>
                
                <template v-if="selectedSong.id">
                    <div class="m-[5px] p-[5px] flex flex-row justify-start gap-3 bg-white rounded-lg relative" style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">
                        <img :src="require('@/assets/img/close.png')" alt="체크 표시" @click="selectedSong=''" width="15px" height="15px" class="absolute top-1 right-1">
                        
                        <!-- (왼쪽) 악보 사진 -->
                        <div class="h-[80px] w-[80px] flex justify-center">
                            <img class="rounded-lg" :src="selectedSong?.imageUrl" alt="원본 곡 이미지">
                        </div>
                        <!-- (오른쪽) 악보 정보 -->
                        <div class="min-w-[160px] flex flex-col gap-2 mt-2">
                            <div class="bold flex items-center" style="font-size: 18px;">{{ selectedSong.title }}</div>
                            <div class="flex medium" style="font-size: 12px;">{{ selectedSong.composer }}</div>
                            <div class="flex medium" style="font-size: 12px;">장르 {{ selectedSong.genreTitle }}</div>
                        </div>
                    </div>
                </template>
                <template v-else>
                    <div class="m-[5px] p-[5px]  gap-3 bg-white rounded-lg min-w-[230px] min-h-[80px] text-center ">
                        <div>
                            <div>
                                이 악보에 맞는 곡을<br>
                                선택해주세요
                            </div>
                            <div class="btn btn-secondary" @click="showSongRegisterModal">추가</div>
                        </div>
                    </div>
                </template>
            </div>
            <!-- 고르기 -->
            <div class="flex w-[100%] h-[100px] overflow-x-scroll cursor-pointer" style="-ms-overflow-style:none; scrollbar-width:none;">
                <template v-for="song in songs">
                    <div class="m-[5px] p-[5px] flex flex-row justify-start gap-3 bg-white rounded-lg relative" style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);" @click="selectedSong=song">
                        <!-- (왼쪽) 악보 사진 -->
                        <div class="h-[80px] w-[80px] flex f">
                            <img class="rounded-lg" :src="song?.imageUrl" alt="원본 곡 이미지">
                        </div>
                        <!-- (오른쪽) 악보 정보 -->
                        <div class="min-w-[160px] flex flex-col gap-2 mt-2">
                            <div class="flex bold" style="font-size: 18px;">{{ song.title }}</div>
                            <div class="flex medium" style="font-size: 12px;">{{ song.composer }}</div>
                            <div class="flex medium" style="font-size: 12px;">{{ song.genreTitle }}</div>
                        </div>
                        <template v-if="selectedSong?.id == song.id">
                            <img :src="require('@/assets/img/check.png')" alt="체크 표시" width="15px" height="15px" class="absolute top-1 right-1">
                        </template>
                    </div>
                </template>
            </div>

            <div class="flex flex-row gap-10 justify-end items-center">
                <div class="cursor-pointer" @click="router.go(-1)">취소하기</div>
                <button class="btn btn-primary" @click="uploadFile">제출하기</button>
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

ul {
    list-style-type: none;
    background: #fff;
    cursor: pointer;
}
</style>