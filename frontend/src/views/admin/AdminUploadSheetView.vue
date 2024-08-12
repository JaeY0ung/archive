<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { registerdummySheetsByAdmin } from "@/api/sheet";
import { tierInfo } from "@/util/tier-info"
const router = useRouter();

const fileInput = ref(null);

const fileInfo = ref({
    files: "",
    level: "3",
});

// 파일이 바뀔 때마다 파일 ref값 변경
const handleFileChange = (event) => {
    fileInfo.value.files = event.target.files;
};


const uploadFile = async () => {
    if (!fileInfo.value.files) {
        alert("파일을 선택해 주세요");
        return;
    }
    const formData = new FormData();
    Array.from(fileInfo.value.files).forEach(file => {
        formData.append("files", file);
    });

    formData.append("level", new Blob([fileInfo.value.level], { type: "application/json" }));
    
    registerdummySheetsByAdmin(formData, (res) => { // 성공 시, 악보 디테일 페이지로 이동
        alert("업로드 성공!");
    })
};

</script>

<template>
    <div class="flex flex-col flex-grow form w-[500px] max-w-[500px] rounded-box text-center m-auto p-[10px] bg-slate-300">
        <div class="text-5xl mb-10">파일 업로드</div>

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
                <select v-model="fileInfo.level">
                    <option v-for="tier in tierInfo" :value="tier.level">{{ tier.title }}</option>
                </select>
            </label>

            <div class="flex flex-row gap-10 justify-end items-center">
                <div class="cursor-pointer" @click="router.go(-1)">취소하기</div>
                <button class="btn btn-primary" @click="uploadFile">제출하기</button>
            </div>
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