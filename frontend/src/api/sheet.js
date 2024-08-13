import { localAxios } from '@/util/http-common';

const local = localAxios();

// =============== =============== =============== 악보 =============== =============== ===============
async function searchSheetsByFilter(params, success) {
    await local.get(`/sheets`, { params }).then(success).catch((err) => console.error(err));
}

async function searchSheetDetail(sheetId, success) {
    await local.get(`/sheets/${ sheetId }`).then(success).catch((err) => console.error(err));
}

async function searchRecentPlayedsheet(success) {
    await local.get(`/sheets/recent-play`).then(success).catch((err) => console.error(err));
}

async function registerSheet(params, success) {
    await local.post(`/sheets`, params , { headers: { "Content-Type": "multipart/form-data" } }).then(success).catch((err) => console.error(err));
}

async function updateSheet(sheetId, params, success) {
    await local.put(`/sheets/${ sheetId }`, params).then(success).catch((err) => console.error(err));
}

function predictSheetLevel(sheetId) {
    return local.post(`/predict-level/${sheetId}`)
        .then(response => {
            if (response.status === 200) {
                console.log(`난이도 예측 성공: sheet-id ${sheetId}`);
                return response.data;
            } else {
                throw new Error('난이도 예측 요청이 실패하였습니다.');
            }
        })
        .catch(err => {
            console.error('난이도 예측 중 오류 발생:', err);
            throw err;
        });
}

async function getRecommendSheetByUserRecentPlay(success) {
    await local.get(`/sheets/recommend`).then(success).catch((err) => console.error(err));
}

async function deleteSheet(sheetId, success) {
    await local.delete(`/sheets/${ sheetId }`).then(success).catch((err) => console.error(err));
}
// =============== =============== =============== 악보 좋아요, 좋아요 해제  =============== =============== ===============
async function likeSheet(sheetId, success) {
    await local.post(`/likes/sheets/${sheetId}`).then(success).catch((err) => console.error(err));
}

async function dislikeSheet(sheetId, success) {
    await local.delete(`/likes/sheets/${sheetId}`).then(success).catch((err) => console.error(err));
}

// =============== =============== =============== 악보 평점  =============== =============== ===============
async function searchStarRateListBySheetId(sheetId, success) {
    await local.get(`/sheets/${sheetId}/star-rates`).then(success).catch((err) => console.error(err));
}

async function registerStarRateBySheetId(sheetId, params, success) {
    await local.post(`/sheets/${sheetId}/star-rates`, params).then(success).catch((err) => console.error(err));
}

// =============== =============== =============== 악보 파일 =============== =============== ===============
async function getMusicXmlById(sheetId, success){
    await local.get(`/sheets/${sheetId}/music-xml`).then(success).catch((err) => console.error(err));
}

async function getMidById(sheetId, success){
    await local.get(`/sheets/${sheetId}/mid`).then(success).catch((err) => console.error(err));
}

// =============== =============== =============== 관리자 =============== =============== ===============
async function registerdummySheetsByAdmin(params, success) {
    await local.post(`/sheets/insert/all`, params).then(success).catch((err) => console.error(err));
}

async function searchSheetByStatusForAdmin(params, success) {
    await local.get(`/sheets/admin`, { params }).then(success).catch((err) => console.error(err));
}

async function changeSheetStatusBySheetId(sheetId, status, success) {
    await local.put(`/sheets/${ sheetId }/status/${ status }`).then(success).catch((err) => console.error(err));
}

export {
    searchSheetDetail,
    searchSheetsByFilter,
    registerSheet,
    searchRecentPlayedsheet,
    updateSheet,
    getRecommendSheetByUserRecentPlay,
    deleteSheet,
    predictSheetLevel,
    likeSheet,
    dislikeSheet,

    searchStarRateListBySheetId,
    registerStarRateBySheetId,

    getMusicXmlById,
    getMidById,
    
    registerdummySheetsByAdmin,
    searchSheetByStatusForAdmin,
    changeSheetStatusBySheetId,
};
