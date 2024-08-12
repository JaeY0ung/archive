import { localAxios } from "@/util/http-common";

const local = localAxios();

export const userPageService = {
    checkNicknameDuplicate: async (nickname) => {
        const response = await local.get("/users/check-nickname", {
            params: { nickname },
        });
        return response.data;
    },

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
};
