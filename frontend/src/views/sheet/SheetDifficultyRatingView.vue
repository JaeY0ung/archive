<script setup>
import { ref, onMounted, watch, computed, onUnmounted } from "vue";
import { useRoute } from "vue-router";
import { localAxios } from "@/util/http-common";
import { useUserStore } from "@/stores/user";
import { storeToRefs } from "pinia";
import Profile from "@/common/icons/Profile.vue";
import { onClickOutside } from "@vueuse/core";
import BigSheetCard from "@/common/sheet/BigSheetCardForDifficulty.vue";
import Tier from "@/common/icons/Tier.vue";
import { tierInfo } from "@/util/tier-info";
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
import { parseISO, startOfDay, addDays } from "date-fns";

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

const sortedComments = computed(() => {
    return [...comments.value].sort((a, b) => {
        const dateA = new Date(a.createdAt);
        const dateB = new Date(b.createdAt);
        return sortOrder.value === "desc" ? dateB - dateA : dateA - dateB;
    });
});

const paginatedComments = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    return sortedComments.value.slice(start, end);
});

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

const totalPages = computed(() =>
    Math.ceil(comments.value.length / itemsPerPage)
);

const getImageUrl = (base64String) => {
    return base64String ? `data:image/jpeg;base64,${base64String}` : "";
};

const searchSheetDetail = async () => {
    await local
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

const submitCommentAndDifficulty = async () => {
    if (!newDifficulty.value) {
        alert("난이도를 선택해주세요.");
        return;
    }

    if (newComment.value.trim() && newDifficulty.value) {
        const difficultyLevel =
            difficultyOptions.indexOf(newDifficulty.value) + 1;

        const difficultyData = {
            level: difficultyLevel,
            content: newComment.value.trim(),
        };

        await local
            .post(
                `/sheets/${route.params.sheetId}/difficulties`,
                difficultyData
            )
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
                    alert(
                        "난이도 평가 저장에 실패했습니다. 다시 시도해 주세요."
                    );
                }
            });
    }
};

const fetchCommentsAndDifficulties = async () => {
    await local
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
                // 차트 최신화
                updateChart();
                // 악보 난이도 정보 최신화
                searchSheetDetail();
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

const deleteDifficulty = async (difficultyId) => {
    if (confirm("정말로 이 난이도 평가를 삭제하시겠습니까?")) {
        await local
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

const saveEdit = async () => {
    if (!editedDifficulty.value) {
        alert("난이도를 선택해주세요.");
        return;
    }

    const difficultyLevel =
        difficultyOptions.indexOf(editedDifficulty.value) + 1;

    const updatedData = {
        level: difficultyLevel,
        contents: editedContent.value.trim(),
    };

    await local
        .put(
            `/sheets/difficulties/${editingComment.value.difficultyId}`,
            updatedData
        )
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
            x: startOfDay(parseISO(comment.createdAt)).toISOString(), // 날짜의 시작(00:00:00)으로 변환
            y: comment.difficulty,
            difficulty: comment.difficulty,
        }))
        .sort((a, b) => new Date(a.x) - new Date(b.x));
};

const difficultyColors = {
    브론즈: "rgb(205, 127, 50)",
    실버: "rgb(192, 192, 192)",
    골드: "rgb(255, 215, 0)",
    플래티넘: "rgb(127, 255, 212)",
    다이아몬드: "rgb(185, 242, 255)",
};

