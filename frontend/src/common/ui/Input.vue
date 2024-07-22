<script setup>
import { ref } from 'vue';
defineProps({
    inputTitle: { type: String, required: false, default: "title 값 전달해주세요." },
})

const emit = defineEmits(['send-input']);

const getInput = () => {
    emit('send-input', inputValue.value)
}

const color = ref("blue")

const inputValue = ref("")
</script>

<template>
    <div class="relative w-full min-w-[200px] h-10">
        <input @change="getInput" :class="`peer w-full h-full bg-transparent text-${ color }-gray-700 font-sans font-normal outline outline-0 focus:outline-0 disabled:bg-${ color }-gray-50 disabled:border-0 transition-all placeholder-shown:border placeholder-shown:border-${ color }-gray-200 placeholder-shown:border-t-${ color }-gray-200 border focus:border-2 border-t-transparent focus:border-t-transparent text-sm px-3 py-2.5 rounded-[7px] border-${ color }-gray-200 focus:border-${ color }-500`" placeholder=" " v-model=inputValue />
        <label :class="`flex w-full h-full select-none pointer-events-none absolute left-0 font-normal !overflow-visible truncate peer-placeholder-shown:text-${ color }-gray-500 leading-tight peer-focus:leading-tight peer-disabled:text-transparent peer-disabled:peer-placeholder-shown:text-${ color }-gray-500 transition-all -top-1.5 peer-placeholder-shown:text-sm text-[11px] peer-focus:text-[11px] before:content[' '] before:block before:box-border before:w-2.5 before:h-1.5 before:mt-[6.5px] before:mr-1 peer-placeholder-shown:before:border-transparent before:rounded-tl-md before:border-t peer-focus:before:border-t-2 before:border-l peer-focus:before:border-l-2 before:pointer-events-none before:transition-all peer-disabled:before:border-transparent after:content[' '] after:block after:flex-grow after:box-border after:w-2.5 after:h-1.5 after:mt-[6.5px] after:ml-1 peer-placeholder-shown:after:border-transparent after:rounded-tr-md after:border-t peer-focus:after:border-t-2 after:border-r peer-focus:after:border-r-2 after:pointer-events-none after:transition-all peer-disabled:after:border-transparent peer-placeholder-shown:leading-[3.75] text-${ color }-gray-400 peer-focus:text-${ color }-500 before:border-${ color }-gray-200 peer-focus:before:!border-${ color }-500 after:border-${ color }-gray-200 peer-focus:after:!border-${ color }-500`">{{ inputTitle }}
        </label>
    </div>
</template>

<style scoped>

</style>