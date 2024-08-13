<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router'
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";


const router = useRouter();
const userStore = useUserStore();
const { userInfo, isLogin } = storeToRefs(userStore);

const props = defineProps({
    sheet: {
        type: Object,
        required: true,
        default: {
            id: Number,
            imageUrl: String,
            title: String,
            songComposer: String,
            level: Number,
            uploaderNickname: String,
            likeCount: Number,
            likeStatus: Boolean,
            difficulty: Number, // 아직 안보냄
        },
    },
});

const recentChallengedSheet = ref({}); // 최근에 도전했던 악보

const getRecentChallengedsheets = async () => {
	searchRecentChallengedsheets(
		({ data }) => {
			if (!data) return;
			recentChallengedSheet.value = data;
		}
	)
}
//getRecentChallengedsheets();

const sheetInfo = ref(props.sheet);

watch(() => props.sheet, (newSheet) => {
    sheetInfo.value = newSheet
    props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
})

const goToSheetDetail = (sheetId) => {
	router.push({ name: 'sheetDetail', params: { sheetId } });
};

</script>

<template>
    <div class="flex items-center bg-white shadow-lg rounded-3xl p-4 w-[500px] h-[160px] relative"  
    style="background: linear-gradient(145deg, rgba(255, 255, 255, 0.8), rgba(200, 200, 200, 0.8));" 
    @click="goToSheetDetail(sheet.id)" >
        <!-- 왼쪽 이미지 공간 -->
        <div class="flex justify-center items-center w-[100px] h-[100px] rounded-3xl bg-white shadow-inner overflow-hidden">
            <img
                :src="recentChallengedSheet.imageUrl || require('@/assets/img/default/song_img.png')"
                alt="Sheet Image"
                class="w-[90%] h-[90%]  object-contain"
            />
        </div>
        <!-- 텍스트 -->
        <div class="w-[50%]">
            <div class="ml-3 flex-grow text-gray-400 font-bold text-sm">
                Now Playing
            </div>
            <div class="ml-3 mb-12 flex-grow text-gray-600 font-bold text-lg">
                <!-- {{recentChallengedSheet.title}} -->
                노래 제목
            </div>
        </div>
        
        <!-- 오른쪽 아이콘들 -->
        <div class="flex flex-col justify-center items-center space-y-4">
            <button class="w-8 h-8 rounded-full bg-white bg-opacity-50 shadow-md flex justify-center items-center">
                <span class="text-purple-500">💜</span>
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
</style>
