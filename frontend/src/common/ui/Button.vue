<script setup>
import { ref, computed } from 'vue';

defineProps({
    color: { type: String, required: false, default: "btn-accent" }, // primary, secondary, accent, info, success, error
    text: { type: String, required: false, default: "제출" }
})


// const buttonClass = computed(() => {
//     return `bg-${color.value}-500 shadow-${color.value}-500/20 hover:shadow-${color.value}-500/40`;
// });
</script>

<template>
    <div>
        <button class="btn" :class="color">
            {{ text }}
        </button>

        <!-- <button type="submit" :class="buttonClass" class="select-none rounded-lg py-3 px-6 text-center align-middle font-sans text-xs font-bold uppercase text-white shadow-md transition-all hover:shadow-lg focus:opacity-[0.85] focus:shadow-none active:opacity-[0.85] active:shadow-none disabled:pointer-events-none disabled:opacity-50 disabled:shadow-none">
            {{ text }}
        </button> -->
    </div>
</template>

<style scoped>

</style>