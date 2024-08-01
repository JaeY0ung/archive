<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
    title: String,
    followList: Array,
});

const emit = defineEmits(["close"]);

const closeModal = () => {
    emit("close");
};
</script>

<template>
    <div class="custom-modal-overlay">
        <div class="custom-modal">
            <div class="custom-modal-content">
                <h2 class="custom-modal-title">{{ title }}</h2>
                <ul class="custom-modal-list">
                    <li v-for="user in followList" :key="user.id" class="custom-modal-list-item">
                        <img :src="user.userImg" :alt="user.nickname" class="user-image">
                        <span class="user-nickname">{{ user.nickname }}</span>
                    </li>
                </ul>
                <button @click="closeModal" class="custom-modal-close-btn">Close</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.custom-modal-overlay {
    position: fixed;
    z-index: 999;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.custom-modal {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    max-width: 500px;
    width: 90%;
}

.custom-modal-content {
    background-color: #fefefe;
    padding: 20px;
    border: 1px solid #888;
    width: 100%;
    border-radius: 8px;
}

.custom-modal-title {
    margin-top: 0;
    font-size: 1.5em;
    color: #333;
}

.custom-modal-list {
    list-style-type: none;
    padding: 0;
}

.custom-modal-list-item {
    padding: 10px 0;
    border-bottom: 1px solid #eee;
    display: flex;
    align-items: center;
}

.user-image {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 10px;
}

.user-nickname {
    font-size: 1.1em;
}

.custom-modal-close-btn {
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #4444ff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.custom-modal-close-btn:hover {
    background-color: #3333dd;
}
</style>