<script setup>
import { ref, computed, onMounted } from 'vue';
import SmallSheetCard from '@/common/sheet/SmallSheetCard.vue';
import { getCartItemsAPI } from '@/api/cart';

const cartSheets = ref([]);

const totalPrice = computed(() => {
  return cartSheets.value.reduce((total, sheet) => total + sheet.price, 0);
});

const proceedToPayment = () => {
  console.log('결제 페이지로 이동');
  // 결제 페이지로 이동하거나 결제 프로세스를 시작하는 로직을 여기에 추가합니다.
};

const fetchCartItems = async () => {
  try {
    const response = await getCartItemsAPI();
    cartSheets.value = response.data;
  } catch (error) {
    console.error('Failed to fetch cart items:', error);
  }
};

onMounted(() => {
  fetchCartItems();
});
</script>

<template>
  <div class="cart-page p-4">
    <h1 class="text-2xl font-bold mb-4">장바구니</h1>
    <div class="sheet-list mb-6">
      <SmallSheetCard v-for="sheet in cartSheets" :key="sheet.id" :sheet="sheet" />
    </div>
    <div class="total-section flex justify-between items-center border-t pt-4">
      <div class="total-price text-xl font-bold">총 결제 금액: {{ totalPrice }}원</div>
      <button class="btn btn-primary" @click="proceedToPayment">결제하기</button>
    </div>
  </div>
</template>

<style scoped>
.cart-page {
  /* 장바구니 페이지 스타일을 여기에 추가합니다 */
}
.sheet-list {
  /* 곡 목록 스타일을 여기에 추가합니다 */
}
.total-section {
  /* 총 결제 금액 섹션 스타일을 여기에 추가합니다 */
}
.total-price {
  /* 총 결제 금액 텍스트 스타일을 여기에 추가합니다 */
}
.btn {
  /* 버튼 스타일을 여기에 추가합니다 */
}
</style>
