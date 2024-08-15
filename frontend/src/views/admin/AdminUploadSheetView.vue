<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { registerdummySheetsByAdmin } from "@/api/sheet";
import { tierInfo, tierInfoExceptUnrank } from "@/util/tier-info"
import { showUploadSuccessAlert, showUnselectedWarningAlert } from "@/util/alert";
const router = useRouter();

const fileInput = ref(null);

const fileInfo = ref({
    files: "",
    level: "3",
});

// 파일이 바뀔 때마다 파일 ref값 변경
const handleFileChange = (event) => {
    if (!["audio/mid", "audio/midi"].includes(event.target.files[0].type)) {
        fileInput.value.value = "";
        showUnselectedWarningAlert(router, "mid 확장자의 파일을 업로드해 주세요");
        return;
    }
    fileInfo.value.files = event.target.files;
};

const totalFileCount = ref(0);
const successFileCount = ref(0)
const uploadFile = async () => {
    if (!fileInfo.value.files) {
        showUnselectedWarningAlert(router, "파일을 선택해 주세요");
        return;
    }

    totalFileCount.value = fileInfo.value.files.length;
    successFileCount.value = 0;
    for (const file of fileInfo.value.files) {
        const formData = new FormData();
        formData.append("files", file);
        formData.append("level", new Blob([fileInfo.value.level], { type: "application/json" }));

        try {
            await registerdummySheetsByAdmin(formData, (res) => {
                successFileCount.value++;
            });
        } catch (error) {
            console.error("파일 업로드 실패:", error);
        }
    }

    showUploadSuccessAlert("업로드 성공!");
    totalFileCount.value = 0;
    successFileCount.value = 0;
};

</script>

<template>
    <div class="flex flex-col form w-full max-w-[60vw] mx-auto mt-10 bg-white bg-opacity-80 rounded-2xl text-center p-5">
        <div class="text-5xl mb-10">파일 일괄 업로드</div>
        <div class="text-3xl mb-10">(관리자)</div>

        <div class="scroll-x h-[90%] flex flex-col gap-1">
            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">파일</span>
                </div>
                <input @change="handleFileChange" type="file" class="file-input input-bordered w-full" ref="fileInput" multiple />
            </label>

            <label class="form-control w-full">
                <div class="label">
                    <span class="label-text">레벨</span>
                </div>
                <select v-model="fileInfo.level" class="select select-bordered w-full">
                    <option v-for="tier in tierInfoExceptUnrank" :value="tier.level">{{ tier.title }}</option>
                </select>
            </label>

            <div class="flex flex-row gap-10 justify-end items-center">
                <div class="cursor-pointer" @click="router.go(-1)">취소하기</div>
                <button class="btn bg-gray-800 text-gray-100 w-full" @click="uploadFile">제출하기</button>
            </div>
        </div>
        <div v-if="totalFileCount" class="fixed z-10 top-1/2 left-1/2 translate-x-[-50%] translate-y-[-50%]">
            <button class="btn border-stone-50">
                <span class="loading loading-spinner bg-white"></span>
                loading : {{ totalFileCount }}개 중 {{ successFileCount }}개 업로드 성공
            </button> 
        </div>
    </div>
</template>

<style scoped>
.card {
    margin: 5px;
    padding: 5px;

	display: flex;
    flex-direction: row;
    justify-content: start;

	background-color: rgb(255, 255, 255);
	box-shadow: 0px 5px 8px rgba(0, 0, 0, 0.3);
}

.img {
    height: 80px;
    width: 80px;
    display: flex;
    justify-content: center;
}


.sheet-info {
    min-width: 150px;
}

.sheet-info > * {
    padding: 5px 0;
    font-size: 5vw;
}

.card > * {
    flex: 1;
    margin:3px;
}

ul {
    list-style-type: none;
    background: #fff;
    cursor: pointer;
}
</style>