<script setup>
import { ref, watch, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { localAxios } from '@/util/http-common';

const local = localAxios();

const router = useRouter();
const route = useRoute();

const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const birthDate = ref('');
const nickname = ref('');
const gender = ref(null);

const user = computed(() => ({
  email: email.value,
  password: password.value,
  birthDate: birthDate.value ? `${birthDate.value}T00:00:00` : null,
  nickname: nickname.value,
  gender: gender.value
}));

const passwordsMatch = ref(false);
const passwordMessage = ref('');

onMounted(() => {
  const emailFromUrl = route.query.email;
  if (emailFromUrl) {
    email.value = emailFromUrl;
  }
});

const checkPasswordMatch = () => {
  if (password.value === confirmPassword.value) {
    passwordsMatch.value = true;
    passwordMessage.value = '비밀번호가 일치합니다.';
  } else {
    passwordsMatch.value = false;
    passwordMessage.value = '비밀번호가 일치하지 않습니다.';
  }
};

watch(password, checkPasswordMatch);
watch(confirmPassword, checkPasswordMatch);

const register = async () => {
  if (!passwordsMatch.value) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }

  try {
    const response = await local.post('/auth/users', user.value, {
      withCredentials: true
    });
    console.log('회원가입 성공:', response.data);
    alert('회원가입이 완료되었습니다.');
    router.push({ name: 'login' });
  } catch (error) {
    console.error('회원가입 실패:', error);
    alert('회원가입에 실패했습니다. 다시 시도해주세요.');
  }
};
</script>

<template>
  <div class="register-container">
    <h2>회원가입</h2>
    <form @submit.prevent="register">
      <div class="form-group">
        <label for="email">이메일:</label>
        <input type="text" id="email" v-model="email" required disabled>
      </div>
      
      <div class="form-group">
        <label for="password">비밀번호:</label>
        <input type="password" id="password" v-model="password" required @input="checkPasswordMatch">
      </div>
      
      <div class="form-group">
        <label for="confirmPassword">비밀번호 확인:</label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" required @input="checkPasswordMatch">
        <p v-if="passwordMessage" :class="{ 'success': passwordsMatch, 'error': !passwordsMatch }">
          {{ passwordMessage }}
        </p>
      </div>
      
      <div class="form-group">
        <label for="birthDate">생년월일:</label>
        <input type="date" id="birthDate" v-model="birthDate" required>
      </div>
      
      <div class="form-group">
        <label for="nickname">닉네임:</label>
        <input type="text" id="nickname" v-model="nickname" required>
      </div>
      
      <div class="form-group">
        <label>성별:</label>
        <div>
          <input type="radio" id="male" v-model="gender" :value="true">
          <label for="male">남성</label>
          <input type="radio" id="female" v-model="gender" :value="false">
          <label for="female">여성</label>
        </div>
      </div>
      
      <button type="submit" :disabled="!passwordsMatch">가입하기</button>
    </form>
  </div>
</template>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.email-input-group {
  display: flex;
  gap: 10px;
}

.email-input-group input {
  flex-grow: 1;
}

.email-input-group button {
  padding: 8px 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.email-input-group button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button[type="submit"] {
  width: 100%;
  padding: 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button[type="submit"]:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.success {
  color: green;
}

.error {
  color: red;
}
</style>