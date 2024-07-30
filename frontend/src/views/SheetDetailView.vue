<script setup>
import { useRoute } from 'vue-router'
import { ref } from 'vue';
import { localAxios } from '@/util/http-common';
import Sheet from '@/common/sheet/Sheet.vue';
import SheetPlayNavigation from '@/common/sheet/SheetPlayNavigation.vue';
import BigSheetCard from '@/common/sheet/BigSheetCard.vue';

const route = useRoute();
const local = localAxios();
const isPlay = ref("stop")

const sheet = ref({})
const sameLevelSheets = ref([])
const starRateList = ref([])
const starRateAvg = ref(0.0); // 별점 평균
const starRateStatistic = ref([0, 0, 0, 0, 0, 0]) // 별점 0,1,2,3,4,5 인 리뷰들의 수
const starRateRegisterForm = ref({
    content: "",
    starRate: 3,
})

// 악보 세부 정보 가져오기
const searchSheetDetail = () => {
    local.get(`/sheets/${ route.params.sheetId }`)
        .then(({ data }) => {
            sheet.value = data;
            sheet.value.imageUrl = `data:image/jpeg;base64,${data.songImg}`
        }).catch((err) => {
            alert(err)
        })
}

// 같은 수준의 악보 랜덤으로 가져오기
const searchRandomSameLevelSheets = () => {
    let params = {
        level: sheet.value.level,
        sort: 'RANDOM',
    }

    local.get("/sheets", { params })
        .then(({ data }) => {
            sameLevelSheets.value = data;
            sameLevelSheets.value.map(s => s.songImg ? s.imageUrl = `data:image/jpeg;base64,${s.songImg}` : '기본 이미지')
        }).catch((err) => {
            alert(err)
        })
}

// 별점 가져오기
const searchStarRateList = () => {
    local.get(`/sheets/${ route.params.sheetId }/star-rates`)
        .then(({ data }) => {
            console.log("starRateList: ", data);
            starRateList.value = data;
            let sum = 0;
            starRateList.value.map(starRateInfo => {
                sum += starRateInfo.starRate;
                starRateStatistic.value[starRateInfo.starRate]++;
            });
            starRateAvg.value = round(sum / starRateList.value.length, 2);
        }).catch((err) => {
            alert(err)
        })
}

// 별점 등록하기
const registerStarRate = () => {
    local.post(`/sheets/${ route.params.sheetId }/star-rates`, starRateRegisterForm)
        .then(({ data }) => {
            console.log("starRateList: ", data);
            starRateList.value = data;
            let sum = 0;
            starRateList.value.map(starRateInfo => {
                sum += starRateInfo.starRate;
                starRateStatistic.value[starRateInfo.starRate]++;
            });
            starRateAvg.value = round(sum / starRateList.value.length, 2);
        }).catch((err) => {
            alert(err)
        })
}

function round(number, place) {
    return Math.round(number * 10 ** place) / (10 ** place);
}

searchSheetDetail()
searchRandomSameLevelSheets()
searchStarRateList()
</script>

<template>
  <div class="flex justify-between flex-margin h-full">
    <!-- 왼쪽 -->
    <div class="flex flex-col gap-10 w-[49%] h-full rounded-xl">
      <BigSheetCard :sheet />

      <div>
        <div>비슷한 수준의 악보 추천</div>
        <div class="line"></div>
        <div class="h-full bg-white/50 rounded-xl">
          <BigSheetCard
            v-for="(sheet, index) in sameLevelSheets"
            :key="index"
            :sheet="sheet"
          />
        </div>
      </div>

      <div>
        <div>리뷰</div>
        <div class="line"></div>
        <div class="h-full bg-white/50 rounded-xl flex justify-between">
          <div class="flex flex-1 justify-center items-center">
            <div>
              <img :src="require('@/assets/img/star-fill.svg')" alt="별" width="30px" />
            </div>
            <div>{{ starRateAvg }} / 5.00</div>
          </div>

          <div class="flex flex-1 items-center flex-row gap-3">
            <div v-for="(starRateCount, index) in starRateStatistic" :key="index">
              <template v-if="index != 0"> {{ index }} : {{ starRateCount }} </template>
            </div>
          </div>
        </div>
        <div class="flex">
          <input
            v-model="starRateRegisterForm.content"
            type="text"
            class="input input-bordered w-full"
            placeholder="평가"
          />
          <input
            v-model="starRateRegisterForm.starRate"
            type="range"
            min="1"
            max="5"
            step="1"
            class="input input-bordered w-full"
          />
          {{ starRateRegisterForm.starRate }}
          <button @click="registerStarRate" class="btn btn-primary">등록</button>
        </div>

        <div class="flex flex-col gap-3">
          <div
            class="flex gap-10"
            v-for="(starRateInfo, index) in starRateList"
            :key="index"
          >
            <div>{{ starRateInfo.nickname }}</div>
            <div>{{ starRateInfo.content }}</div>
            <div>{{ starRateInfo.starRate }}</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 오른쪽 -->
    <div class="flex flex-col gap-5 w-[49%] h-full p-3 bg-white/50 rounded-xl">
      <SheetPlayNavigation
        class="flex-none h-[30px]"
        @play="isPlay = 'play'"
        @pause="isPlay = 'pause'"
        @stop="isPlay = 'stop'"
      />
      <div class="bg-black rounded-xl h-full"></div>
      <!-- <Sheet class="rounded-xl h-full" :isPlay="isPlay" /> -->
    </div>
  </div>
</template>

<style scoped>
.line::before {
  content: "";
  display: block;
  border-top: 2px solid rgba(0, 0, 0, 0.5);
  width: 100%;
}
</style>
