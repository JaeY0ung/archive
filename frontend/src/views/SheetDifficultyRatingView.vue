<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute } from "vue-router";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import { onClickOutside } from "@vueuse/core";
import BigSheetCard from "@/common/sheet/BigSheetCardForDifficulty.vue";
import {
    Chart,
    TimeScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    LineController,
    ScatterController,
    CategoryScale,
} from "chart.js";
import "chartjs-adapter-date-fns";
import { ko } from "date-fns/locale";
import { faL } from "@fortawesome/free-solid-svg-icons";

Chart.register(
    TimeScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend,
    LineController,
    ScatterController,
    CategoryScale
);

const userStore = useUserStore();
const route = useRoute();
const local = localAxios();
const { userInfo } = storeToRefs(userStore);

const sheet = ref({});
const newComment = ref("");
const newDifficulty = ref("");
const comments = ref([]);
const sortOrder = ref("desc");
const editingComment = ref(null);
const editedContent = ref("");
const editedDifficulty = ref("");
const menuOpenState = ref({});
const menuRef = ref(null);

const difficultyOptions = ["브론즈", "실버", "골드", "플래티넘", "다이아몬드"];
const difficultyMap = {
    1: "브론즈",
    2: "실버",
    3: "골드",
    4: "플래티넘",
    5: "다이아몬드",
};

const currentPage = ref(1);
const itemsPerPage = 5;

const chartRef = ref(null);
let chart = null;

const paginatedComments = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return comments.value.slice(start, end);
});

const totalPages = computed(() => Math.ceil(comments.value.length / itemsPerPage));

const getImageUrl = (base64String) => {
    return base64String ? `data:image/jpeg;base64,${base64String}` : "";
};

const searchSheetDetail = () => {
    local
        .get(`/sheets/${route.params.sheetId}`)
        .then(({ data }) => {
            sheet.value = data;
            sheet.value.imageUrl = `data:image/jpeg;base64,${data.songImg}`;
        })
        .catch((err) => {
            console.error("악보 정보를 가져오는 데 실패했습니다:", err);
            alert("악보 정보를 불러오는 데 실패했습니다.");
        });
};

const setDifficulty = (difficulty) => {
    newDifficulty.value = difficulty;
};

const submitCommentAndDifficulty = () => {
    if (!newDifficulty.value) {
        alert("난이도를 선택해주세요.");
        return;
    }

    if (newComment.value.trim() && newDifficulty.value) {
        const difficultyLevel = difficultyOptions.indexOf(newDifficulty.value) + 1;

        const difficultyData = {
            level: difficultyLevel,
            content: newComment.value.trim(),
        };

        local
            .post(`/sheets/${route.params.sheetId}/difficulties`, difficultyData)
            .then((response) => {
                fetchCommentsAndDifficulties();
                newComment.value = "";
                newDifficulty.value = "";
            })
            .catch((error) => {
                console.error("난이도 평가 저장 중 오류 발생:", error);
                if (error.response && error.response.data) {
                    alert(error.response.data);
                } else {
                    alert("난이도 평가 저장에 실패했습니다. 다시 시도해 주세요.");
                }
            });
    }
};

const fetchCommentsAndDifficulties = () => {
    local
        .get(`/sheets/${route.params.sheetId}/difficulties/all`)
        .then(({ data }) => {
            if (data) {
                comments.value = data.map((item) => ({
                    difficultyId: item.difficultyId,
                    username: item.username,
                    userAvatar: getImageUrl(item.userImg),
                    sheetTitle: item.sheetTitle,
                    sheetFileName: item.sheetFileName,
                    difficulty: difficultyMap[item.level] || "알 수 없음",
                    text: item.content,
                    createdAt: item.createdAt,
                }));
                updateChart();
            } else {
                comments.value = [];
            }
        })
        .catch((err) => {
            console.error("난이도 평가 목록을 가져오는 데 실패했습니다:", err);
            alert("난이도 평가 목록을 불러오는 데 실패했습니다.");
            comments.value = [];
        });
};

