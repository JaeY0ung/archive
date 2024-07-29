<template>
    <div>
      <p>인증 처리 중...</p>
    </div>
  </template>
  
  <script setup>
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  const router = useRouter();
  
  onMounted(async () => {
    try {
      const response = await axios.get('http://localhost:8080/auth/token', {
        withCredentials: true // 쿠키 포함
      })
      
        // 응답 헤더에서 Authorization 토큰 추출
        const authHeader = response.headers['authorization'];
        if (authHeader && authHeader.startsWith('Bearer ')) {
          const accessToken = authHeader.substring(7);
          // 세션 스토리지에 저장
          sessionStorage.setItem("accessToken", accessToken);
          console.log("accessToken을 세션 스토리에 저장합니다. = ", accessToken);
        } else {
          console.warn("Authorization 헤더가 없거나 Bearer 토큰이 아닙니다.");
        }
      
      // 메인 페이지로 리다이렉트
      router.replace({ name: "main" });
    } catch (error) {
      console.error('인증 처리 중 오류 발생:', error)
      router.push('/login')
    }
  })
  </script>