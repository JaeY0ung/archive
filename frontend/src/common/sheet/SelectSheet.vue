<script setup>
import { ref } from 'vue';
import SmallSheetCard from './SmallSheetCard.vue';

const props = defineProps({
	sheets: Array,
})

const emit = defineEmits(['send-go-to-back', 'send-sheet-id'])
</script>

<template>
    <div>
		<div @click="emit('send-go-to-back')">뒤로가기</div>
		<SmallSheetCard v-for="sheet in sheets" :sheet="sheet" @click="emit('send-sheet-id', sheet.id)" class="cursor-pointer"/>
	</div>
</template>

<style scoped>
.card-box:has(div:hover) > div:not(:hover){
    filter: blur(4px)
}

.card-box:has(div:hover) > div:hover{
    width: 20vw;
}

.card{
    transition: all 150ms ease-in-out;
}
</style>