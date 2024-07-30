<template>
  <div class="flex justify-between flex-margin h-full">
    <!-- 왼쪽 -->
    <div class="flex flex-col gap-10 w-[49%] h-full rounded-xl">
      <BigSheetCard :sheet="currentSheet" />

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
      <!-- <SheetPage class="rounded-xl h-full" :isPlay="isPlay" /> -->
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentSheet: {}, // Ensure this is defined
      sameLevelSheets: [], // Ensure this is populated with data
      starRateAvg: 0, // Ensure this is calculated
      starRateStatistic: [], // Ensure this is populated with data
      starRateRegisterForm: {
        content: "",
        starRate: 1,
      },
      starRateList: [], // Ensure this is populated with data
      isPlay: "stop", // Ensure this is set correctly
    };
  },
  methods: {
    registerStarRate() {
      // Implement the method to handle star rate registration
    },
  },
};
</script>

<style scoped>
.line::before {
  content: "";
  display: block;
  border-top: 2px solid rgba(0, 0, 0, 0.5);
  width: 100%;
}
</style>
