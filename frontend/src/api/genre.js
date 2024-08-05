import { localAxios } from '@/util/http-common';

const local = localAxios();

const getAllGenres = async (success) => {
    await local.get("/genres").then(success).catch((err) => console.error(`${err} : 장르를 가져오지 못했습니다.`));
}

export {
    getAllGenres,
};
