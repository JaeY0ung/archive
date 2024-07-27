<template>
  <div>
    hi
    <div id="naverIdLogin"></div>
    <button type="button" @click="logout">로그아웃</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const naverLogin = ref(null);

const handleOAuthLogin = async () => {
  try {
    const response = await fetch('/oauth2/authorization/naver', {
      method: 'GET',
      credentials: 'include'
    });

    if (response.ok) {
      const data = await response.json();
      console.log(">>>", data);
      if (data.redirect) {
        router.push(data.redirect);
      }
    } else {
      console.error('OAuth 로그인 실패');
    }
  } catch (error) {
    console.error('OAuth 로그인 중 오류 발생:', error);
  }
};

onMounted(() => {
  naverLogin.value = new window.naver.LoginWithNaverId({
    clientId: "iDUO1TY8oTZc0dDxSfU9",
    callbackUrl: "http://localhost:8080/login/oauth2/code/naver",
    isPopup: false,
    loginButton: {
      color: "green", 
      type: 3, 
      height: 60
    },
  });

  naverLogin.value.init();

  naverLogin.value.getLoginStatus((status) => {
    if (status) {
      console.log(status);
      console.log(naverLogin.value.user);

      const email = naverLogin.value.user.getEmail();
      if (email === undefined || email === null) {
        alert("이메일은 필수 정보입니다. 정보 제공을 동의해주세요.");
        naverLogin.value.reprompt();
        return;
      }

      console.log("소셜 로그인 처리가 완료되었습니다.")
      handleOAuthLogin(); // 백엔드의 리다이렉트 처리를 호출
    } else {
      console.log("callback 처리에 실패했습니다.")
    }
  });
});

const logout = () => {
  // 로그아웃 로직 구현
  console.log("로그아웃 처리");
  // 필요한 경우 백엔드에 로그아웃 요청을 보낼 수 있습니다.
};
</script>