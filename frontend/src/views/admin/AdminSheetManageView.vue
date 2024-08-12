<script setup>
import { ref, watch } from 'vue';
import { changeSheetStatusBySheetId, deleteSheet, searchSheetByStatusForAdmin } from "@/api/sheet";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import SmallSheetCardForUpdate from "@/common/sheet/SmallSheetCardForUpdate.vue";
import { localAxios } from "@/util/http-common"

const local = localAxios();

const sheets = ref([]);

const searchSheets = () => {
    for (let status = 0; status < 3; status++) {
        searchSheetByStatusForAdmin(
            {
                statuses: status,
                sort: "LATEST",
            },
            ({ data }) => {
                sheets.value[status] = data;
            }
        )
    }
}
searchSheets();

const changeSheetStatus = (sheetId, status) => {
    changeSheetStatusBySheetId(
        sheetId,
        status,
        (res) => {
            searchSheets();
        }
    )
}

const removeSheet = (sheetId) => {
    if (!confirm("정말로 삭제 하시겠습니까?")) {
        return;
    }
    deleteSheet( sheetId, (res) => { searchSheets() } )
}

const updatingSheetId = ref('');

const updateEventOccur = () => {
    searchSheets();
    updatingSheetId.value = '';
}

</script>

<template>
    <div class="m-auto">
        <div class="flex justify-center gap-3">
            <div class=" bg-green-700 rounded-xl p-1 scroll-x">
                <div class="text-center">대기중인 악보</div>
                <div>
                    <template v-for="sheet in sheets[0]" :key="sheet.id">
                        <div class="flex">
                            <SmallSheetCard :sheet v-if="sheet.id != updatingSheetId">
                                <div class="h-full flex flex-col items-center gap-1">
                                    <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 1)">승인</div>
                                    <div class="bg-red-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 2)">거절</div>
                                </div>
                            </SmallSheetCard>
                            <div class="flex flex-col justify-center" v-if="sheet.id != updatingSheetId">
                                <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updatingSheetId = sheet.id">수정</div>
                            </div>
                            <SmallSheetCardForUpdate :sheet v-else @update-sheet-event="updateEventOccur"/>
                        </div>
                    </template>
                </div>
            </div>

            <div class=" bg-blue-700 rounded-xl p-1 scroll-x">
                <div class="text-center">승인된 악보</div>
                <div>
                    <template v-for="sheet in sheets[1]" :key="sheet.id">
                        <div class="flex">
                            <SmallSheetCard :sheet v-if="sheet.id != updatingSheetId">
                                <div class="h-full flex flex-col items-center gap-1">
                                    <div class="bg-green-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 0)">대기</div>
                                    <div class="bg-red-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 2)">거절</div>
                            
                                </div>
                            </SmallSheetCard>
                            <div class="flex flex-col justify-center" v-if="sheet.id != updatingSheetId">
                                <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updatingSheetId = sheet.id">수정</div>
                            </div>
                            <SmallSheetCardForUpdate :sheet v-else @update-sheet-event="updateEventOccur"/>
                        </div>
                    </template>
                </div>
            </div>

            <div class=" bg-red-700 rounded-xl p-1 scroll-x">
                <div class="text-center">거절된 악보</div>
                <div>
                    <template v-for="sheet in sheets[2]" :key="sheet.id">
                        <div class="flex">
                            <SmallSheetCard :sheet v-if="sheet.id != updatingSheetId">
                                <div class="h-full flex flex-col items-center gap-1">
                                    <div class="bg-green-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 0)">대기</div>
                                    <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="changeSheetStatus(sheet.id, 1)">승인</div>
                                </div>
                            </SmallSheetCard>
                            <div v-if="sheet.id != updatingSheetId" class="m-auto flex flex-col gap-1">
                                <div class="flex flex-col justify-center" >
                                    <div class="bg-blue-500 rounded-xl p-2 cursor-pointer" @click="updatingSheetId = sheet.id">수정</div>
                                </div>
                                <div class="flex flex-col justify-center">
                                    <div class="bg-red-400 rounded-xl p-2 cursor-pointer" @click="removeSheet(sheet.id)">삭제</div>
                                </div>
                            </div>
                            <SmallSheetCardForUpdate :sheet v-else @update-sheet-event="updateEventOccur"/>
                        </div>
                    </template>

                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>