const createChart = () => {
    if (chartRef.value) {
        const ctx = chartRef.value.getContext("2d");
        const chartData = prepareChartData();

        // 현재 날짜를 기준으로 20일 전 날짜 계산 (시간은 00:00:00으로 설정)
        const twentyDaysAgo = startOfDay(addDays(new Date(), -20));

        chart = new Chart(ctx, {
            type: "scatter",
            data: {
                datasets: difficultyOptions.map((difficulty) => ({
                    label: difficulty,
                    data: chartData.filter((item) => {
                        const itemDate = startOfDay(parseISO(item.x));
                        return (
                            item.difficulty === difficulty &&
                            itemDate >= twentyDaysAgo
                        );
                    }),
                    backgroundColor: difficultyColors[difficulty],
                    borderColor: difficultyColors[difficulty],
                    pointRadius: 6,
                    pointHoverRadius: 8,
                })),
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                animation: false,
                layout: {
                    padding: {
                        left: 20,
                        right: 20,
                        top: 20,
                        bottom: 20,
                    },
                },
                plugins: {
                    legend: {
                        display: false,
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const point = context.raw;
                                return `난이도: ${
                                    point.difficulty
                                }, 날짜: ${new Date(
                                    point.x
                                ).toLocaleDateString()}`;
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
                        min: twentyDaysAgo, // Set only once
                        max: addDays(startOfDay(new Date()), 1), // Set only once
                        ticks: {
                            padding: 10, // x축 레이블과 차트 사이의 패딩 증가
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
                        ticks: {
                            padding: 10, // y축 레이블과 차트 사이의 패딩 증가
                        },
                    },
                },
            },
        });
    }
};

const updateChart = () => {
    if (chart) {
        const chartData = prepareChartData();

        chart.data.datasets = difficultyOptions.map((difficulty) => ({
            label: difficulty,
            data: chartData.filter((item) => {
                const itemDate = startOfDay(parseISO(item.x));
                return (
                    item.difficulty === difficulty &&
                    itemDate >= startOfDay(addDays(new Date(), -20))
                );
            }),
            backgroundColor: difficultyColors[difficulty],
            borderColor: difficultyColors[difficulty],
            pointRadius: 6,
            pointHoverRadius: 8,
        }));

        chart.update();
    }
};

watch(comments, updateChart);

const destroyChart = () => {
    if (chart) {
        chart.destroy();
        chart = null;
    }
};

onMounted(async () => {
    await searchSheetDetail();
    await fetchCommentsAndDifficulties();
    createChart();
});

onUnmounted(() => {
    destroyChart();
});
</script>

<template>
    <div class="w-full h-full max-w-7xl p-2.5 box-border">
        <div class="flex gap-4 h-[100%]">
            <div class="flex-1 flex flex-col h-full overflow-hidden">
                <div class="bg-white p-5 rounded-lg shadow-md h-full">
                    <BigSheetCard :sheet="sheet" />
                    <div class="mt-10">
                        <canvas ref="chartRef" class="w-full"></canvas>
                    </div>
                </div>
            </div>
            <div class="flex-1 flex flex-col h-full overflow-hidden">
                <div
                    class="bg-white p-5 rounded-lg shadow-md h-full flex flex-col pb-15"
                >
                    <h2 class="mb-5 text-2xl font-extrabold text-gray-800">
                        난이도 평가
                    </h2>
                    <div class="mb-5">
                        <textarea
                            v-model="newComment"
                            placeholder="난이도 평가를 해주세요"
                            class="w-full h-20 p-2.5 border border-gray-300 rounded-lg text-sm resize-y transition-colors focus:outline-none focus:border-green-500"
                        ></textarea>
                        <div class="flex justify-between items-center mt-2.5">
                            <div class="flex gap-1 flex-wrap">
                                <button
                                    v-for="difficulty in difficultyOptions"
                                    :key="difficulty"
                                    @click="setDifficulty(difficulty)"
                                    :class="[
                                        'px-2.5 py-1 rounded-full cursor-pointer font-bold transition-all text-xs',
                                        difficulty === newDifficulty
                                            ? 'transform scale-105 text-white'
                                            : 'text-gray-700',
                                        {
                                            'bg-[#cd7f32]':
                                                difficulty === '브론즈',
                                            'bg-[#c0c0c0]':
                                                difficulty === '실버',
                                            'bg-[#ffd700]':
                                                difficulty === '골드',
                                            'bg-[#7FFFD4]':
                                                difficulty === '플래티넘',
                                            'bg-[#b9f2ff]':
                                                difficulty === '다이아몬드',
                                        },
                                    ]"
                                >
                                    {{ difficulty }}
                                </button>
                            </div>
                            <button
                                @click="submitCommentAndDifficulty"
                                class="px-4 py-2 bg-green-500 text-white rounded transition-colors hover:bg-green-600 active:transform active:scale-98"
                            >
                                등록
                            </button>
                        </div>
                    </div>

                    <div class="flex-1 overflow-y-auto scrollbar-hide">
                        <div
                            v-if="comments.length === 0"
                            class="text-center p-5 bg-gray-100 rounded-lg text-base text-gray-600"
                        >
                            첫 댓글을 달아주세요!
                        </div>
                        <div v-else>
                            <div class="flex justify-between items-center mb-5">
                                <h3 class="text-lg font-bold text-gray-800">
                                    댓글 목록
                                </h3>
                                <button
                                    @click="toggleSortOrder"
                                    class="px-2.5 py-1 bg-gray-200 rounded transition-colors hover:bg-gray-300 text-sm"
                                >
                                    {{
                                        sortOrder === "desc"
                                            ? "최신순"
                                            : "오래된순"
                                    }}
                                </button>
                            </div>

                            <div class="space-y-4">
                                <div
                                    v-for="(
                                        comment, index
                                    ) in paginatedComments"
                                    :key="index"
                                    class="flex gap-4 bg-white p-4 rounded-lg border border-gray-200 transition-shadow hover:shadow-md"
                                >
                                    <div class="flex-shrink-0">
                                        <img
                                            v-if="comment.userAvatar"
                                            :src="comment.userAvatar"
                                            alt="User avatar"
                                            class="w-10 h-10 rounded-full object-cover border-2 border-white shadow"
                                        />
                                        <div
                                            v-else
                                            class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center"
                                        >
                                            <Profile
                                                class="w-6 h-6 text-gray-600"
                                            />
                                        </div>
                                    </div>
                                    <div class="flex-1">
                                        <div
                                            class="flex justify-between items-center mb-1"
                                        >
                                            <div
                                                class="flex items-center gap-2.5"
                                            >
                                                <strong class="text-gray-800">{{
                                                    comment.username
                                                }}</strong>
                                                <span
                                                    :class="[
                                                        'px-2.5 py-1 rounded-full text-xs font-bold',
                                                        {
                                                            'bg-[#cd7f32]':
                                                                comment.difficulty ===
                                                                '브론즈',
                                                            'bg-[#c0c0c0]':
                                                                comment.difficulty ===
                                                                '실버',
                                                            'bg-[#ffd700]':
                                                                comment.difficulty ===
                                                                '골드',
                                                            'bg-[#7FFFD4]':
                                                                comment.difficulty ===
                                                                '플래티넘',
                                                            'bg-[#b9f2ff]':
                                                                comment.difficulty ===
                                                                '다이아몬드',
                                                        },
                                                    ]"
                                                >
                                                    {{ comment.difficulty }}
                                                </span>
                                            </div>
                                        </div>
                                        <p
                                            v-if="editingComment !== comment"
                                            class="text-gray-700 mt-1"
                                        >
                                            {{ comment.text }}
                                        </p>
                                        <div v-else class="mt-2">
                                            <textarea
                                                v-model="editedContent"
                                                class="w-full p-2 border border-gray-300 rounded-lg text-sm resize-y transition-colors focus:outline-none focus:border-green-500"
                                            ></textarea>
                                            <div class="flex gap-1 mt-2">
                                                <button
                                                    v-for="difficulty in difficultyOptions"
                                                    :key="difficulty"
                                                    @click="
                                                        editedDifficulty =
                                                            difficulty
                                                    "
                                                    :class="[
                                                        'px-2 py-1 rounded-full cursor-pointer font-bold transition-all text-xs',
                                                        difficulty ===
                                                        editedDifficulty
                                                            ? 'transform scale-105 text-black'
                                                            : 'text-gray-700',
                                                        {
                                                            'bg-[#cd7f32]':
                                                                difficulty ===
                                                                '브론즈',
                                                            'bg-[#c0c0c0]':
                                                                difficulty ===
                                                                '실버',
                                                            'bg-[#ffd700]':
                                                                difficulty ===
                                                                '골드',
                                                            'bg-[#7FFFD4]':
                                                                difficulty ===
                                                                '플래티넘',
                                                            'bg-[#b9f2ff]':
                                                                difficulty ===
                                                                '다이아몬드',
                                                        },
                                                    ]"
                                                >
                                                    {{ difficulty }}
                                                </button>
                                            </div>
                                            <div class="flex gap-2 mt-2">
                                                <button
                                                    @click="saveEdit"
                                                    class="px-3 py-1 bg-green-500 text-white rounded font-bold transition-colors hover:bg-green-600 text-sm"
                                                >
                                                    저장
                                                </button>
                                                <button
                                                    @click="cancelEditing"
                                                    class="px-3 py-1 bg-red-500 text-white rounded font-bold transition-colors hover:bg-red-600 text-sm"
                                                >
                                                    취소
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="flex justify-center items-center mt-5 gap-2.5">
                        <button
                            @click="prevPage"
                            :disabled="currentPage === 1"
                            class="px-2.5 py-1 bg-gray-200 rounded transition-colors hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            이전
                        </button>
                        <span class="text-sm"
                            >{{ currentPage }} / {{ totalPages }}</span
                        >
                        <button
                            @click="nextPage"
                            :disabled="currentPage === totalPages"
                            class="px-2.5 py-1 bg-gray-200 rounded transition-colors hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            다음
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
