<script setup>
import { ref } from "vue";

const props = defineProps({
    show: {
        type: Boolean,
        required: true,
    },
    message: {
        type: String,
        required: true,
    },
    primaryButtonText: {
        type: String,
        required: true,
    },
    secondaryButtonText: {
        type: String,
        required: true,
    },
});

const emit = defineEmits(["primaryAction", "secondaryAction"]);

const onPrimaryAction = () => {
    emit("primaryAction");
};

const onSecondaryAction = () => {
    emit("secondaryAction");
};
</script>

<template>
    <Transition name="fade">
        <div
            v-if="show"
            class="modal-overlay fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
        >
            <div class="modal-content bg-white p-6 rounded-lg shadow-lg text-center">
                <p>{{ message }}</p>
                <div class="mt-4 flex justify-around gap-4">
                    <button class="btn btn-primary" @click="onPrimaryAction">
                        {{ primaryButtonText }}
                    </button>
                    <button class="btn btn-secondary" @click="onSecondaryAction">
                        {{ secondaryButtonText }}
                    </button>
                </div>
            </div>
        </div>
    </Transition>
</template>

<style scoped>
.modal-overlay {
    z-index: 10;
}

.modal-content {
    z-index: 11;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>
