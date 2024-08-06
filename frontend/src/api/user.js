import { localAxios } from '@/util/http-common';

const local = localAxios();


async function userConfirm(param, success, fail) {
    const formData = new URLSearchParams();
    formData.append('email', param.email);
    formData.append('password', param.password);

    try {
        const response = await local.post(`/auth/login`, formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        });
        console.log('유저 정보를 확인하는 정보를 백엔드로 전달합니다.', response);
        success(response);
    } catch (error) {
        fail(error);
    }
}

async function logout(success, fail) {
    await local.get(`/auth/logout`).then(success).catch(fail);
}

async function findByEmail(success, fail) {
    try {
        // TODO : 중복인지 확인하기
        const accessToken = sessionStorage.getItem("accessToken");
        const headers = {
            'Authorization': `Bearer ${accessToken}`
        };

        // Axios 요청에 헤더 포함
        const response = await local.get(`/auth/userInfo`, { headers });
        console.log(response.data)
        success(response);
    } catch (error) {
        fail(error);
    }
}

async function findById(username, success, fail) {
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    await local.get(`/auth/info/${username}`).then(success).catch(fail);
}

async function tokenRegeneration(user, success, fail) {
  local.defaults.headers["refreshToken"] = sessionStorage.getItem("refreshToken"); //axios header에 refresh-token 셋팅
    await local.post(`/auth/refresh`, user).then(success).catch(fail);
}

async function signout(success, fail) {
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    await local.delete(`/users`).then(success).catch(fail);
}

async function insertUser(param, success, fail) {
    await local.post(`/users`, param).then(success).catch(fail);
}


export {
    userConfirm,
    logout,
    findByEmail,
    findById,
    
    tokenRegeneration,
    signout,
    insertUser,

};
