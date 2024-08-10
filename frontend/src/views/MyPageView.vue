<script setup>
import { ref, onMounted, computed } from "vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import Profile from "@/common/icons/Profile.vue";

const router = useRouter();
const local = localAxios();
const userStore = useUserStore();
const { userInfo } = storeToRefs(userStore);

const originalUserInfo = ref({
    email: "",
    nickname: "",
    profileImage: null,
});

const loginUserInfo = ref({
    email: "",
    nickname: "",
    profileImage: null,
});

const imagePreview = ref(null);
const fileInputRef = ref(null);
const isNicknameChecked = ref(false);
const isNicknameAvailable = ref(false);

const handleImageUpload = (event) => {
    const file = event.target.files[0];
    loginUserInfo.value.profileImage = file;
    imagePreview.value = URL.createObjectURL(file);
};

const triggerFileInput = () => {
    fileInputRef.value.click();
};

const checkNicknameDuplicate = async () => {
    try {
        const response = await local.get("/users/check-nickname", {
            params: { nickname: loginUserInfo.value.nickname },
        });

        isNicknameChecked.value = true;
        isNicknameAvailable.value = !response.data;

        if (isNicknameAvailable.value) {
            alert("사용 가능한 닉네임입니다.");
        } else {
            alert("이미 사용 중인 닉네임입니다.");
        }
    } catch (error) {
        console.error("닉네임 중복 확인 중 오류 발생:", error);
        alert("닉네임 중복 확인에 실패했습니다.");
        isNicknameChecked.value = false;
    }
};

const resetForm = () => {
    loginUserInfo.value = { ...originalUserInfo.value };
    imagePreview.value = currentProfileImage.value;
    isNicknameChecked.value = true;
    isNicknameAvailable.value = true;
    if (fileInputRef.value) {
        fileInputRef.value.value = "";
    }
};

const updateUserInfo = async () => {
    if (
        loginUserInfo.value.nickname !== originalUserInfo.value.nickname &&
        (!isNicknameChecked.value || !isNicknameAvailable.value)
    ) {
        alert("닉네임 중복 확인을 먼저 해주세요.");
        return;
    }

    try {
        const formData = new FormData();

        const userUpdateDto = {
            nickname: loginUserInfo.value.nickname,
        };
        const userUpdateDtoBlob = new Blob([JSON.stringify(userUpdateDto)], {
            type: "application/json",
        });
        formData.append("userUpdateDto", userUpdateDtoBlob);

        if (loginUserInfo.value.profileImage instanceof File) {
            formData.append("profileImage", loginUserInfo.value.profileImage);
        }

        const response = await local.put("/users", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });

        alert("사용자 정보가 성공적으로 업데이트되었습니다.");
        originalUserInfo.value = { ...loginUserInfo.value };

        await userStore.getUserInfo();

        resetForm();
    } catch (error) {
        console.error("사용자 정보 업데이트 중 오류 발생:", error);
        alert("사용자 정보 업데이트에 실패했습니다. 다시 시도해 주세요.");
    }
};

const resetNicknameCheck = () => {
    if (loginUserInfo.value.nickname !== originalUserInfo.value.nickname) {
        isNicknameChecked.value = false;
        isNicknameAvailable.value = false;
    } else {
        isNicknameChecked.value = true;
        isNicknameAvailable.value = true;
    }
};

const isModified = computed(() => {
    return (
        loginUserInfo.value.nickname !== originalUserInfo.value.nickname ||
        loginUserInfo.value.profileImage !== originalUserInfo.value.profileImage
    );
});

const canUpdate = computed(() => {
    if (loginUserInfo.value.nickname !== originalUserInfo.value.nickname) {
        return isModified.value && isNicknameChecked.value && isNicknameAvailable.value;
    }
    return isModified.value;
});

const goToUserProfile = () => {
    if (userInfo.value && userInfo.value.nickname) {
        router.push({ name: "userProfile", params: { nickName: userInfo.value.nickname } });
    } else {
        alert("사용자 정보를 불러올 수 없습니다.");
    }
};

const currentProfileImage = computed(() => {
    if (userInfo.value && userInfo.value.userImg) {
        return `data:image/jpeg;base64,${userInfo.value.userImg}`;
    }
    return null;
});

onMounted(() => {
    if (userInfo.value) {
        originalUserInfo.value = { ...userInfo.value };
        loginUserInfo.value = { ...userInfo.value };
        imagePreview.value = currentProfileImage.value;
    }
});
</script>

<template>
    <div class="container">
        <div class="mb-4 profile-image-container" @click="triggerFileInput">
            <img
                v-if="imagePreview || currentProfileImage"
                :src="imagePreview || currentProfileImage"
                alt="Profile Preview"
                class="profile-preview mx-auto"
            />
            <Profile v-else class="profile-icon profile-image" />
            <div class="profile-image-overlay">
                <span>클릭하여 변경</span>
            </div>
            <input
                ref="fileInputRef"
                type="file"
                @change="handleImageUpload"
                accept="image/*"
                class="hidden"
            />
        </div>

        <div class="form-control w-full mb-4">
            <label class="label">
                <span class="label-text">이메일</span>
            </label>
            <input
                v-model="loginUserInfo.email"
                type="text"
                class="input input-bordered w-full"
                disabled
            />
        </div>

        <div class="form-control w-full mb-4">
            <label class="label">
                <span class="label-text">닉네임</span>
            </label>
            <div class="flex">
                <input
                    v-model="loginUserInfo.nickname"
                    type="text"
                    class="input input-bordered flex-grow mr-2"
                    @input="resetNicknameCheck"
                />
                <button
                    @click="checkNicknameDuplicate"
                    class="btn btn-outline"
                    :disabled="!isModified || loginUserInfo.nickname === originalUserInfo.nickname"
                >
                    중복확인
                </button>
            </div>
        </div>

        <div class="flex flex-col items-center mt-4">
            <div class="mb-2">
                <button @click="updateUserInfo" class="btn btn-primary mr-2" :disabled="!canUpdate">
                    수정하기
                </button>
                <button @click="goToUserProfile" class="btn btn-info">내 프로필 가기</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    width: 500px;
    margin: auto;
    text-align: center;
}

.profile-image-container {
    position: relative;
    width: 200px;
    height: 200px;
    margin: 0 auto;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
}

.profile-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.profile-image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    display: flex;
    justify-content: center;
    align-items: center;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.profile-image-container:hover .profile-image-overlay {
    opacity: 1;
}

.profile-image-overlay span {
    font-size: 16px;
}

.btn-info {
    background-color: #3498db;
    color: white;
    border: none;
}

.btn-info:hover {
    background-color: #2980b9;
}

.profile-icon {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f0f0f0;
    border-radius: 50%;
}

.profile-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>