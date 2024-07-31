<!-- 파일 업로드 multiple : true, 모든 확장자 다 받음 -->
<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { localAxios } from "@/util/http-common";

const local = localAxios();
const router = useRouter();
const fileInfo = ref({
  files: "",
  title: "",
  level: "",
  songId: "",
});
// 파일이 바뀔 때마다 파일 ref값 변경
const handleFileChange = (event) => {
  console.log(event.target.files);
  fileInfo.value.files = event.target.files;
};

const uploadFile = async () => {
  console.log("업로드 파일 정보: ");
  console.log(fileInfo.value);
  // 파일 선택 안하고 제출 시 + 파일 선택했다 취소하고 제출 시
  if (!fileInfo.value.files) {
    alert("파일을 선택해 주세요.");
    return;
  }

  const formData = new FormData();

  for (let i = 0; i < fileInfo.value.files.length; i++) {
    formData.append("files", fileInfo.value.files[i]);
  }

  formData.append(
    "title",
    new Blob([fileInfo.value.title], { type: "application/json" })
  );
  formData.append(
    "level",
    new Blob([fileInfo.value.level], { type: "application/json" })
  );
  formData.append(
    "songId",
    new Blob([fileInfo.value.songId], { type: "application/json" })
  );

  await local
    .post("/sheets", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    })
    .then((res) => {
      console.error("파일 업로드 성공: ", res);
    })
    .catch((err) => {
      alert("파일 업로드 실패: ", err);
    });
};
</script>

<template>
  <form
    class="h-full w-[500px] text-center m-auto"
    @submit.prevent="uploadFile"
  >
    <div class="text-5xl mb-10">파일 업로드</div>
    <div class="scroll-x h-[90%] flex flex-col gap-1">
      <label class="form-control w-full">
        <div class="label">
          <span class="label-text">악보 파일</span>
        </div>
        <input
          @change="handleFileChange"
          type="file"
          class="file-input input-bordered w-full"
          multiple
        />
      </label>

      <label class="form-control w-full">
        <div class="label">
          <span class="label-text">악보 제목</span>
        </div>
        <input
          v-model="fileInfo.title"
          type="text"
          class="input input-bordered w-full"
        />
      </label>

      <label class="form-control w-full">
        <div class="label">
          <span class="label-text">악보 티어</span>
        </div>
        <input
          v-model="fileInfo.level"
          type="text"
          class="input input-bordered w-full"
        />
      </label>

      <label class="form-control w-full mb-5">
        <div class="label">
          <span class="label-text">이 악보의 곡</span>
        </div>
        <input
          v-model="fileInfo.songId"
          type="text"
          class="input input-bordered w-full"
        />
      </label>

      <div class="flex flex-row gap-10 justify-end items-center">
        <div class="cursor-pointer" @click="router.go(-1)">취소하기</div>
        <button class="btn btn-primary">제출하기</button>
      </div>
    </div>
  </form>
</template>

<style scoped>
</style>