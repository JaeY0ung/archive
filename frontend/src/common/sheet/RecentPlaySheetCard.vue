<script setup>
import { watch } from 'vue';
import { useRouter } from 'vue-router'
import { useUserStore } from "@/stores/user";


const router = useRouter();
const userStore = useUserStore();

const props = defineProps({
    sheet: {
        type: Object,
        required: false,
        default: {
            id: 0,
            imageUrl: require('@/assets/img/default/song_img.png'),
            title: "",
            songComposer: "",
            level: 0,
            uploaderNickname: "",
            likeCount: 0,
            likeStatus: false,
            difficulty: 0,
            songImg: "",
        },
    },
});

watch(() => props.sheet, () => {
    props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
})

props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};

</script>

<template>
    <div class="container flex justify-between items-center bg-white shadow-lg rounded-3xl p-4 relative"  @click="goToSheetDetail(sheet.id)">
        <!-- 왼쪽 이미지 공간 -->
        <div class="flex justify-center items-center w-[200px] h-[200px] rounded-3xl bg-white shadow-inner overflow-hidden">
            <img  :src="sheet.imageUrl" alt="Sheet Image"  class="w-[90%] h-[90%]  object-contain" />
        </div>
        <!-- 텍스트 -->
        <div class="w-[50%]">
            <div class="ml-3 flex-grow text-gray-400 font-bold text-sm">
                Now Playing
            </div>
            <div class="ml-3 mb-12 flex-grow text-gray-600 font-bold text-lg">
                {{ sheet.title ? sheet.title : "최근 플레이한 곡이 없습니다" }}
            </div>
        </div>
        
        <!-- 오른쪽 아이콘들 -->
        <div class="flex flex-col justify-center items-center space-y-4">
            <button class="w-8 h-8 rounded-full bg-white bg-opacity-50 shadow-md flex justify-center items-center">
                <img v-if="sheet.likeStatus == true"  :src="require('@/assets/img/heart-fill.svg')" alt="꽉 찬 하트" class="w-7 h-7" @click="onClickDislikeSheet" />
                <img v-else-if="sheet.likeStatus == false" :src="require('@/assets/img/heart-empty.svg')"  alt="빈 하트" class="w-7 h-7" @click="onClickLikeSheet" />
            </button>
            <button class="w-8 h-8 rounded-full bg-white bg-opacity-50 shadow-md flex justify-center items-center">
                <span class="text-gray-400">≡</span>
            </button>
            <button class="w-8 h-8 rounded-full bg-white bg-opacity-50 shadow-md flex justify-center items-center">
                <span class="text-gray-400">🔗</span>
            </button>
        </div>
    </div>
</template>



<style scoped>
.line::before {
  content: "";
  display: block;
  border-left: 1px solid gray;
  height: 100%;
}
 .container {
   border: 1px solid #C9DEFF;
   border-radius: 12px;
   padding: 30px 28px;
   box-sizing: border-box;
   box-shadow: 0px 0px 6px 1px rgba(63, 128, 234, 0.2);
 }

.bold {
  font-weight: bold;
}

.scroll-y {
  overflow-y: auto;
}
</style>
