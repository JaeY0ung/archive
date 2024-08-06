<script setup>
import Sheet from '@/common/sheet/Sheet.vue';
import { useRoute} from 'vue-router';

const route = useRoute();
const sheetId = route.params.sheetId;
</script>

<template>
    <div stlye="width:100%; height:400px;" >
        <Sheet :sheet-id="sheetId" />
    </div>
</template>

<style scoped>

</style>