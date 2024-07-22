<script setup>
import { useThemeStore } from '@/stores/theme'
import { storeToRefs } from "pinia";

const themeStore = useThemeStore();
</script>

<template>
    <div>
        <div class="form-control w-24">
            <label class="label cursor-pointer">
                <span class="label-text">테마</span>
                <input type="checkbox" class="toggle toggle-primary" checked="checked" @change="themeStore.switchTheme" />
            </label>
        </div>
    </div>
</template>

<style scoped>

</style>