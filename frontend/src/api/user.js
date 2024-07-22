import { localAxios } from '@/util/http-common';
const local = localAxios();

async function userConfirm(param, success, fail) {
    await local.post(`/auth/login`, param).then(success).catch(fail);
}

async function findById(username, success, fail) {
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    await local.get(`/auth/info/${username}`).then(success).catch(fail);
}

async function tokenRegeneration(user, success, fail) {
  local.defaults.headers["refreshToken"] = sessionStorage.getItem("refreshToken"); //axios header에 refresh-token 셋팅
    await local.post(`/auth/refresh`, user).then(success).catch(fail);
}

async function logout(username, success, fail) {
    await local.get(`/auth/logout/${username}`).then(success).catch(fail);
}

async function signout(success, fail) {
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    await local.delete(`/users`).then(success).catch(fail);
}

async function insertUser(param, success, fail) {
    await local.post(`/users`, param).then(success).catch(fail);
}

// async function updateUser(param, success, fail) {
//   local.defaults.headers["refreshToken"] = sessionStorage.getItem("refreshToken");
//   await local.put(`/users`, param).then(success).catch(fail);
// }
async function updateUser(formData, success, fail) {
    const config = {
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': sessionStorage.getItem('refreshToken')
        }
    };
    await local.put('/users', formData, config).then(success).catch(fail);
}

// 존재하면 1 반환
async function isUserExist(username, success, fail) {
    await local.get(`/auth/exists/${username}`).then(success).catch(fail);
}

export {
    userConfirm,
    findById,
    tokenRegeneration,
    logout,
    signout,
    insertUser,
    updateUser,
    isUserExist,
};
