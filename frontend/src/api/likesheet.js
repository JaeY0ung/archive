import { localAxios } from '@/util/http-common';

const local = localAxios();

async function likeSheet (sheetId, success, fail) {
    await local.post(`/likes/sheets/${sheetId}`).then(success).catch(fail);
}
async function dislikeSheet (sheetId, success, fail) {
    await local.delete(`/likes/sheets/${sheetId}`).then(success).catch(fail);
}

export {
    likeSheet,
    dislikeSheet,
};
