import { localAxios } from '@/util/http-common';

const local = localAxios();

async function updateAllUsersScore(success) {
    await local.put(`/admin/update-score`).then(success).catch((err) => console.error(err));
}

export {
    updateAllUsersScore
};
