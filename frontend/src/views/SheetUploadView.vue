<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { registerSheet } from "@/api/sheet";
import { searchSongsByFilter } from "@/api/song";
import SongRegisterModal from "@/common/modal/SongRegisterModal.vue"
import { tierInfo } from "@/util/tier-info"
import SmallSongCard from "@/common/sheet/SmallSongCard.vue";
const router = useRouter();

const keyword = ref("");
const fileInput = ref(null);
const modalVisibility = ref(false);
const songs = ref([{
    id: "",
    title: "",
    composer: "",
    genreTitle: "",
    img: "",
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
    
    if (!["audio/mid", "audio/midi"].includes(event.target.files[0].type)) {
        fileInput.value.value = "";
        alert(".mid 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.file = event.target.files[0];
};

const searchSongsByKeyword = () => {
    searchSongsByFilter(
        { keyword: keyword.value },
        ({data}) => {
            if (!data) return;
            songs.value = data;
            console.log(data)
            songs.value.map(s => s.imageUrl = s.img ? `data:image/jpeg;base64,${s.img}` : require('@/assets/img/default/song_img.png')); 
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
    <div class="flex flex-col flex-grow form w-[500px] max-w-[500px] rounded-box text-center m-auto p-[10px] bg-slate-300">
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
                <select v-model="fileInfo.level">
                    <template v-for="tier in tierInfo" :key="tier.level">
                        <option v-if="tier.level" :value="tier.level">{{ tier.title }}</option>
                    </template>
                </select>
            </label>

            <div class="flex flex-row items-center">
                <label class="form-control w-full mb-5">
                    <div class="label">
                        <span class="label-text">이 악보의 곡</span>
                    </div>
                    <input @submit.prevent="searchSongsByKeyword" v-model="keyword" type="text" class="input input-bordered w-full" @input="searchSongsByKeyword" placeholder="검색" />
                    
                </label>

                <template v-if="selectedSong.id">
                    <SmallSongCard  :song="selectedSong">
                        <img :src="require('@/assets/img/close.png')" alt="체크 표시" @click="selectedSong=''" width="15px" height="15px" class="absolute top-1 right-1">
                    </SmallSongCard>
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
                    <SmallSongCard @click="selectedSong = song" :song>
                        <template v-if="selectedSong?.id == song.id">
                            <img :src="require('@/assets/img/check.png')" alt="체크 표시" width="15px" height="15px" class="absolute top-1 right-1">
                        </template>
                    </SmallSongCard>
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