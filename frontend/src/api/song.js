import { localAxios } from '@/util/http-common';

const local = localAxios();

async function searchSongsByFilter(params, success) {
    await local.get(`/songs`, { params }).then(success).catch((err) => console.error(err));
}

export {
    searchSongsByFilter,
};
