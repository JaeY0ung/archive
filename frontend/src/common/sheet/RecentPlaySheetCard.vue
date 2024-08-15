<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useUserStore } from "@/stores/user";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
    faMusic,
    faHeart,
    faStar,
    faPlay,
    faLock,
} from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

library.add(faMusic, faHeart, faStar, faPlay, faLock);

const userStore = useUserStore();
const { userInfo, isLogin } = storeToRefs(userStore);

const router = useRouter();

const props = defineProps({
    sheet: {
        type: Object,
        required: true,
        default: () => ({
            id: 0,
            title: "",
            songComposer: "",
            level: 0,
            uploaderNickname: "",
            likeCount: 0,
            likeStatus: false,
            difficulty: 0,
            songImg: "",
        }),
    },
});

const songImage = ref("");

watch(() => props.sheet.songImg, updateSongImage, { immediate: true });

function updateSongImage() {
    if (props.sheet.songImg) {
        songImage.value = `data:image/jpeg;base64,${props.sheet.songImg}`;
    } else {
        songImage.value = new URL(
            "@/assets/img/default/song_img.png",
            import.meta.url
        ).href;
    }
}

const goToPlayRoom = () => {
    router.push({ name: "singlePlay", params: { sheetId: props.sheet.id } });
};

onMounted(() => {
    updateSongImage();
});
</script>

<template>
    <div class="current-sheet-card w-full">
        <div v-if="isLogin" class="action-container">
            <div class="content-wrapper">
                <div class="sheet-cover">
                    <img :src="songImage" :alt="sheet.title" />
                </div>
                <div class="info-container">
                    <div class="current-challenge">
                        <font-awesome-icon :icon="['fas', 'music']" /> 최근 도전한 곡
                    </div>
                    <h2 class="sheet-title">{{ sheet.title }}</h2>
                    <p class="composer-name">
                        {{ sheet.songComposer }}
                    </p>
                </div>
            </div>

            <button @click="goToPlayRoom" class="play-button">
                <font-awesome-icon :icon="['fas', 'play']" /> 플레이하기
            </button>
        </div>
        <div v-else class="login-message">
            <font-awesome-icon :icon="['fas', 'lock']" />
            로그인 후 원하는 곡을 연주해보세요!
        </div>
    </div>
</template>

<style scoped>
.current-sheet-card {
    background: #ffffff;
    border-radius: 30px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
    color: #333333;
    display: flex;
    flex-direction: column;
    padding: 25px;
    width: 100%;
    max-width: 350px;
    margin: 20px auto;
    transition: all 0.3s ease;
}

.current-sheet-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.content-wrapper {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
}

.sheet-cover {
    width: 100px;
    height: 100px;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    flex-shrink: 0;
}

.sheet-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.info-container {
    flex-grow: 1;
    margin-left: 25px;
    overflow: hidden;
}

.current-challenge {
    font-size: 0.95rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 12px;
    color: #3498db;
    display: flex;
    align-items: center;
    background-color: #e8f4fd;
    padding: 8px 15px;
    border-radius: 20px;
}

.current-challenge svg {
    margin-right: 10px;
}

.sheet-title {
    font-size: 1.5rem;
    font-weight: bold;
    margin: 0 0 12px 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #2c3e50;
}

.composer-name {
    font-size: 1.1rem;
    margin: 0;
    padding: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #7f8c8d;
}

.action-container {
    margin-top: 25px;
}

.play-button {
    background-color: #3498db;
    color: white;
    border: none;
    padding: 18px 25px;
    border-radius: 30px;
    font-size: 1.3rem;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    box-shadow: 0 10px 20px rgba(52, 152, 219, 0.3);
}

.play-button svg {
    margin-right: 12px;
}

.play-button:hover {
    background-color: #2980b9;
    transform: translateY(-3px);
    box-shadow: 0 15px 25px rgba(52, 152, 219, 0.4);
}

.login-message {
    background-color: #f1f3f5;
    color: #34495e;
    padding: 18px 25px;
    border-radius: 25px;
    font-size: 1.1rem;
    font-weight: bold;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 25px;
    box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.login-message svg {
    margin-right: 12px;
    color: #3498db;
}
</style>