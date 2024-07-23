<script setup>
import { ref } from 'vue';

const loginUserInfo = ref({
	email: "ssafy@ssafy.com"
})
</script>

<template>
	<div class="container">
		<!-- 마이페이지 -->
		<div class="text-4xl">마이페이지</div>

		<label class="form-control w-full">
			<div class="label">
				<span class="label-text">아이디 (이메일)</span>
			</div>
			<input v-model="loginUserInfo.email" type="text"  class="input input-bordered w-full" disabled/>
		</label>

		<label class="form-control w-full">
			<div class="label">
				<span class="label-text">닉네임</span>
			</div>
			<input v-model="loginUserInfo.nickname" type="text"  class="input input-bordered w-full" />
		</label>

		<div class="flex justify-center">
            <button class="btn btn-primary" :class="color">
                수정하기
            </button>
        </div>

	</div>
</template>

<style scoped>
.container {
	width: 500px;
	margin:auto;
	text-align: center;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.container > * {
	margin-bottom: 20px;
}
</style>