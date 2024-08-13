<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router'
import { likeSheet, dislikeSheet } from '@/api/likesheet';
import Tier from "@/common/icons/Tier.vue"
import ModalComponent from '@/common/modal/ModalComponent';
import { addToOrder } from "@/util/order";

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

const sheetInfo = ref(props.sheet);
// 모달 표시 여부를 관리하는 상태 추가
const showModal = ref(false);

watch(() => props.sheet, (newSheet) => {
    sheetInfo.value = newSheet
    props.sheet.imageUrl = props.sheet.songImg ? `data:image/jpeg;base64,${props.sheet.songImg}` : require('@/assets/img/default/song_img.png');
})


const goToUserProfile = () => {
    router.push({ name: "userProfile", params: { nickName: props.sheet.uploaderNickname } });
};

// 싱글 배틀 페이지로 이동하기.
const goToPlayRoom = () => {
    router.push({ name: "singlePlay", params: { sheetId : props.sheet.id} });
};

// 좋아요
const onClickLikeSheet = async () => { 
    if (!isLogin.value) {
        alert("로그인 이후 가능합니다.");
        return;
    }
    likeSheet(
        props.sheet.id,
        (res)=>{
            sheetInfo.value.likeStatus = true;
            sheetInfo.value.likeCount++;
        }
    )
}

// 좋아요 해제
const onClickDislikeSheet = async () => {
    if (!isLogin.value) {
        alert("로그인 이후 가능합니다.");
        return;
    }
    dislikeSheet(props.sheet.id, (res) => {
        sheetInfo.value.likeStatus = false;
        sheetInfo.value.likeCount--;
    });
};

// TODO : 장바구니에 넣는 로직 추가하기
const addSheetToOrder = async () => {
    try {
        addToOrder(sheetInfo.value); // LocalStorage에 장바구니 추가
        showModal.value = true;
    } catch (error) {
        console.error("장바구니에 담기는 게 실패하였습니다.", error);
    }
    // 모달을 표시하도록 상태 변경
    // try {
    //   await addSheetToOrderAPI(sheetInfo.value.id, '카카오페이'); // API 호출로 장바구니에 추가
    //   showModal.value = true;
    // } catch (error) {
    //   console.error("장바구니에 담기는 게 실패하였습니다.", error);
    // }
};

const goToCart = () => {
    router.push({ name: "order" });
    showModal.value = false;
};

// 모달에서 계속 쇼핑하기
const continueShopping = () => {
    // 모달 숨기기
    showModal.value = false;
};

const goToDifficultyRatingPage = () => {
    router.push({ name: 'sheetDifficultyRating', params: { sheetId: sheetInfo.value.id } })
}
</script>

<template>
    <div
        class="flex flex-row gap-3 text-[1.25vw] bg-white bg-opacity-30 p-3 shadow-xl rounded-xl shadow-pink-500/30 "
    >
        <!-- 1. 악보 이미지 -->
        <div class="img flex justify-center items-center bg-white bg-opacity-30">
            <img
                class="song-img h-[150px] w-[150px] "
                :src="sheet.imageUrl"
                alt="원본 곡 이미지"
            />
        </div>
        <!-- 2. 악보 정보 -->
        <div class="flex flex-1 flex-col gap-2 bg-white bg-opacity-70 shadow-md rounded-xl p-1">
            <div class="items-center flex gap-2 justify-start w-full">
                <!-- 악보 제목 -->
                <div class="flex justify-start">
                    <div class="bold text-[2vw] mr-3 mb-2">
                        {{ sheet.title }}
                    </div>
                    <Tier :level="sheet.level" />
                </div>
                <!-- 유저 기준 악보 난이도 -->
                <div class="flex items-start">
                    <div class="flex items-center">
                        <!-- <img :src="require('@/assets/img/star-fill.svg')" alt="별" /> -->
                        <p>{{ sheet.difficulty ? sheet.difficulty : "Unrated" }}</p>
                    </div>
                </div>
            </div>

            <div class="text-xl flex flex-row gap-3">
                <div class="font-bold">작곡가</div>
                <p class="text-xl">{{ sheet.songComposer }}</p>
            </div>

            <div>
                <div class="text-xl flex flex-row gap-3">
                    <div class="font-bold">게시자</div>
                    <div class="cursor-pointer" @click="goToUserProfile">
                        {{ sheet.uploaderNickname }}
                    </div>
                </div>
            </div>
            <div class="flex items-center justify-between">
                <!-- 조회수 및 하트 -->
                <div class="flex items-start gap-3 text-lg">
                    <div class="flex items-center gap-2">
                        <img :src="require('@/assets/img/view.svg')" class="w-8 h-8" alt="눈" />
                        <p>{{ sheet.viewCount }}</p>
                    </div>
                    <div class="flex items-center gap-2 mt-0.5 cursor-pointer">
                        <img
                            v-if="sheetInfo.likeStatus === true"
                            :src="require('@/assets/img/heart-fill.svg')"
                            alt="꽉 찬 하트"
                            class="w-7 h-7"
                            @click="onClickDislikeSheet"
                        />
                        <img
                            v-else
                            :src="require('@/assets/img/heart-empty.svg')"
                            alt="빈 하트"
                            class="w-7 h-7"
                            @click="onClickLikeSheet"
                        />
                        {{ sheetInfo.likeCount }}
                    </div>
                </div>
                <!-- 난이도 기여, PLAY 버튼 -->
                <div class="flex">
                    <div>
                        <button class="btn bg-purple-500 bg-opacity-60 text-white w-[180px] h-10 max-w-[12vw]" @click="goToDifficultyRatingPage">
                            난이도 기여하러 가기
                        </button>
                    </div>

                    <button class="btn bg-gray-700 text-white w-[100px] h-1 max-w-[8vw]" @click="goToPlayRoom">
                        TEST
                    </button>
                </div>
            </div>
        </div>
        <!-- 세로 선 -->
        <div class="line"></div>
        <!-- 3. 장바구니 -->
        <div
            class="items-center w-[10%] bg-white bg-opacity-70 rounded-lg shadow-md flex flex-col justify-center gap-1 cursor-pointer"
            @click="addSheetToOrder"
        >
            <img :src="require('@/assets/img/cart.svg')" alt="장바구니 이미지" class="w-10 h-10" />

            <div class="flex gap-1 ">
                <img src="@/assets/img/cash.png" class="mt-2" style="width:15px; height: 15px;">
                <div class="text-lg text-gray-900">
                {{ sheet.price }}
                </div>
            </div>
           
			
        </div>
    </div>

    <!-- 모달 컴포넌트 사용 -->
    <ModalComponent
        :show="showModal"
        message="장바구니에 추가되었습니다."
        primaryButtonText="장바구니로 이동"
        secondaryButtonText="계속 쇼핑하기"
        @primaryAction="goToCart"
        @secondaryAction="continueShopping"
    />
</template>

<style scoped>
.line::before {
    content: "";
    display: block;
    border-left: 1px solid gray;
    height: 100%;
}
</style>
