import { localAxios } from "@/util/http-common";

const local = localAxios();

export const userPageService = {
    // === 마이페이지 관련 Axios === //
    // 닉네임 중복 검사 하기
    checkNicknameDuplicate: async (nickname) => {
        const response = await local.get("/users/check-nickname", {
            params: { nickname },
        });
        return response.data;
    },

    // 유저 정보 업데이트 하기
    updateUserInfo: async (userUpdateDto, profileImage) => {
        const formData = new FormData();

        const userUpdateDtoBlob = new Blob([JSON.stringify(userUpdateDto)], {
            type: "application/json",
        });
        formData.append("userUpdateDto", userUpdateDtoBlob);

        if (profileImage instanceof File) {
            formData.append("profileImage", profileImage);
        }

        const response = await local.put("/users", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });

        return response.data;
    },

    // === 유저 프로필 관련 Axios ===//
    // 유저 프로필 정보 가져오기
    fetchUserProfile: async (nickName) => {
        try {
            const response = await local.get(`/users/${nickName}`);
            return response.data;
        } catch (error) {
            console.error("사용자 프로필을 가져오는데 실패했습니다:", error);
            throw error;
        }
    },

    // 싱글 플레이 악보 정보 가져오기
    fetchSinglePlaySheets: async (userId) => {
        try {
            const response = await local.get(`/plays/single/${userId}`);
            return response.data.map((sheet) => ({
                id: sheet.id,
                title: sheet.sheetTitle,
                songComposer: sheet.songComposer,
                uploaderNickname: sheet.uploaderNickname,
                songImg: sheet.songImg,
                singleScore: sheet.score,
                nickname: sheet.nickname,
                userImg: sheet.userImg,
                level: sheet.level,
                playTime: sheet.playTime,
            }));
        } catch (error) {
            console.error("싱글 플레이 악보를 가져오는데 실패했습니다:", error);
            return [];
        }
    },

    // 멀티 플레이 악보 정보 가져오기
    fetchMultiPlaySheets: async (userId) => {
        try {
            const response = await local.get(`/plays/multi/${userId}`);
            return response.data.map((sheet) => ({
                id: sheet.id,
                title: sheet.sheetTitle,
                songComposer: sheet.songComposer,
                songImg: sheet.songImg,
                uploaderNickname: sheet.uploaderNickname,
                myScore: sheet.myScore,
                otherScore: sheet.otherScore,
                myNickname: sheet.myNickname,
                otherNickname: sheet.otherNickname,
                myProfileImg: sheet.myProfileImg,
                otherProfileImg: sheet.otherProfileImg,
                level: sheet.level,
                playTime: sheet.playTime,
                draw: sheet.draw,
            }));
        } catch (error) {
            console.error("멀티 플레이 악보를 가져오는데 실패했습니다:", error);
            return [];
        }
    },

    // 좋아요한 악보 정보 가져오기
    fetchLikedSheets: async (userId) => {
        try {
            const response = await local.get(`/sheets/like/${userId}`);
            return response.data.map((sheet) => ({
                id: sheet.id,
                title: sheet.title,
                uploaderNickname: sheet.uploaderNickname,
                songComposer: sheet.songComposer,
                songImg: sheet.songImg,
                level: sheet.level,
            }));
        } catch (error) {
            console.error("좋아요한 악보를 가져오는데 실패했습니다:", error);
            return [];
        }
    },

    // 팔로우 정보 가져오기
    fetchFollowInfo: async (nickName) => {
        try {
            const [followersResponse, followingsResponse] = await Promise.all([
                local.get(`/follows/followers/${nickName}`),
                local.get(`/follows/followings/${nickName}`),
            ]);

            return {
                followersCount: followersResponse.data.length,
                followingsCount: followingsResponse.data.length,
                followers: followersResponse.data,
                followings: followingsResponse.data,
            };
        } catch (error) {
            console.error("팔로우 정보를 가져오는 데 실패했습니다:", error);
            return {
                followersCount: 0,
                followingsCount: 0,
                followers: [],
                followings: [],
            };
        }
    },

    // 팔로우/언팔로우 토글
    toggleFollow: async (userId, isCurrentlyFollowing) => {
        try {
            let response;
            if (isCurrentlyFollowing) {
                response = await local.delete(`/follows/followings/${userId}`);
            } else {
                response = await local.post(`/follows/followings/${userId}`);
            }
            return response.data;
        } catch (error) {
            console.error("팔로우/언팔로우에 실패했습니다:", error);
            throw error;
        }
    },
    // 팔로워/팔로잉 목록 가져오기
    fetchFollowList: async (type, nickName) => {
        try {
            const response = await local.get(`/follows/${type}/${nickName}`);
            return response.data;
        } catch (error) {
            console.error(`${type} 목록을 가져오는데 실패했습니다:`, error);
            return [];
        }
    },
};
