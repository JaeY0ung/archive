<script setup></script>

<template>
    <div class="profile-svg-container">
        <svg
            class="profile-svg"
            viewBox="0 0 600 600"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink"
        >
            <title>Abstract user icon</title>

            <defs>
                <clipPath id="circular-border">
                    <circle cx="300" cy="300" r="280" />
                </clipPath>
                <clipPath id="avoid-antialiasing-bugs">
                    <rect width="100%" height="498" />
                </clipPath>
            </defs>

            <circle
                cx="300"
                cy="300"
                r="280"
                fill="currentColor"
                clip-path="url(#avoid-antialiasing-bugs)"
            />
            <circle cx="300" cy="230" r="115" fill="white" />
            <circle cx="300" cy="550" r="205" fill="white" clip-path="url(#circular-border)" />
        </svg>
    </div>
</template>

<style scoped>
.profile-svg-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.profile-svg {
    width: 100%;
    height: 100%;
    fill: currentColor;
}
</style>
