<script setup>
import { ref, watch } from 'vue';
import SmallSongCard from '@/common/sheet/SmallSongCard.vue';
import SmallSongCardForUpdate from '@/common/sheet/SmallSongCardForUpdate.vue';
import { searchSongsByFilter, deleteSong } from '@/api/song';

const songs = ref([]);

const getSongs = () => {
    searchSongsByFilter(
        {},
        ({ data }) => {
            songs.value = data;
        }
    )
}

getSongs();

const updatingSongId = ref('')

const updateEventOccur = () => {
    getSongs();
    updatingSongId = '';
}

const deleteSongById = async (songId) => {
    if (!confirm("정말로 삭제 하시겠습니까?")) {
        return;
    }
    await deleteSong(
        songId,
        (res) => {
            getSongs();
        }
    );
}
</script>

<template>
    <div class="m-auto">
        <div class="flex justify-center gap-3">
            <div class=" bg-green-700 rounded-xl p-1 scroll-y w-[70%]">
                <div class="text-center">곡</div>
                <div>
                    <template v-for="song in songs" :key="song.id">
                        <div class="flex">
                            <SmallSongCard v-if="song.id != updatingSongId" class="w-full" :song >
                                <div class="flex flex-col justify-center gap-2">
                                    <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updatingSongId = song.id">수정</div>
                                    <div class="bg-red-500 rounded-xl p-2 cursor-pointer" @click="deleteSongById(song.id)">삭제</div>
                                </div>
                            </SmallSongCard>
                            <SmallSongCardForUpdate v-else class="w-full" @update-song-event="updateEventOccur" :song/>
                        </div>
                    </template>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>