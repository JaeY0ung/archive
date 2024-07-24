<script setup>
import axios from 'axios';
import { useRoute } from 'vue-router'

const route = useRoute();

const searchSheetDetailById = () => {
    axios.get("http://localhost:8080/sheets/" + route.params.sheetId)
        .then(({data}) => {
            console.log(data);
        }).catch((err) => {
            alert(err)
        })
}
searchSheetDetailById()

</script>

<template>
    <div>
        
    </div>
</template>

<style scoped>

</style>