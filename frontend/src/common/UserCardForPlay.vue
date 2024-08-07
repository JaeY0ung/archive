<script setup>
defineProps({
    user: {
        img: String,
        nickname: String,
        score: Number,
    }
})
const emit = defineEmits(['onClickStart'])
</script>

<template>
    <div class="player-card">
        <div class="player-img">
            <img :src="user.img" alt="Profile Image" />
        </div>
        <div class="player-info-text">
            <div>{{ user.nickname }}</div>
            <div>현재 스코어 : {{ user.score }}</div>
            <button  class="btn text-white" style="background-color: gray" @click="emit('onClickStart')">
                채점 시작
            </button>
        </div>
    </div>
</template>

<style scoped>
.player-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  width: 40%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.player-img {
  width: 100px;
  height: 100px;
  background-color: white;
  border-radius: 50%;
  margin-bottom: 10px;
}

.player-info-text {
  text-align: center;
  margin-bottom: 10px;
}
</style>
