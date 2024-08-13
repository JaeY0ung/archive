<script setup>
import { computed } from "vue";
import { useRouter } from 'vue-router';
import Profile from "@/common/icons/Profile.vue";

const props = defineProps({
    title: String,
    followList: Array,
});
const emit = defineEmits(["close"]);

const closeModal = () => {
    emit("close");
};

const getImageUrl = (base64String) => {
    return base64String ? `data:image/jpeg;base64,${base64String}` : null;
};

const followListWithImageUrls = computed(() => {
    return props.followList.map(user => ({
        ...user,
        imageUrl: getImageUrl(user.userImg)
    }));
});

const goToUserProfile = (nickname) => {
    window.location.href = `/user/${nickname}/profile`;
    closeModal();
};
</script>

<template>
    <div class="custom-modal-overlay" @click="closeModal">
        <div class="custom-modal" @click.stop>
            <div class="custom-modal-content">
                <ul class="custom-modal-list">
                    <li v-for="user in followListWithImageUrls" :key="user.id" class="custom-modal-list-item">
                        <div class="user-image-container">
                            <img v-if="user.imageUrl" :src="user.imageUrl" :alt="user.nickname" class="user-image">
                            <Profile v-else class="user-image profile-icon" />
                        </div>
                        <span @click="goToUserProfile(user.nickname)" class="user-nickname clickable">
                            {{ user.nickname }}
                        </span>
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
    backdrop-filter: blur(5px);
}

.custom-modal {
    background-color: #fff;
    border-radius: 15px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    max-width: 400px;
    width: 90%;
    transform: scale(0.9);
    transition: transform 0.3s ease-in-out;
    animation: modalAppear 0.3s forwards;
}

@keyframes modalAppear {
    from {
        opacity: 0;
        transform: scale(0.8);
    }
    to {
        opacity: 1;
        transform: scale(1);
    }
}

.custom-modal-content {
    background-color: #fefefe;
    padding: 30px;
    width: 100%;
    border-radius: 15px;
}

.custom-modal-title {
    margin-top: 0;
    margin-bottom: 20px;
    font-size: 1.8em;
    color: #333;
    text-align: center;
    font-weight: 600;
}

.custom-modal-list {
    list-style-type: none;
    padding: 0;
    max-height: 300px;
    overflow-y: auto;
    scrollbar-width: none;  
    -ms-overflow-style: none;  
}

.custom-modal-list::-webkit-scrollbar {
    width: 0;
    height: 0;
    display: none;
}

.custom-modal-list-item {
    padding: 15px 0;
    border-bottom: 1px solid #eee;
    display: flex;
    align-items: center;
    transition: background-color 0.2s;
}

.custom-modal-list-item:hover {
    background-color: #f9f9f9;
}

.user-image-container {
    width: 50px;
    height: 50px;
    margin-right: 15px;
}

.user-image {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.profile-icon {
    color: #666;
    background-color: #f0f0f0;
}

.user-nickname {
    font-size: 1.2em;
    font-weight: 500;
}

.custom-modal-close-btn {
    margin-top: 25px;
    padding: 12px 25px;
    background-color: #4444ff;
    color: white;
    border: none;
    border-radius: 25px;
    cursor: pointer;
    font-size: 1em;
    font-weight: 600;
    transition: background-color 0.3s, transform 0.2s;
    display: block;
    width: 100%;
}

.custom-modal-close-btn:hover {
    background-color: #3333dd;
    transform: translateY(-2px);
}

.user-nickname.clickable {
    cursor: pointer;
    color: #4444ff;
    transition: color 0.2s;
}

.user-nickname.clickable:hover {
    color: #3333dd;
    text-decoration: underline;
}

.custom-modal-list::-webkit-scrollbar {
    display: none;
}
</style>