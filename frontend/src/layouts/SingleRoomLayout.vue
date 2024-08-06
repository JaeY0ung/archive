<script setup>
import { useRoute, useRouter } from "vue-router";
import { ref, computed } from "vue";
import { useUserStore } from "@/stores/user";
import { usePlayStore } from "@/stores/play";
import defaultProfileImage from "@/assets/img/common/default_profile.png";

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();
const playStore = new usePlayStore();

const isReady = ref("false");
const isInvited = ref("false");
const canLeaveSite = ref(false);

const me = ref({
    img: defaultProfileImage,
    name: "악카이브1",
    score: "0",
    isEmpty: true,
});



const accessToken = sessionStorage.getItem("accessToken");

userStore.getUserInfo(accessToken);

let user = userStore.userInfo;

me.value.name = user.nickname;

const onClickStart = () => {
    router.push({ 
        name: "singleDefault",
        params: {
            sheetId: 3
        }
    });
};

const onClickQuit = () => {
    router.push("/pianoSaurus");
}
</script>

<template>
    <div class="container">
        <div class="up">
            <RouterView />
        </div>
        <div class="down">
            <div class="player-card">
                <div class="player-img">
                    <img :src="me.img" alt="Profile Image" />
                </div>
                <div class="player-info-text">
                    <div>{{ me.name }}</div>
                    <div>현재 스코어 : {{ me.score }}</div>
                    <button class="btn text-white" style="background-color: gray" @click="onClickStart">
                        채점 시작
                    </button>
                </div>
            </div>
        <!-- <button class="btn btn-primary w-24" v-if="route.name == 'play'" @click="quitButton"> -->
            <button class="btn btn-primary w-24" @click="onClickQuit">나가기</button>
        </div>
    </div>
</template>

<style scoped>
.container {
  margin: 10px auto;
  width: 90vw;
  background-color: #f0f0f0;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  opacity: 0.8;
}
.up {
  background-color: #fff;
  height: 60vh;
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.down {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 25vh;
}

.button-div {
  display: flex;
  justify-content: center;
  align-items: center;
}

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

button {
  border: 1px solid black;
  width: 100px;
  height: 40px;
  margin: 5px;
  background-color: #2196f3;
  color: white;
  border-radius: 5px;
  cursor: pointer;
}

.invite-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-title {
  padding-bottom: 20px;
}

.modal-button {
  display: flex;
  justify-content: space-between;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 50vw;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.modal-content ul {
  list-style: none;
  padding: 0;
}

.modal-content li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.modal-content li.selected {
  background-color: #ccc;
}

.modal-content li img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.modal-content .status {
  padding: 5px 10px;
  border-radius: 5px;
}

.modal-content .status.online {
  background-color: green;
  color: white;
}

.modal-content .status.busy {
  background-color: red;
  color: white;
}

.modal-content .status.offline {
  background-color: gray;
  color: white;
}
</style>