const toggleSortOrder = () => {
    sortOrder.value = sortOrder.value === "desc" ? "asc" : "desc";
    comments.value.sort((a, b) => {
        const dateA = new Date(a.createdAt);
        const dateB = new Date(b.createdAt);
        return sortOrder.value === "desc" ? dateB - dateA : dateA - dateB;
    });
    currentPage.value = 1;
};

const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
    }
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;
    }
};

const deleteDifficulty = (difficultyId) => {
    if (confirm("정말로 이 난이도 평가를 삭제하시겠습니까?")) {
        local
            .delete(`/sheets/difficulties/${difficultyId}`)
            .then(() => {
                fetchCommentsAndDifficulties();
            })
            .catch((error) => {
                console.error("난이도 평가 삭제 중 오류 발생:", error);
                alert("난이도 평가 삭제에 실패했습니다. 다시 시도해 주세요.");
            });
    }
};

const startEditing = (comment) => {
    editingComment.value = comment;
    editedContent.value = comment.text;
    editedDifficulty.value = comment.difficulty;
};

const cancelEditing = () => {
    editingComment.value = null;
    editedContent.value = "";
    editedDifficulty.value = "";
};

const saveEdit = () => {
    if (!editedDifficulty.value) {
        alert("난이도를 선택해주세요.");
        return;
    }

    const difficultyLevel = difficultyOptions.indexOf(editedDifficulty.value) + 1;

    const updatedData = {
        level: difficultyLevel,
        contents: editedContent.value.trim(),
    };

    local
        .put(`/sheets/difficulties/${editingComment.value.difficultyId}`, updatedData)
        .then(() => {
            alert("난이도 평가가 수정되었습니다.");
            fetchCommentsAndDifficulties();
            cancelEditing();
        })
        .catch((error) => {
            console.error("난이도 평가 수정 중 오류 발생:", error);
            alert("난이도 평가 수정에 실패했습니다. 다시 시도해 주세요.");
        });
};

const closeAllMenus = () => {
    Object.keys(menuOpenState.value).forEach((key) => {
        menuOpenState.value[key] = false;
    });
};

onClickOutside(menuRef, closeAllMenus);

const toggleMenu = (commentId, event) => {
    event.stopPropagation();
    closeAllMenus();
    menuOpenState.value[commentId] = !menuOpenState.value[commentId];
};

const prepareChartData = () => {
    return comments.value
        .filter((comment) => comment.difficulty && comment.createdAt)
        .map((comment) => ({
            x: new Date(comment.createdAt),
            y: comment.difficulty,
            difficulty: comment.difficulty,
        }))
        .sort((a, b) => a.x - b.x);
};

const difficultyColors = {
    브론즈: "rgb(205, 127, 50)",
    실버: "rgb(192, 192, 192)",
    골드: "rgb(255, 215, 0)",
    플래티넘: "rgb(229, 228, 226)",
    다이아몬드: "rgb(185, 242, 255)",
};

const createChart = () => {
    if (chartRef.value) {
        const ctx = chartRef.value.getContext("2d");
        const chartData = prepareChartData();

        chart = new Chart(ctx, {
            type: "scatter",
            data: {
                datasets: difficultyOptions.map((difficulty, index) => ({
                    label: difficulty,
                    data: chartData.filter(item => item.difficulty === difficulty),
                    backgroundColor: difficultyColors[difficulty],
                    borderColor: difficultyColors[difficulty],
                    pointRadius: 6,
                    pointHoverRadius: 8,
                })),
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const point = context.raw;
                                return `난이도: ${point.difficulty}, 날짜: ${new Date(point.x).toLocaleDateString()}`;
                            },
                        },
                    },
                },
                scales: {
                    x: {
                        type: "time",
                        time: {
                            unit: "day",
                            displayFormats: {
                                day: "MM/dd",
                            },
                        },
                        adapters: {
                            date: {
                                locale: ko,
                            },
                        },
                        title: {
                            display: false,
                            text: "날짜",
                        },
                    },
                    y: {
                        type: "category",
                        labels: difficultyOptions,
                        title: {
                            display: false,
                            text: "난이도",
                        },
                        reverse: true,
                    },
                },
            },
        });
    }
};

