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
    <div class="container mx-auto p-4 max-w-xl">
        <div class="card bg-base-100 shadow-xl">
            <div class="card-body">
                <h2 class="card-title text-2xl font-bold text-center mb-6">{{ userInfo.nickname }} 프로필</h2>

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
                        <div
                            class="absolute inset-0 bg-black bg-opacity-30 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity duration-300"
                        >
                            <span class="text-white text-sm">이미지 변경</span>
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
                        <span class="label-text">이메일</span>
                    </label>
                    <input
                        v-model="loginUserInfo.email"
                        type="text"
                        class="input input-bordered w-full"
                        disabled
                    />
                </div>

                <div class="form-control w-full mb-6">
                    <label class="label">
                        <span class="label-text">닉네임</span>
                    </label>
                    <div class="flex gap-2">
                        <input
                            v-model="loginUserInfo.nickname"
                            type="text"
                            class="input input-bordered flex-grow"
                            @input="resetNicknameCheck"
                        />
                        <button
                            @click="checkNicknameDuplicate"
                            class="btn btn-primary"
                            :disabled="
                                !isModified || loginUserInfo.nickname === originalUserInfo.nickname
                            "
                        >
                            중복확인
                        </button>
                    </div>
                </div>

                <div class="flex justify-center gap-4">
                    <button @click="updateUserInfo" class="btn btn-primary" :disabled="!canUpdate">
                        수정하기
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.avatar:hover .overlay {
    opacity: 1;
}
</style>
