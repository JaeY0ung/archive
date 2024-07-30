<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { localAxios } from '@/util/http-common';

const local = localAxios();
const keyword = ref('');

const searchSheets = async () => {
  // 테스트 용입니다.
  try {
    const response = await local.get(`/sheets?keyword=${keyword.value}&sort=RANDOM`, {
      withCredentials: true
    });
    console.log('검색 결과:', response.data);
    // 여기서 검색 결과를 처리하거나 상태를 업데이트할 수 있습니다.
  } catch (error) {
    console.error('검색 중 오류 발생:', error);
  }
};
</script>

<!-- <template>
    <div>
        <label class="input input-bordered flex items-center gap-2 rounded-badge">
            <input type="text" class="grow" placeholder="악보 검색" />
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="h-4 w-4 opacity-70">
                <path
                fill-rule="evenodd"
                d="M9.965 11.026a5 5 0 1 1 1.06-1.06l2.755 2.754a.75.75 0 1 1-1.06 1.06l-2.755-2.754ZM10.5 7a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0Z"
                clip-rule="evenodd" />
            </svg>
        </label>
    </div>
</template> -->
<template>
    <div>
      <label class="input input-bordered flex items-center gap-2 rounded-badge">
        <input
          v-model="keyword"
          @keyup.enter="searchSheets"
          type="text"
          class="grow"
          placeholder="악보 검색"
        />
        <svg
          @click="searchSheets"
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 16 16"
          fill="currentColor"
          class="h-4 w-4 opacity-70 cursor-pointer"
        >
          <path
            fill-rule="evenodd"
            d="M9.965 11.026a5 5 0 1 1 1.06-1.06l2.755 2.754a.75.75 0 1 1-1.06 1.06l-2.755-2.754ZM10.5 7a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0Z"
            clip-rule="evenodd"
          />
        </svg>
      </label>
    </div>
  </template>

<style scoped>

</style>