const updateChart = () => {
    if (chart) {
        const chartData = prepareChartData();
        chart.data.datasets = difficultyOptions.map((difficulty, index) => ({
            label: difficulty,
            data: chartData.filter(item => item.difficulty === difficulty),
            backgroundColor: difficultyColors[difficulty],
            borderColor: difficultyColors[difficulty],
            pointRadius: 6,
            pointHoverRadius: 8,
        }));
        chart.update();
    }
};

watch(comments, updateChart);

onMounted(() => {
    searchSheetDetail();
    fetchCommentsAndDifficulties();
    createChart();
});
</script>

<template>
    <div class="difficulty-contribution">
        <div class="sheet-info">
            <BigSheetCard :sheet="sheet" />
        </div>

        <div class="chart-container">
            <canvas ref="chartRef"></canvas>
        </div>

        <div class="comments-ratings-section">
            <div class="comment-form">
                <input
                    v-model="newComment"
                    type="text"
                    placeholder="난이도 평가를 해주세요"
                    class="comment-input"
                />
                <div class="difficulty-and-submit">
                    <div class="difficulty-selection">
                        <button
                            v-for="difficulty in difficultyOptions"
                            :key="difficulty"
                            @click="setDifficulty(difficulty)"
                            :class="[
                                'difficulty-button',
                                difficulty,
                                { active: difficulty === newDifficulty },
                            ]"
                        >
                            {{ difficulty }}
                        </button>
                    </div>
                    <button @click="submitCommentAndDifficulty" class="submit-button">등록</button>
                </div>
            </div>

            <div v-if="comments.length === 0" class="no-comments-message">
                첫 댓글을 달아주세요!
            </div>
            <div v-else>
                <div class="sort-control">
                    <button @click="toggleSortOrder" class="sort-button">
                        {{ sortOrder === "desc" ? "최신순" : "오래된순" }}
                    </button>
                </div>

                <div class="comments-list">
                    <div
                        v-for="(comment, index) in paginatedComments"
                        :key="index"
                        class="comment-item"
                    >
                        <img
                            v-if="comment.userAvatar"
                            :src="comment.userAvatar"
                            alt="User avatar"
                            class="user-avatar"
                        />
                        <div v-else class="user-avatar-placeholder"></div>
                        <div class="comment-content">
                            <div class="comment-header">
                                <div class="user-info">
                                    <strong>{{ comment.username }}</strong>
                                    <span :class="['difficulty-badge', comment.difficulty]">
                                        {{ comment.difficulty }}
                                    </span>
                                </div>
                                <div
                                    v-if="comment.username === userInfo.nickname"
                                    class="comment-actions"
                                    ref="menuRef"
                                >
                                    <button
                                        @click="toggleMenu(comment.difficultyId, $event)"
                                        class="menu-button"
                                    >
                                        <svg width="24" height="24" viewBox="0 0 32 32">
                                            <circle cx="16" cy="6" r="3" fill="#333" />
                                            <circle cx="16" cy="16" r="3" fill="#333" />
                                            <circle cx="16" cy="26" r="3" fill="#333" />
                                        </svg>
                                    </button>
                                    <div
                                        v-if="menuOpenState[comment.difficultyId]"
                                        class="action-menu"
                                    >
                                        <button @click="startEditing(comment)" class="edit-button">
                                            수정
                                        </button>
                                        <button
                                            @click="deleteDifficulty(comment.difficultyId)"
                                            class="delete-button"
                                        >
                                            삭제
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <p v-if="editingComment !== comment" class="comment-text">
                                {{ comment.text }}
                            </p>
                            <div v-else class="edit-form">
                                <input v-model="editedContent" type="text" class="edit-input" />
                                <div class="difficulty-selection">
                                    <button
                                        v-for="difficulty in difficultyOptions"
                                        :key="difficulty"
                                        @click="editedDifficulty = difficulty"
                                        :class="[
                                            'difficulty-button',
                                            difficulty,
                                            { active: difficulty === editedDifficulty },
                                        ]"
                                    >
                                        {{ difficulty }}
                                    </button>
                                </div>
                                <div class="edit-actions">
                                    <button @click="saveEdit" class="save-button">저장</button>
                                    <button @click="cancelEditing" class="cancel-button">
                                        취소
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="pagination">
                    <button @click="prevPage" :disabled="currentPage === 1">이전</button>
                    <span>{{ currentPage }} / {{ totalPages }}</span>
                    <button @click="nextPage" :disabled="currentPage === totalPages">다음</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.difficulty-contribution {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.sheet-info {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 30px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.comments-ratings-section {
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.comment-form,
.edit-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 20px;
    background-color: #f0f0f0;
    padding: 15px;
    border-radius: 8px;
}

.comment-input,
.edit-input {
    flex-grow: 1;
    height: 40px;
    padding: 0 15px;
    border: 1px solid #ddd;
    border-radius: 20px;
    font-size: 14px;
    transition: border-color 0.3s;
}

.comment-input:focus,
.edit-input:focus {
    outline: none;
    border-color: #4caf50;
}

.difficulty-and-submit,
.edit-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
}

.difficulty-selection {
    display: flex;
    gap: 5px;
    flex-wrap: wrap;
}

.difficulty-button {
    padding: 5px 10px;
    border: none;
    border-radius: 15px;
    cursor: pointer;
    font-weight: bold;
    transition: all 0.3s;
    font-size: 12px;
}

.difficulty-button.active {
    transform: scale(1.05);
    color: white;
}

.submit-button,
.save-button,
.cancel-button {
    padding: 5px 10px;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s, transform 0.1s;
    font-size: 12px;
}

.submit-button,
.save-button {
    background-color: #4caf50;
}

.cancel-button {
    background-color: #f44336;
}

.submit-button:hover,
.save-button:hover {
    background-color: #45a049;
}

.cancel-button:hover {
    background-color: #d32f2f;
}

.submit-button:active,
.save-button:active,
.cancel-button:active {
    transform: scale(0.98);
}

.comments-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.comment-item {
    display: flex;
    gap: 15px;
    background-color: white;
    padding: 15px;
    border-radius: 8px;
    border: 1px solid #eee;
    transition: box-shadow 0.3s;
}

.comment-item:hover {
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.user-avatar,
.user-avatar-placeholder {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.2);
}

.user-avatar-placeholder {
    background-color: #ccc;
    display: flex;
    justify-content: center;
    align-items: center;
}

.comment-content {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.comment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.comment-actions {
    position: relative;
}

.menu-button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 5px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    transition: background-color 0.3s;
}

.menu-button:hover {
    background-color: #f0f0f0;
}

.menu-button svg {
    width: 100%;
    height: 100%;
}

.action-menu {
    position: absolute;
    right: 0;
    top: 100%;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    z-index: 10;
    min-width: 100px;
    overflow: hidden;
}

.action-menu button {
    padding: 8px 12px;
    border: none;
    background: none;
    text-align: left;
    cursor: pointer;
    transition: background-color 0.3s;
    color: #333;
    font-size: 14px;
}

.action-menu button:hover {
    background-color: #f0f0f0;
}

.action-menu .edit-button {
    color: #4caf50;
}

.action-menu .delete-button {
    color: #f44336;
}

.difficulty-badge {
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
    color: white;
}

.comment-text {
    margin-top: 5px;
    color: #333;
}

.브론즈 {
    background-color: #cd7f32;
}
.실버 {
    background-color: #c0c0c0;
}
.골드 {
    background-color: #ffd700;
}
.플래티넘 {
    background-color: #e5e4e2;
}
.다이아몬드 {
    background-color: #b9f2ff;
}

.sort-control {
    margin-bottom: 15px;
    text-align: right;
}

.sort-button {
    padding: 5px 10px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.sort-button:hover {
    background-color: #e0e0e0;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    gap: 10px;
}

.pagination button {
    padding: 5px 10px;
    background-color: #f0f0f0;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.pagination button:hover:not(:disabled) {
    background-color: #e0e0e0;
}

.pagination button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.pagination span {
    font-size: 14px;
}

.no-comments-message {
    text-align: center;
    padding: 20px;
    background-color: #f0f0f0;
    border-radius: 8px;
    font-size: 16px;
    color: #666;
}

.chart-container {
    margin: 10px auto;
    width: 100%;
    max-width: 800px;
    height: 300px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
