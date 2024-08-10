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

onMounted(() => {
    if (props.sheets && props.sheets.length > 0) {
        selectedSheet.value = props.sheets[0];
        emit('send-sheet-id', props.sheets[0].id); // 첫 번째 시트의 ID를 emit
    }
});
</script>

<template>
    <div class="flex w-full h-full bg-red-600">
        <div class="flex flex-col flex-shrink-0 w-[30%] h-full bg-yellow-300">
            <div @click="emit('send-go-to-back')">뒤로가기</div>
            <div class="flex w-full " v-if="selectedSheet">
                <img 
                :src="selectedSheet.songImg ? `data:image/jpeg;base64,${selectedSheet?.songImg}` : require('@/assets/img/default/song_img.png')" 
                alt="선택한 악보 이미지" 
                class="object-cover w-full h-full"
                />
            </div>
            <div class="flex flex-col flex-grow w-full bg-blue-400" v-if="selectedSheet">
                {{ selectedSheet }}
            </div>
        </div>
        <div class="flex flex-grow flex-col h-full relative overflow-hidden items-center bg-green-300" >
            <div class="flex flex-col w-full h-full absolute overflow-y-scroll ">
                <SmallSheetCard v-for="sheet in sheets" :sheet="sheet" @click="onClickSheetCard(sheet)" class="cursor-pointer" :key="sheet.id"/>
            </div>
        </div>
	</div>
</template>

<style scoped>

</style>