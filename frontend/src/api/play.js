import { localAxios } from '@/util/http-common';

const local = localAxios();

async function gradeMyFile(sheetId, success, fail) {
    await local.get(`/sheets/${ sheetId }`).then(success).catch(fail);
}

export {
    gradeMyFile,
};
