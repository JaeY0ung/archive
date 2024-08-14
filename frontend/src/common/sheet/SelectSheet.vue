<script setup>
import { ref, onMounted } from 'vue';
import SmallSheetCard from './SmallSheetCard.vue';

const props = defineProps({
	sheets: Array,
})

const emit = defineEmits(['send-go-to-back', 'send-sheet-id']);

const selectedSheet = ref(null);

const onClickSheetCard = (sheet) => {
    console.log(sheet)
    selectedSheet.value = sheet;
    emit('send-sheet-id', sheet.id);
};

// onMounted(() => {
//     if (props.sheets && props.sheets.length > 0) {
//         selectedSheet.value = props.sheets[0];
//         emit('send-sheet-id', props.sheets[0].id); // 첫 번째 시트의 ID를 emit
//     }
// });

onMounted(() => {
    // 첫 번째 시트를 자동으로 선택하지 않도록 onMounted에서 초기 선택 제거
    selectedSheet.value = null;
    // 사용자가 직접 첫 번째 선택을 해야 selectedSheet에 값이 들어가게 함
});

</script>

<template>
    <div class="flex w-full h-full  z-1 bg-black bg-opacity-20"
    :style="{
        backgroundImage: `url(${require('@/assets/img/sheet_play/play-background-select.png')})`,
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        backgroundBlendMode: 'multiply',
    }">
        <!-- 좌 : 고른 악보 정보 -->
        <div class="flex flex-col flex-shrink-0 w-[40%] h-full gap-4">
            <!-- 1. 뒤로 가기 + 선택한 악보 제목 -->
            <div 
                class="flex justify-center items-center shadow-md h-[10%] w-[40%] rounded-xl m-4 text-gray-600 text-2xl font-bold" 
                @click="emit('send-go-to-back')"
                :style="{
                        backgroundImage: `url(${require('@/assets/img/sheet_play/box_green.png')})`,
                        backgroundSize: '100% 100%', // 배경 이미지가 요소에 딱 맞게 조정됨
                        backgroundPosition: 'center',
                        backgroundRepeat: 'no-repeat',
                        }"
                > <  뒤로 가기</div>
            <!-- 2. 선택 악보 -->
            <div class="flex flex-col justify-center items-center h-[60%] m-4 gap-1">
                <!-- 선택 악보 이미지 -->
                <div class="flex justify-center items-start w-full mb-2" v-if="selectedSheet">
                    <img 
                    :src="selectedSheet.songImg ? `data:image/jpeg;base64,${selectedSheet?.songImg}` : require('@/assets/img/default/song_img.png')" 
                    alt="선택한 악보 이미지" 
                    class="object-cover w-[55%] rounded-2xl shadow-2xl"
                    />
                </div>
                <!-- 선택 악보 정보 -->
                <div class="flex items-center justify-center w-full h-[40%]" v-if="selectedSheet">
                    <div class="flex flex-col justify-center flex-grow h-full w-[70%] ">
                        <div class="flex flex-row m-4 justify-around items-center p-4 pr-10 pl-4 mb-2 rounded-xl shadow-md"
                        :style="{
                        backgroundImage: `url(${require('@/assets/img/sheet_play/box_blue.png')})`,
                        backgroundSize: '100% 100%', // 배경 이미지가 요소에 딱 맞게 조정됨
                        backgroundPosition: 'center',
                        backgroundRepeat: 'no-repeat',
                        }">
                            <div class="text-sm text-gray-700">제목</div>
                            <div class="text-lg text-center font-bold">{{selectedSheet.title}}</div>
                        </div>
                        <div class="flex flex-row m-4 justify-around items-center p-4 mt-2 pr-10 pl-4 rounded-xl shadow-md"
                        :style="{
                        backgroundImage: `url(${require('@/assets/img/sheet_play/box_purple.png')})`,
                        backgroundSize: '100% 100%', // 배경 이미지가 요소에 딱 맞게 조정됨
                        backgroundPosition: 'center',
                        backgroundRepeat: 'no-repeat',
                        }">
                            <div class="text-sm text-gray-700">업로더/작곡가</div>
                            <div class="text-lg text-center font-bold">{{selectedSheet.uploaderNickname}}</div>
                        </div>   
                    </div>
                    <!--곡 레벨 -->
                    <div class="flex flex-col justify-center items-center gap-2">
                        <div class=" h-full pr-4 pl-4 text-2xl font-bold text-gray-700 rounded-xl"
                        :style="{
                            backgroundImage: `url(${require('@/assets/img/sheet_play/box_green.png')})`,
                            backgroundSize: '90% 90%', 
                            backgroundPosition: 'center',
                            backgroundRepeat: 'no-repeat',
                        }"
                        >LEVEL</div>
                        <div class="flex flex-col items-center justify-center h-full w-full pl-4 pr-4 rounded-xl"
                        :style="{
                                backgroundImage: `url(${require('@/assets/img/sheet_play/box_yellow.png')})`,
                                backgroundSize: '100% 100%',
                                backgroundPosition: 'center',
                                backgroundRepeat: 'no-repeat',
                            }">
                            

                            <img v-if="selectedSheet.level === 0" src="@/assets/img/sheet_play/number0.png" alt="Level 1" class="w-[90%] h-[90%] object-contain p-2" />
                            <img v-else-if="selectedSheet.level === 1" src="@/assets/img/sheet_play/number1.png" alt="Level 1" class="w-[90%] h-[90%]  object-contain p-2" />
                            <img v-else-if="selectedSheet.level === 2" src="@/assets/img/sheet_play/number2.png" alt="Level 2" class="w-[90%] h-[90%]  object-contain p-2" />
                            <img v-else-if="selectedSheet.level === 3" src="@/assets/img/sheet_play/number3.png" alt="Level 3" class="w-[90%] h-[90%]  object-contain p-2" />
                            <img v-else-if="selectedSheet.level === 4" src="@/assets/img/sheet_play/number4.png" alt="Level 4" class="w-[90%] h-[90%]  object-contain p-2" />
                            <span v-else class="text-xl font-bold">{{ selectedSheet.level }}</span> <!-- level이 1, 2, 3이 아닐 때 -->
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
                    :restrictTitle="false"
                    @click="onClickSheetCard(sheet)"
                    class="w-full"
                />
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>