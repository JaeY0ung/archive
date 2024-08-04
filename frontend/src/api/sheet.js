import { localAxios } from '@/util/http-common';

const local = localAxios();

async function searchSheetDetail(sheetId, success, fail) {
    await local.get(`/sheets/${ sheetId }`).then(success).catch(fail);
}

async function searchSheetsByFilter(params, success) {
    await local.get(`/sheets`, { params }).then(success).catch((err) => console.error(err));
}

async function dislikeSheet(sheetId, success, fail) {
    await local.delete(`/likes/sheets/${sheetId}`).then(success).catch(fail);
}

export {
    searchSheetDetail,
    searchSheetsByFilter,
    dislikeSheet,
};
