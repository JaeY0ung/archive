<script setup>
import { ref } from 'vue';
const props = defineProps({
    inputTitle: { type: String, required: false, default: "title 값 전달해주세요." },
    type: { type: String, required: false, default: "text" },
    text: { type: String, required: false, default: "" },
    color: { type: String, required: false, default: "blue" },
})

const emit = defineEmits(['send-input']);

const getInput = () => {
    emit('send-input', inputValue.value)
    console.log("입력: ", inputValue.value)
}

// const status = ref('none') // none, success, fail

const inputValue = ref(props.text)
</script>

<template>
    <div>
        <label class="form-control w-full max-w-xs">
            <div class="label">
                <span class="label-text">{{ inputTitle }}</span>
                <!-- <span class="label-text-alt">Top Right label</span> -->
            </div>
            <input :type="type" @change="getInput" :placeholder="text" class="input input-bordered w-full max-w-xs" />
            <div class="label">
                <!-- <span v-if="status == 'success'" class="label-text-alt">올바른 {{ inputTitle }} 입니다.</span>
                <span v-else-if="status == 'fail'" class="label-text-alt">잘못된 {{ inputTitle }} 입니다.</span> -->
                <!-- <span class="label-text-alt">Bottom Right label</span> -->
            </div>
        </label>
    </div>
</template>

<style scoped>
</style>