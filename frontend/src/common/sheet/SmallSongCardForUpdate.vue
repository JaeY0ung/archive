<script setup>
import { ref, watch } from 'vue';
import { updateSong } from '@/api/song';
import { getAllGenres } from '@/api/genre';

const props = defineProps({
    song: Object
})

const emit = defineEmits(['update-song-event'])

const file = ref();
const fileInputRef = ref(null);

const triggerFileInput = () => {
    fileInputRef.value.click();
};

const handleFileChange = (event) => {
    file.value = event.target.files[0];
    props.song.imageUrl = URL.createObjectURL(file.value)
}

const updateForm = ref({
    title: props.song.title,
    composer: props.song.composer,
    genreId: props.song.genreId
})

const updateSongById = () => {
    const formData = new FormData();

    formData.append( "file", file.value );
    formData.append( "title", new Blob([updateForm.value.title], { type: "application/json" }) );
    formData.append( "composer", new Blob([updateForm.value.composer], { type: "application/json" }) );
    formData.append( "genreId", new Blob([updateForm.value.genreId], { type: "application/json" }) );
    updateSong(
        props.song.id,
        formData,
        (res) => {
            emit('update-song-event')
        }
    )
}

const genres = ref([]);
getAllGenres(({ data }) => { genres.value = data });

watch(() => props.song, () => {
    console.log(props.song)
    props.song.imageUrl = props.song.img ? `data:image/jpeg;base64,${props.song.img}` : require('@/assets/img/default/song_img.png');
})
props.song.imageUrl = props.song.img ? `data:image/jpeg;base64,${props.song.img}` : require('@/assets/img/default/song_img.png');

</script>

<template>
    <div class="m-[5px] p-[5px] flex flex-row justify-between gap-3 bg-white rounded-2xl relative"
        style="box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);">
        <div class="flex gap-3">
            <!-- (왼쪽) 악보 사진 -->
            <div class="max-h-[80px] max-w-[80%] flex">
                <img @click="triggerFileInput" class="rounded-2xl" :src="song?.imageUrl" alt="원본 곡 이미지" width="80">
                <input @change="handleFileChange" ref="fileInputRef" type="file" accept="image/*" class="hidden" />
            </div>
            <!-- (오른쪽) 악보 정보 -->
            <div class="flex-grow flex flex-col gap-2 mt-2">
                <div class="w-full flex bold" style="font-size: 18px;">
                    <input type="text" v-model="updateForm.title" class="w-full">
                </div>
                
                <div class="w-full flex medium" style="font-size: 12px;">
                    <input type="text" v-model="updateForm.composer">
                </div>

                <div class="w-full flex medium" style="font-size: 12px;">
                    <select v-model="updateForm.genreId">
                        <option v-for="genre in genres" :value="genre.id">{{ genre.title }}</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="flex flex-col justify-center">
            <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updateSongById">확인</div>
        </div>
    </div>
</template>

<style scoped></style>