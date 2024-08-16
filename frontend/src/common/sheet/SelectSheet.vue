<script setup>
import { ref, onMounted } from 'vue';
import SmallSheetCard from './SmallSheetCard.vue';

const props = defineProps({
    sheets: Array,
})

const emit = defineEmits(['send-go-to-back', 'send-sheet-id']);

const selectedSheet = ref(null);

const onClickSheetCard = (sheet) => {
    selectedSheet.value = sheet;
    emit('send-sheet-id', sheet.id);
};

onMounted(() => {
    // 첫 번째 시트를 자동으로 선택하지 않도록 onMounted에서 초기 선택 제거
    selectedSheet.value = null;
    // 사용자가 직접 첫 번째 선택을 해야 selectedSheet에 값이 들어가게 함
});

</script>

<template>
    <div class="flex w-full h-full z-1 bg-[#f0f4ff] bg-opacity-80"> <!-- 배경색 설정 -->
        <!-- 좌 : 고른 악보 정보 -->
        <div class="flex flex-col flex-shrink-0 w-[40%] h-full gap-4 relative p-4">
            <!-- 1. 뒤로 가기 버튼 -->
            <div 
                class="flex bg-white p-4 justify-center items-center h-[10%] w-[60%] rounded-xl m-4 text-[#4A90E2] text-2xl font-bold cursor-pointer hover:bg-[#e6f0fb] transition-colors shadow-lg" 
                @click="emit('send-go-to-back')"> 
                <  뒤로 가기
            </div>
            <!-- 2. 선택 악보 -->
            <div class="custom-shadow-sky flex flex-col justify-center items-center h-full bg-[#f3f7fd] rounded-xl shadow-xl p-4">
                <!-- 선택 악보 이미지 -->
                <div class="flex justify-center items-center w-full h-[60%]">
                    <img 
                        v-if="selectedSheet"
                        :src="selectedSheet.songImg ? `data:image/jpeg;base64,${selectedSheet?.songImg}` : require('@/assets/img/default/song_img.png')" 
                        alt="선택한 악보 이미지" 
                        class="object-cover w-[55%] max-w-[250px] rounded-2xl shadow-2xl"
                    />
                </div>
                <!-- 선택 악보 정보 -->
                <div class="flex items-center justify-center w-full h-[40%]" v-if="selectedSheet">
                    <div class="flex flex-col justify-center flex-grow h-full w-[70%] ">
                        <div class="flex flex-row m-4 justify-around items-center p-4 pr-10 pl-4 mb-2 rounded-xl bg-[#f0f4ff] shadow-lg custom-shadow-blue">
                            <div class="text-sm w-[20%] text-gray-700">제목</div>
                            <div 
                            class="text-lg text-center font-bold text-[#4A90E2] truncate">
                            {{ selectedSheet.title.length > 12 ? selectedSheet.title.slice(0, 12) + '...' : selectedSheet.title }}
                        </div>
                        </div>
                        <div class="flex flex-row m-4 justify-around items-center p-4 mt-2 pr-10 pl-4 rounded-xl bg-[#f0f4ff] shadow-lg custom-shadow-blue">
                            <div class="text-sm text-gray-700">업로더/아티스트</div>
                            <div class="text-lg text-center font-bold text-[#4A90E2]">{{selectedSheet.uploaderNickname}}</div>
                        </div>   
                    </div>
                    <!-- 곡 레벨 -->
                    <div class="flex flex-col h-[60%] justify-center items-center bg-gray-100 shadow-lg rounded-xl custom-shadow-blue">
                        <div class="pr-4 pl-4 text-xl font-bold text-gray-700 rounded-xl">LEVEL</div>
                        <div class="flex flex-col items-center justify-center w-full ">
                            <span class="text-3xl font-bold text-[#4A90E2]">{{ selectedSheet.level }}</span> 
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 우 : 악보 목록 -->
        <div class="flex flex-grow flex-col h-full w-[50%] relative overflow-hidden items-center">
            <div 
                class="grid grid-cols-1 sm:grid-cols-2 gap-4 w-full h-full absolute justify-center items-center overflow-y-scroll p-4">
                <SmallSheetCard
                    v-for="sheet in sheets"
                    :key="sheet.id"
                    :sheet="sheet"
                    :restrictTitle="true"
                    @click="onClickSheetCard(sheet)"
                    class="w-full bg-white rounded-xl shadow-lg hover:shadow-xl transition-shadow p-4"
                />
            </div>
        </div>
    </div>
</template>

<style scoped>
.custom-shadow-blue {
    box-shadow: 0 4px 10px rgba(66, 153, 225, 0.4), 0 2px 4px rgba(66, 153, 225, 0.3); /* 하늘색 그림자 */
}


.custom-shadow-sky {
    box-shadow: 0 4px 10px rgba(66, 153, 225, 0.2), 0 2px 4px rgba(66, 153, 225, 0.15); /* 더 연한 하늘색 그림자 */
}

</style>
