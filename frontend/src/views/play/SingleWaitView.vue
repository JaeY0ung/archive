<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";
import { useUserStore } from "@/stores/user";
// import defaultProfileImage from "@/assets/img/common/default_profile.png";
import UserCardForPlay from "@/common/UserCardForPlay.vue";
import SelectCategory from "@/common/sheet/SelectCategory.vue";
import SelectSheet from "@/common/sheet/SelectSheet.vue";
import { searchSheetsByFilter } from '@/api/sheet';
const router = useRouter();
const userStore = new useUserStore();

const isInCategoryView = ref(true);

const user = ref({
    img: "",
    nickname: "악카이브1",
    score: "0",
    isEmpty: true,
});

const accessToken = sessionStorage.getItem("accessToken");
userStore.getUserInfo(accessToken);
const loginUser = userStore.userInfo;

const onClickStart = () => {
  if (!selectedSheetId.value) {
    alert("악보를 고르세요")
    return;
  }
    router.push({ name: "singlePlay", params: { sheetId: selectedSheetId.value } });
};

const onClickQuit = () => {
    router.push({ name: 'multiRoomList' });
}

const sheets = ref([]);
const selectedSheetId = ref();

const getPopularsheets = async () => {
	searchSheetsByFilter(
		{ sort: "POPULAR" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getNewsheets = async () => {
	searchSheetsByFilter(
		{ sort: "LATEST" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getRandomsheets = async () => {
	searchSheetsByFilter(
		{ sort: "RANDOM" },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getUserLevelsheets = async () => {
	searchSheetsByFilter(
		{ levels: [1] },
		({ data }) => {
			if (!data) return;
			sheets.value = data;
		}
	)
}

const getSheetsByCategory = (sort) => {
  isInCategoryView.value = false
  if (sort == "RANDOM") getRandomsheets();
  else if (sort == "POPULAR") getPopularsheets();
  else if (sort == "LATEST") getNewsheets();
  else if (sort == "LEVEL") getUserLevelsheets();
}

const setSheetId = (sheetId) => {
  selectedSheetId.value = sheetId;
}
</script>

<template>
    <div class="flex w-full flex-col rounded-xl shadow-xl opacity-[0.8] mb-[10px] bg-red-400">
        <div class="flex w-full flex-grow-0 h-[70%] justify-center items-center rounded-tl-xl rounded-tr-xl bg-blue-300">
            <SelectCategory v-if="isInCategoryView" @send-sheet-category="getSheetsByCategory" />
            <SelectSheet v-else :sheets="sheets" @send-go-to-back="isInCategoryView=true" @send-sheet-id="setSheetId"/>
        </div>
        <div class="flex flex-grow w-full h-[35%] justify-evenly items-center rounded-bl-xl rounded-br-xl bg-yellow-100">
          <button class="btn btn-primary w-24" @click="onClickStart">연주하기</button>
          <UserCardForPlay :user="loginUser" @onClickStart="onClickStart" />
          <button class="btn btn-primary w-24" @click="onClickQuit">나가기</button>
        </div>
    </div>
</template>

<style scoped>
.justify-center {
    justify-content: center;
    align-items: center;
}
.justify-around {
    justify-content: space-around;
}

.container {
  margin: 10px auto;
  width: 90vw;
  background-color: #f0f0f0;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  opacity: 0.8;
}

.button-div {
  display: flex;
  justify-content: center;
  align-items: center;
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