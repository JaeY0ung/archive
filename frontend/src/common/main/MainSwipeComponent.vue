<script setup>
import { ref, onMounted } from "vue";
import { Carousel, Slide } from "vue3-carousel";
import "vue3-carousel/dist/carousel.css";
import SmallSheetCardForPhoto from "@/common/sheet/SmallSheetCardForPhoto.vue";

const props = defineProps({
    sheets: Array,
    title: String,
});
const emit = defineEmits(["goToSheetDetail", "goToSheetSearchListView"]);

const carouselRef = ref(null);

onMounted(() => {
    if (carouselRef.value) {
        carouselRef.value.$el.addEventListener("mouseenter", () => {
            carouselRef.value.pauseAutoplay();
        });
        carouselRef.value.$el.addEventListener("mouseleave", () => {
            carouselRef.value.resumeAutoplay();
        });
    }
});
</script>

<template>
    <div
        class="container w-full h-full flex flex-col bg-gradient-to-br from-blue-50 to-white"
    >
        <h2 class="text-2xl font-bold mb-6 text-gray-800 border-b pb-2">
            {{ title }}
        </h2>
        <div class="flex-grow relative overflow-hidden rounded-lg">
            <template v-if="sheets && sheets.length > 0">
                <Carousel
                    ref="carouselRef"
                    :items-to-show="1"
        
                    :transition="500"
                    :autoplay="5000"
                    :pause-autoplay-on-hover="true"
                    class="h-full"
                >
                    <Slide
                        v-for="sheet in sheets"
                        :key="sheet.id"
                        class="carousel__item"
                    >
                        <SmallSheetCardForPhoto
                            class="bg-white rounded-lg shadow-lg h-full duration-300 hover:scale-100"
                            :sheet="sheet"
                            @click="emit('goToSheetDetail', sheet.id)"
                        />
                    </Slide>

                    <template #addons>
                        <!-- <div class="carousel__navigation">
                            <button
                                class="carousel__prev"
                                @click="carouselRef.prev()"
                            >
                                <span class="sr-only">Previous</span>
                                <svg
                                    class="w-6 h-6"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        stroke-width="2"
                                        d="M15 19l-7-7 7-7"
                                    ></path>
                                </svg>
                            </button>
                            <button
                                class="carousel__next"
                                @click="carouselRef.next()"
                            >
                                <span class="sr-only">Next</span>
                                <svg
                                    class="w-6 h-6"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                    xmlns="http://www.w3.org/2000/svg"
                                >
                                    <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        stroke-width="2"
                                        d="M9 5l7 7-7 7"
                                    ></path>
                                </svg>
                            </button>
                        </div> -->
                    </template>
                </Carousel>
            </template>
            <template v-else>
                <div
                    class="w-full h-full flex justify-center items-center text-gray-500 bg-gray-100 rounded-lg"
                >
                    <p class="text-lg">현재 표시할 악보가 없습니다.</p>
                </div>
            </template>
        </div>
        <!-- <div class="mt-6 text-right">
            <button
                @click="emit('goToSheetSearchListView', title)"
                class="bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-full transition-colors duration-300 flex items-center justify-center ml-auto"
            >
                더 보기
                <svg
                    class="w-4 h-4 ml-2"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M14 5l7 7m0 0l-7 7m7-7H3"
                    ></path>
                </svg>
            </button>
        </div> -->
    </div>
</template>

<style scoped>
.container {
    border: 1px solid #e2e8f0;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
        0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.carousel__item {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 20px;
}

:deep(.carousel__slide) {
    padding: 0;
}

:deep(.carousel__pagination) {
    padding-top: 16px;
}

:deep(.carousel__pagination-button::after) {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: #cbd5e0;
    transition: background-color 0.3s ease;
}

:deep(.carousel__pagination-button--active::after) {
    background-color: #3b82f6;
}

.carousel__navigation {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    transform: translateY(-50%);
    display: flex;
    justify-content: space-between;
    padding: 0 10px;
}

.carousel__prev,
.carousel__next {
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #4a5568;
    transition: all 0.3s ease;
}

.carousel__prev:hover,
.carousel__next:hover {
    background-color: rgba(255, 255, 255, 0.9);
    color: #2d3748;
}
</style>
