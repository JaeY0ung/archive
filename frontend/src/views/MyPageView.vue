<script setup>
import { ref, onMounted, computed } from "vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

const router = useRouter();
const local = localAxios();
const userStore = useUserStore();
const { userInfo, getUserInfo } = storeToRefs(userStore);

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
    imagePreview.value = null;
    isNicknameChecked.value = true;
    isNicknameAvailable.value = true;
    if (fileInputRef.value) {
        fileInputRef.value.value = "";
    }
    loginUserInfo.value.profileImage = null;
};

const updateUserInfo = async () => {
    // 닉네임 변경 시 중복 확인 여부 체크
    if (
        loginUserInfo.value.nickname !== originalUserInfo.value.nickname &&
        (!isNicknameChecked.value || !isNicknameAvailable.value)
    ) {
        alert("닉네임 중복 확인을 먼저 해주세요.");
        return;
    }

    try {
        const formData = new FormData();

        // UserUpdateDto 객체 생성 및 JSON 변환
        const userUpdateDto = {
            nickname: loginUserInfo.value.nickname,
        };
        const userUpdateDtoBlob = new Blob([JSON.stringify(userUpdateDto)], {
            type: "application/json"
        });
        formData.append("userUpdateDto", userUpdateDtoBlob);

        // 프로필 이미지가 변경되었을 경우에만 추가
        if (
            loginUserInfo.value.profileImage &&
            loginUserInfo.value.profileImage !== originalUserInfo.value.profileImage
        ) {
            formData.append("profileImage", loginUserInfo.value.profileImage);
        }

        // API 호출
        const response = await local.put("/users", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });

        alert("사용자 정보가 성공적으로 업데이트되었습니다.");
        originalUserInfo.value = { ...loginUserInfo.value };

        // 사용자 정보 갱신
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

const goLogout = async () => {
    try {
        await userStore.userLogout();
        router.push({ name: "login" });
    } catch (error) {
        console.error("로그아웃 중 오류 발생:", error);
        alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
};

const goToUserProfile = () => {
    if (userInfo.value && userInfo.value.nickname) {
        router.push({ name: "userProfile", params: { nickName: userInfo.value.nickname } });
    } else {
        alert("사용자 정보를 불러올 수 없습니다.");
    }
};

const deleteAccount = async () => {
    if (!confirm("정말로 회원탈퇴를 하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
        return;
    }

    try {
        await local.delete(`/users/${userInfo.value.id}`);
        alert("회원탈퇴가 완료되었습니다.");
        await userStore.userLogout();
        router.push({ name: "main" });
    } catch (error) {
        console.error("회원탈퇴 중 오류 발생:", error);
        alert("회원탈퇴 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
};

onMounted(() => {
    if (userInfo.value) {
        originalUserInfo.value = { ...userInfo.value };
        loginUserInfo.value = { ...userInfo.value };
    }
});
</script>

<template>
    <div class="container">
        <div class="text-4xl mb-6">마이페이지</div>

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

        <div class="form-control w-full mb-4">
            <label class="label">
                <span class="label-text">프로필 사진</span>
            </label>
            <div class="flex items-center">
                <input
                    type="text"
                    :value="
                        loginUserInfo.profileImage
                            ? loginUserInfo.profileImage.name
                            : '선택된 파일 없음'
                    "
                    class="input input-bordered flex-grow mr-2"
                    readonly
                />
                <button @click="triggerFileInput" class="btn btn-outline">파일 선택</button>
                <input
                    ref="fileInputRef"
                    type="file"
                    @change="handleImageUpload"
                    accept="image/*"
                    class="hidden"
                />
            </div>
        </div>

        <div v-if="imagePreview" class="mb-4">
            <img :src="imagePreview" alt="Profile Preview" class="profile-preview" />
        </div>

        <div class="flex justify-center mt-4">
            <button @click="updateUserInfo" class="btn btn-primary mr-2" :disabled="!canUpdate">
                수정하기
            </button>
            <button @click="goToUserProfile" class="btn btn-info mr-2">내 프로필 가기</button>
            <button @click="goLogout" class="btn btn-secondary mr-2">로그아웃</button>
            <button @click="deleteAccount" class="btn btn-error">회원탈퇴</button>
        </div>
    </div>
</template>

<style scoped>
.container {
    width: 500px;
    margin: auto;
    text-align: center;
}

.btn-secondary {
    background-color: #f44336;
    color: white;
    border: none;
}

.btn-secondary:hover {
    background-color: #d32f2f;
}

.profile-preview {
    width: 50px;
    height: 50px;
    margin: 0 auto;
    border-radius: 50%;
    object-fit: cover;
}

.btn-info {
    background-color: #3498db;
    color: white;
    border: none;
}

.btn-info:hover {
    background-color: #2980b9;
}

.btn-error {
    background-color: #000000;
    color: white;
    border: none;
}

.btn-error:hover {
    background-color: #3d3837;
}
</style>
