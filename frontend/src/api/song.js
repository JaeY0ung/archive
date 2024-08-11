import { localAxios } from '@/util/http-common';

const local = localAxios();

async function searchSongsByFilter(params, success) {
    await local.get(`/songs`, { params })
        .then(success).catch((err) => console.error(err));
}

async function registerSong(params, success) {
    await local.post(
        `/songs`,
        params,
        { headers: { "Content-Type": "multipart/form-data" } }
    ).then(success).catch((err) => console.error(err));
}

async function updateSong(songId, params, success) {
    await local.put(
        `/songs/${songId}`,
        params,
        { headers: { "Content-Type": "multipart/form-data" } }
    ).then(success).catch((err) => console.error(err));
}

export {
    searchSongsByFilter,
    registerSong,
    updateSong,
};
