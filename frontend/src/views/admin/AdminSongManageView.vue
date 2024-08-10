<script setup>
import { ref, watch } from 'vue';
import SmallSongCard from '@/common/sheet/SmallSongCard.vue';
import SmallSongCardForUpdate from '@/common/sheet/SmallSongCardForUpdate.vue';
import { searchSongsByFilter } from '@/api/song';

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
</script>

<template>
    <div class="m-auto">
        <div class="flex justify-center gap-3">
            <div class=" bg-green-700 rounded-xl p-1">
                <div class="text-center">곡</div>
                <div>
                    <template v-for="song in songs" :key="song.id">
                        <div class="flex">
                            <SmallSongCard v-if="song.id != updatingSongId" class="w-full" :song>
                                <div class="flex flex-col justify-center">
                                    <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updatingSongId = song.id">수정</div>
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