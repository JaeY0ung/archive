<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
import defaultProfileImage from "@/assets/img/common/default_profile.png";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import Sheet from "@/common/sheet/Sheet.vue";
import { useRoute } from "vue-router";

const route = useRoute();
const router = useRouter();
const userStore = new useUserStore();

const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;



const onClickQuit = () => {
    router.push("/room/multi/list");
}


console.log("route.params.sheetId = " + route.params.sheetId);
</script>

<template>
    <div class="container">
      <div class="up">
        <Sheet :sheetId="route.params.sheetId" height="95"/>
      </div>
      <div class="down">
        <UserCardForPlay :user="loginUser" @onClickStart="onClickStart" />
      </div>
    </div>
</template>

<style scoped>
.container {
  margin: 10px auto;
  width: 90vw;
  height: 90vh;
  background-color: #f0f0f0;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  opacity: 0.8;
}
.up {
  background-color: #fff;
  height: 72%;
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.down {
  display: flex;
  justify-content: center;
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