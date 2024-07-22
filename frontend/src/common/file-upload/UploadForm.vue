<!-- 파일 업로드 multiple : true, 모든 확장자 다 받음 -->
<script setup>
import { ref } from 'vue';
import { localAxios } from '@/util/http-common';

import Button from '@/common/ui/Button.vue';
import Input from '@/common/ui/Input.vue'

const local = localAxios();

const fileInfo = ref({
    files: "",
    title: "",
    price: "",
    level: "",
    point: "",
    songId: "",
})
// 파일이 바뀔 때마다 파일 ref값 변경
const handleFileChange = (event) => {
    console.log(event.target.files)
    fileInfo.value.files = event.target.files;
};

const uploadFile = async () => {
    console.log("업로드 파일 정보: ");
    console.log(fileInfo.value);
    // 파일 선택 안하고 제출 시 + 파일 선택했다 취소하고 제출 시
    if (!fileInfo.value.files) {
        alert('파일을 선택해 주세요.');
        return;
    }

    const formData = new FormData();

    for (let i = 0; i < fileInfo.value.files.length; i++) {
        formData.append('files', fileInfo.value.files[i]);
    }
    
    formData.append('title', new Blob([fileInfo.value.title], { type: "application/json" }));
    formData.append('price', new Blob([fileInfo.value.price], { type: "application/json" }));
    formData.append('level', new Blob([fileInfo.value.level], { type: "application/json" }));
    formData.append('point', new Blob([fileInfo.value.point], { type: "application/json" }));
    formData.append('songId', new Blob([fileInfo.value.songId], { type: "application/json" }));

    await local.post(
        "http://localhost:8080/sheets", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        })
        .then(res => {
            console.error("파일 업로드 성공: ", res); 
        })
        .catch(err => {
            alert("파일 업로드 실패: ", err); 
        })
}

const changeTitle = (data) => {
    fileInfo.value.title = data;
}
const changePrice = (data) => {
    fileInfo.value.price = data;
}
const changeLevel = (data) => {
    fileInfo.value.level = data;
}
const changePoint = (data) => {
    fileInfo.value.point = data;
}
const changeSongId = (data) => {
    fileInfo.value.songId = data;
}
</script>

<template>
    <div class="container">
        <form class="form" @submit.prevent="uploadFile">
            <input type="file" class="file-input w-full max-w-xs" @change="handleFileChange" multiple />
            <Input @send-input="changeTitle" inputTitle="악보 제목"/>   
            <Input @send-input="changePrice" inputTitle="악보 가격"/>
            <Input @send-input="changeLevel" inputTitle="악보 티어"/>
            <Input @send-input="changePoint" inputTitle="악보 경험치"/>
            <Input @send-input="changeSongId" inputTitle="이 악보의 곡"/>
            <Button text="제출하기"/>
        </form>
    </div>
</template>

<style scoped>
.form > * {
    
}
</style>