<script setup>
import { ref, onMounted, computed } from "vue";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";

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

onMounted(() => {
    if (userInfo.value) {
        originalUserInfo.value = { ...userInfo.value };
        loginUserInfo.value = { ...userInfo.value };
    }
});

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

        console.log(response.data);

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

// 사용자 정보 업데이트 하기
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
        formData.append("email", loginUserInfo.value.email);
        formData.append("nickname", loginUserInfo.value.nickname);
        if (
            loginUserInfo.value.profileImage &&
            loginUserInfo.value.profileImage !== originalUserInfo.value.profileImage
        ) {
            formData.append("profileImage", loginUserInfo.value.profileImage);
        }

        const response = await local.post("/user", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });

        console.log("User info updated successfully:", response.data);
        alert("사용자 정보가 성공적으로 업데이트되었습니다.");
        originalUserInfo.value = { ...loginUserInfo.value };
    } catch (error) {
        console.error("Error updating user info:", error);
        alert("사용자 정보 업데이트에 실패했습니다.");
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

// 로그아웃 함수 추가
const goLogout = async () => {
    try {
        await userStore.userLogout(); // store에서 직접 액션 호출
        router.push({ name: "main" });
    } catch (error) {
        console.error("로그아웃 중 오류 발생:", error);
        alert("로그아웃 중 오류가 발생했습니다. 다시 시도해주세요.");
    }
};
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
            <img :src="imagePreview" alt="Profile Preview" class="max-w-xs mx-auto rounded-full" />
        </div>

        <div class="flex justify-center mt-4">
            <button @click="updateUserInfo" class="btn btn-primary mr-2" :disabled="!canUpdate">
                수정하기
            </button>
            <button @click="goLogout" class="btn btn-secondary">로그아웃</button>
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
</style>
