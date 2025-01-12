<template>
    <div class="w-full h-full flex justify-center items-center">
        <div class="w-[500px] bg-white bg-opacity-50 rounded-2xl p-8 shadow-lg">
            <h1 class="text-4xl font-bold mb-8 text-center text-gray-600">
                회원가입
            </h1>
            <form @submit.prevent="register" class="space-y-4">
                <!-- Email input (unchanged) -->
                <div class="space-y-2">
                    <label
                        for="email"
                        class="block text-sm font-medium text-gray-700"
                        >이메일</label
                    >
                    <div class="flex space-x-2">
                        <input
                            v-model="email"
                            id="email"
                            type="email"
                            required
                            class="flex-grow px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            type="button"
                            @click="checkEmailDuplicate"
                            :disabled="!email"
                            class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300"
                        >
                            중복 확인
                        </button>
                    </div>
                    <p
                        v-if="emailCheckMessage"
                        :class="{
                            'text-green-600': !isEmailDuplicate,
                            'text-red-600': isEmailDuplicate,
                        }"
                        class="text-sm mt-1"
                    >
                        {{ emailCheckMessage }}
                    </p>
                </div>

                <!-- Password inputs (unchanged) -->
                <div class="space-y-2">
                    <label
                        for="password"
                        class="block text-sm font-medium text-gray-700"
                        >비밀번호</label
                    >
                    <input
                        v-model="password"
                        id="password"
                        type="password"
                        required
                        @input="checkPasswordMatch"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>

                <div class="space-y-2">
                    <label
                        for="confirmPassword"
                        class="block text-sm font-medium text-gray-700"
                        >비밀번호 확인</label
                    >
                    <input
                        v-model="confirmPassword"
                        id="confirmPassword"
                        type="password"
                        required
                        @input="checkPasswordMatch"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                    <p
                        v-if="passwordMessage"
                        :class="{
                            'text-green-600': passwordsMatch,
                            'text-red-600': !passwordsMatch,
                        }"
                        class="text-sm mt-1"
                    >
                        {{ passwordMessage }}
                    </p>
                </div>

                <!-- Updated Nickname input with duplication check -->
                <div class="space-y-2">
                    <label
                        for="nickname"
                        class="block text-sm font-medium text-gray-700"
                        >닉네임</label
                    >
                    <div class="flex space-x-2">
                        <input
                            v-model="nickname"
                            id="nickname"
                            type="text"
                            required
                            class="flex-grow px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <button
                            type="button"
                            @click="checkNicknameDuplicate"
                            :disabled="!nickname"
                            class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300"
                        >
                            중복 확인
                        </button>
                    </div>
                    <p
                        v-if="nicknameCheckMessage"
                        :class="{
                            'text-green-600': !isNicknameDuplicate,
                            'text-red-600': isNicknameDuplicate,
                        }"
                        class="text-sm mt-1"
                    >
                        {{ nicknameCheckMessage }}
                    </p>
                </div>

                <button
                    type="submit"
                    :disabled="
                        isEmailDuplicate ||
                        !isEmailChecked ||
                        !passwordsMatch ||
                        isNicknameDuplicate ||
                        !isNicknameChecked
                    "
                    class="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-300"
                >
                    가입하기
                </button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, computed } from "vue";
import { useRouter } from "vue-router";
import { localAxios } from "@/util/http-common";

const local = localAxios();
const router = useRouter();

const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const birthDate = ref("");
const nickname = ref("");
const gender = ref(null);
const passwordsMatch = ref(false);
const passwordMessage = ref("");
const isEmailDuplicate = ref(false);
const isEmailChecked = ref(false);
const emailCheckMessage = ref("");
const isNicknameDuplicate = ref(false);
const isNicknameChecked = ref(false);
const nicknameCheckMessage = ref("");

const user = computed(() => ({
    email: email.value,
    password: password.value,
    birthDate: birthDate.value ? `${birthDate.value}T00:00:00` : null,
    nickname: nickname.value,
    gender: gender.value,
}));

const checkEmailDuplicate = async () => {
    try {
        const response = await local.get(
            `/users/check-email?email=${email.value}`,
            {
                withCredentials: true,
            }
        );
        isEmailDuplicate.value = response.data;
        isEmailChecked.value = true;
        emailCheckMessage.value = isEmailDuplicate.value
            ? "이미 사용 중인 이메일입니다."
            : "사용 가능한 이메일입니다.";
    } catch (error) {
        console.error("이메일 중복 확인 실패:", error);
        emailCheckMessage.value = "이메일 중복 확인에 실패했습니다.";
    }
};

const checkNicknameDuplicate = async () => {
    try {
        const response = await local.get(
            `/users/check-nickname?nickname=${nickname.value}`,
            {
                withCredentials: true,
            }
        );
        isNicknameDuplicate.value = response.data;
        isNicknameChecked.value = true;
        nicknameCheckMessage.value = isNicknameDuplicate.value
            ? "이미 사용 중인 닉네임입니다."
            : "사용 가능한 닉네임입니다.";
    } catch (error) {
        console.error("닉네임 중복 확인 실패:", error);
        nicknameCheckMessage.value = "닉네임 중복 확인에 실패했습니다.";
    }
};

const checkPasswordMatch = () => {
    if (password.value === confirmPassword.value) {
        passwordsMatch.value = true;
        passwordMessage.value = "비밀번호가 일치합니다.";
    } else {
        passwordsMatch.value = false;
        passwordMessage.value = "비밀번호가 일치하지 않습니다.";
    }
};

watch(password, checkPasswordMatch);
watch(confirmPassword, checkPasswordMatch);

const register = async () => {
    if (isEmailDuplicate.value || !isEmailChecked.value) {
        alert("이메일 중복 확인을 해주세요.");
        return;
    }

    if (!passwordsMatch.value) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    if (isNicknameDuplicate.value || !isNicknameChecked.value) {
        alert("닉네임 중복 확인을 해주세요.");
        return;
    }

    try {
        const response = await local.post("/users", user.value, {
            withCredentials: true,
        });
        alert("회원가입이 완료되었습니다.");
        router.push({ name: "login" });
    } catch (error) {
        console.error("회원가입 실패:", error);
        alert("회원가입에 실패했습니다. 다시 시도해주세요.");
    }
};
</script>
