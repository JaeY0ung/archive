<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import BigSheetCard from "@/common/sheet/BigSheetCardForDifficulty.vue";

const userStore = useUserStore();
const route = useRoute();
const local = localAxios();

const { userInfo } = storeToRefs(userStore);
const sheet = ref({});
// 댓글 및 난이도 관련 상태
const newComment = ref("");
const newDifficulty = ref("");
const comments = ref([]);
// 정렬 상태
const sortOrder = ref('desc');

// 난이도 옵션 및 매핑
const difficultyOptions = ["브론즈", "실버", "골드", "플래티넘", "다이아몬드"];
const difficultyMap = {
    1: "브론즈",
    2: "실버",
    3: "골드",
    4: "플래티넘",
    5: "다이아몬드"
};

// 페이지네이션
const currentPage = ref(1);
const commentsPerPage = 3;
const totalPages = ref(0);
const totalItems = ref(0);

// 악보 세부 정보 가져오기
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

// 난이도 설정 함수
const setDifficulty = (difficulty) => {
    newDifficulty.value = difficulty;
};

// 댓글 및 난이도 제출
const submitCommentAndDifficulty = () => {
    if (!newDifficulty.value) {
        alert("난이도를 선택해주세요.");
        return;
    }

    if (newComment.value.trim() && newDifficulty.value) {
        const difficultyLevel = difficultyOptions.indexOf(newDifficulty.value) + 1;
        
        const difficultyData = {
            level: difficultyLevel,
            contents: newComment.value.trim()
        };

        local.post(`/sheets/${route.params.sheetId}/difficulties`, difficultyData)
            .then(response => {
                // 저장 성공 후 댓글 목록 새로고침
                fetchCommentsAndDifficulties();
                // 입력 필드 초기화
                newComment.value = "";
                newDifficulty.value = "";
            })
            .catch(error => {
                console.error("난이도 평가 저장 중 오류 발생:", error);
                alert("난이도 평가 저장에 실패했습니다. 다시 시도해 주세요.");
            });
    }
};

const fetchCommentsAndDifficulties = () => {
    local.get(`/sheets/${route.params.sheetId}/difficulties`, {
        params: {
            page: currentPage.value - 1,
            size: commentsPerPage
        }
    })
    .then(({ data }) => {
        if (data && data.content) {
            comments.value = data.content.map(item => ({
                username: item.username,
                userAvatar: item.userImg,
                sheetTitle: item.sheetTitle,
                sheetFileName: item.sheetFileName,
                difficulty: difficultyMap[item.level] || "알 수 없음",
                text: item.content,
                createdAt: new Date(item.createAt)
            }));
            console.log('페이지 네이션으로 받은 데이터', data);
            totalItems.value = data.totalElements;
            totalPages.value = data.totalPages;
            currentPage.value = data.number + 1;
        } else {
            console.error("Expected paginated data, but received:", data);
            comments.value = [];
            totalItems.value = 0;
            totalPages.value = 0;
        }
        sortComments();
    })
    .catch((err) => {
        console.error("난이도 평가 목록을 가져오는 데 실패했습니다:", err);
        alert("난이도 평가 목록을 불러오는 데 실패했습니다.");
        comments.value = [];
        totalItems.value = 0;
        totalPages.value = 0;
    });
};

// 정렬 함수 추가
const sortComments = () => {
    comments.value.sort((a, b) => {
        if (sortOrder.value === 'desc') {
            return b.createdAt - a.createdAt;
        } else {
            return a.createdAt - b.createdAt;
        }
    });
};

// 정렬 순서 변경 함수
const toggleSortOrder = () => {
    sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc';
    sortComments();
};

const goToPage = (page) => {
    currentPage.value = page;
    fetchCommentsAndDifficulties();
};

const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++;
        fetchCommentsAndDifficulties();
    }
};

const prevPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--;
        fetchCommentsAndDifficulties();
    }
};

onMounted(() => {
    searchSheetDetail();
    fetchCommentsAndDifficulties();
});
</script>

<template>
    <div class="difficulty-contribution">
        <div class="sheet-info">
            <BigSheetCard :sheet="sheet" />
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
                            :class="['difficulty-button', difficulty, { active: difficulty === newDifficulty }]"
                        >
                            {{ difficulty }}
                        </button>
                    </div>
                    <button @click="submitCommentAndDifficulty" class="submit-button">등록</button>
                </div>
            </div>
            <div class="sort-control">
                <button @click="toggleSortOrder" class="sort-button">
                    {{ sortOrder === 'desc' ? '최신순' : '오래된순' }}
                </button>
            </div>

            <div class="comments-list">
                <div v-for="(comment, index) in comments" :key="index" class="comment-item">
                    <img :src="comment.userAvatar" alt="User avatar" class="user-avatar" />
                    <div class="comment-content">
                        <strong>{{ comment.username }}</strong>
                        <span :class="['difficulty-badge', comment.difficulty]">
                            {{ comment.difficulty }}
                        </span>
                        <p>{{ comment.text }}</p>
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

.comment-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin-bottom: 20px;
    background-color: #f0f0f0;
    padding: 15px;
    border-radius: 8px;
}

.comment-input {
    flex-grow: 1;
    height: 40px;
    padding: 0 15px;
    border: 1px solid #ddd;
    border-radius: 20px;
    font-size: 14px;
    transition: border-color 0.3s;
}

.comment-input:focus {
    outline: none;
    border-color: #4caf50;
}

.difficulty-and-submit {
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

.submit-button {
    height: 40px;
    padding: 0 20px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.3s, transform 0.1s;
}

.submit-button:hover {
    background-color: #45a049;
}

.submit-button:active {
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

.user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #fff;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.2);
}

.comment-content {
    flex: 1;
}

.comment-content strong {
    font-size: 16px;
    color: #333;
}

.difficulty-badge {
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 12px;
    margin-left: 10px;
    color: white;
}

.comment-content p {
    margin-top: 5px;
    color: #666;
    font-size: 14px;
}

.브론즈 { background-color: #cd7f32; }
.실버 { background-color: #c0c0c0; }
.골드 { background-color: #ffd700; }
.플래티넘 { background-color: #e5e4e2; }
.다이아몬드 { background-color: #b9f2ff; }

.difficulty-button:not(.active) {
    background-color: #f0f0f0;
    color: #333;
}

.difficulty-button:hover:not(.active) {
    background-color: #e0e0e0;
}

.error-message {
    color: red;
    font-size: 14px;
    margin-top: 10px;
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
</style>