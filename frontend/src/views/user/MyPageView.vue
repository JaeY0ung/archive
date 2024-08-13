<script setup>
import { ref, onMounted, computed } from "vue";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import Profile from "@/common/icons/Profile.vue";
import { userPageService } from "@/api/user-page.js";

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
        const isAvailable = await userPageService.checkNicknameDuplicate(
            loginUserInfo.value.nickname
        );

        isNicknameChecked.value = true;
        isNicknameAvailable.value = !isAvailable;

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

const updateUserInfo = async () => {
    if (
        loginUserInfo.value.nickname !== originalUserInfo.value.nickname &&
        (!isNicknameChecked.value || !isNicknameAvailable.value)
    ) {
        alert("닉네임 중복 확인을 먼저 해주세요.");
        return;
    }

    try {
        const userUpdateDto = {
            nickname: loginUserInfo.value.nickname,
        };

        await userPageService.updateUserInfo(userUpdateDto, loginUserInfo.value.profileImage);

        alert("사용자 정보가 성공적으로 업데이트되었습니다.");
        originalUserInfo.value = { ...loginUserInfo.value };

        await userStore.getUserInfo();
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
    <div class="container mx-auto p-4 max-w-xl h-[calc(100vh-4rem)] overflow-y-auto scrollbar-hide">
        <div class="card bg-white bg-opacity-80">
            <div class="card-body">
                <h2 class="card-title text-3xl font-bold text-center mb-6 font-poppins">
                    {{ userInfo.nickname }} 프로필
                </h2>

                <div class="avatar mb-6" @click="triggerFileInput">
                    <div
                        class="w-40 h-40 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2 mx-auto relative overflow-hidden cursor-pointer"
                    >
                        <img
                            v-if="imagePreview || currentProfileImage"
                            :src="imagePreview || currentProfileImage"
                            alt="Profile Preview"
                            class="w-full h-full object-cover"
                        />
                        <Profile v-else class="w-full h-full p-4 bg-base-200" />
                        <div class="overlay">
                            <span class="text-white text-sm font-roboto">이미지 변경</span>
                        </div>
                    </div>
                </div>
                <input
                    ref="fileInputRef"
                    type="file"
                    @change="handleImageUpload"
                    accept="image/*"
                    class="hidden"
                />

                <div class="form-control w-full mb-4">
                    <label class="label">
                        <span class="label-text text-xl font-semibold text-gray-800">이메일</span>
                    </label>
                    <div class="relative">
                        <input
                            v-model="loginUserInfo.email"
                            type="text"
                            class="input input-bordered w-full pr-10 font-roboto bg-gray-100"
                            disabled
                        />
                        <span
                            class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
                        >
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                class="h-5 w-5"
                                viewBox="0 0 20 20"
                                fill="currentColor"
                            >
                                <path
                                    fill-rule="evenodd"
                                    d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                                    clip-rule="evenodd"
                                />
                            </svg>
                        </span>
                    </div>
                    <p class="text-xs text-gray-500 mt-1 font-roboto">
                        이메일은 변경할 수 없습니다.
                    </p>
                </div>

                <div class="form-control w-full mb-6">
                    <label class="label">
                        <span class="label-text text-xl font-semibold text-gray-800">닉네임</span>
                    </label>
                    <div class="flex gap-2">
                        <input
                            v-model="loginUserInfo.nickname"
                            type="text"
                            class="input input-bordered flex-grow font-roboto"
                            @input="resetNicknameCheck"
                        />
                        <button
                            @click="checkNicknameDuplicate"
                            class="btn btn-primary font-poppins"
                            :disabled="
                                !isModified || loginUserInfo.nickname === originalUserInfo.nickname
                            "
                        >
                            중복확인
                        </button>
                    </div>
                </div>

                <div class="flex justify-center gap-4">
                    <button
                        @click="updateUserInfo"
                        class="btn btn-primary font-poppins"
                        :disabled="!canUpdate"
                    >
                        수정하기
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700&family=Poppins:wght@300;400;600&display=swap");

.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}

.scrollbar-hide::-webkit-scrollbar {
    display: none;
}

.font-roboto {
    font-family: "Roboto", sans-serif;
}

.font-poppins {
    font-family: "Poppins", sans-serif;
}

.avatar {
    position: relative;
}

.avatar .overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.avatar:hover .overlay {
    opacity: 1;
}

.card-title {
    font-family: "Poppins", sans-serif;
    font-weight: 600;
}

.btn {
    font-family: "Poppins", sans-serif;
    font-weight: 400;
}

.input {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
}

.label-text {
    font-family: "Roboto", sans-serif;
    font-weight: 500;
}
</style>
