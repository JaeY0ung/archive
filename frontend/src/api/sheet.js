import { localAxios } from '@/util/http-common';

const local = localAxios();

async function searchSheetDetail(sheetId, success) {
    await local.get(`/sheets/${ sheetId }`).then(success).catch((err) => console.error(err));
}

async function searchSheetsByFilter(params, success) {
    await local.get(`/sheets`, { params }).then(success).catch((err) => console.error(err));
}

async function registerSheet(params, success) {
    await local.post(`/sheets`, params , {
            headers: { "Content-Type": "multipart/form-data" }
        }).then(success).catch((err) => console.error(err));
}

async function searchRecentChallengedsheets(success) {
    await local.get(`/sheets/recent-challenge`).then(success).catch((err) => console.error(err));
}

async function searchStarRateListBySheetId(sheetId, success) {
    await local.get(`/sheets/${sheetId}/star-rates`).then(success).catch((err) => console.error(err));
}

async function registerStarRateBySheetId(sheetId, params, success) {
    await local.post(`/sheets/${sheetId}/star-rates`, params).then(success).catch((err) => console.error(err));
}

async function likeSheet(sheetId, success) {
    await local.post(`/likes/sheets/${sheetId}`).then(success).catch((err) => console.error(err));
}

async function dislikeSheet(sheetId, success) {
    await local.delete(`/likes/sheets/${sheetId}`).then(success).catch((err) => console.error(err));
}

async function getMusicXmlById(sheetId, success){
    await local.get(`/sheets/${sheetId}/music-xml`).then(success).catch((err) => console.error(err));
}

async function getMidById(sheetId, success){
    await local.get(`/sheets/${sheetId}/mid`).then(success).catch((err) => console.error(err));
}

export {
    searchSheetDetail,
    searchSheetsByFilter,
    registerSheet,
    searchRecentChallengedsheets,
    searchStarRateListBySheetId,
    registerStarRateBySheetId,
    likeSheet,
    dislikeSheet,
    getMusicXmlById,
    getMidById,
};
