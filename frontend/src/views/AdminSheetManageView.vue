<script setup>
import { ref, watch } from 'vue';
import { searchSheetsByFilter } from "@/api/sheet";
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import { localAxios } from "@/util/http-common"

const local = localAxios();
const sheets = ref([]);
const waitingSheets = ref([]);
const approvedSheets = ref([]);
const rejectedSheets = ref([]);

const searchSheet = () => {
    searchSheetsByFilter(
        { },
        ({ data }) => {
            sheets.value = data;
            sheets.value = data;
            sheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지');
        }
    )
}
searchSheet();

watch(sheets, () => {
    let wSheets = [];
    let aSheets = [];
    let rSheets = [];

    sheets.value.map(s => {
        if (s.status === 0) {
            wSheets.push(s);
        } else if (s.status === 1)  {
            aSheets.push(s);
        } else if (s.status === 2)  {
            rSheets.push(s);
        }
    })
    waitingSheets.value = wSheets;
    approvedSheets.value = aSheets;
    rejectedSheets.value = rSheets;
})

const changeSheetToWaiting = (sheetId) => {
    local.put(`/sheets/${sheetId}/status/0`)
        .then(({ data }) => {
            searchSheet();
        })
}

const changeSheetToApproved = (sheetId) => {
    local.put(`/sheets/${sheetId}/status/1`)
        .then(({ data }) => {
            searchSheet();
        })
}

const changeSheetToRejected = (sheetId) => {
    local.put(`/sheets/${sheetId}/status/2`)
        .then(({ data }) => {
            searchSheet();
        })
}

const removeSheet = (sheetId) => {
    if (!confirm("정말로 삭제 하시겠습니까?")) {
        return;
    }
    local.delete(`/sheets/${sheetId}`)
        .then(({ data }) => {
            searchSheet();
        })
}


</script>

<template>
    <div class="flex">
        <div class="flex-1 bg-green-700 rounded-xl">
            <div class="text-center">대기중인 악보</div>
            <div>
                <SmallSheetCard v-for="sheet in waitingSheets" :sheet>
                    <div class="btn btn-secondary" @click="changeSheetToApproved(sheet.id)">승인</div>
                    <div class="btn btn-error" @click="changeSheetToRejected(sheet.id)">거절</div>
                </SmallSheetCard>
            </div>
        </div>
        <div class="flex-1 bg-blue-700 rounded-xl">
            <div class="text-center">승인된 악보</div>
            <div>
                <SmallSheetCard v-for="sheet in approvedSheets" :sheet >
                    <div class="btn btn-success" @click="changeSheetToWaiting(sheet.id)">대기로 이동</div>
                    <div class="btn btn-error" @click="changeSheetToRejected(sheet.id)">거절로 이동</div>
                </SmallSheetCard>
            </div>
        </div>
        <div class="flex-1 bg-red-700 rounded-xl">
            <div class="text-center">거절된 악보</div>
            <div>
                <SmallSheetCard v-for="sheet in rejectedSheets" :sheet >
                    <div class="btn btn-success" @click="changeSheetToWaiting(sheet.id)">대기로 이동</div>
                    <div class="btn btn-secondary" @click="changeSheetToApproved(sheet.id)">승인으로 이동</div>
                    <div class="btn btn-error" @click="removeSheet(sheet.id)">삭제</div>
                </SmallSheetCard>
            </div>
        </div>
    </div>
</template>

<style scoped>

</style>