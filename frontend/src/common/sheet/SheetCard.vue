<!-- <script setup>
const props = defineProps({
    card: {
        type: Object,
        required: false,
        default: {
            img: "이미지",
            title: "하입 보이", // 악보 제목
            uploader: "user1", // 악보 제공자
            viewCount: "", // 악보 횟수
        }
    } 
})


watch(() => props.card, () => {
	props.card.imageUrl = props.card.img ? `data:image/jpeg;base64,${props.card.img}` : require('@/assets/img/default/song_img.png');
})
props.card.imageUrl = props.card.img ? `data:image/jpeg;base64,${props.card.img}` : require('@/assets/img/default/song_img.png');
</script>

<template>
    <div>
        이미지 {{ card.img }}
        제목 {{ card.title }}
        업로더 {{ card.uploader }}
        몇 명 봤나? {{ card.viewCount }}
    </div>
</template>

<style scoped>

</style> -->