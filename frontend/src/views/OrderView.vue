<script setup>
import { ref, computed, onMounted } from 'vue';
import { getOrder, removeFromOrder, clearOrder } from '@/util/order';
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import {addSheetToOrderAPI} from "@/api/order";

const orderItems = ref([]);
const selectedItems = ref(new Set());

onMounted(() => {
  orderItems.value = getOrder();
});

const removeItem = (itemId) => {
  removeFromOrder(itemId);
  orderItems.value = getOrder();
  selectedItems.value.delete(itemId);
};

const clearAllItems = () => {
  clearOrder();
  orderItems.value = [];
  selectedItems.value.clear();
};

const toggleSelection = (itemId, isSelected) => {
  if (isSelected) {
	selectedItems.value.add(itemId);
  } else {
	selectedItems.value.delete(itemId);
  }
};

const checkout = async () => {
  try {
	const itemsToCheckout = orderItems.value
		.filter(item => selectedItems.value.has(item.id))
		.map(item => item.id); // id 값만 추출
	console.log(itemsToCheckout); // id 값만 출력됨을 확인
    const totalPrice = totalSelectedPrice.value;
	await addSheetToOrderAPI(itemsToCheckout, 'KAKAO_PAY', totalPrice,
		(redirectUrl) => {
		  clearOrder();
      window.location.href = redirectUrl; // 결제 페이지로 리디렉션
		  alert('Order placed successfully!');
		  // router.push({name: 'home'}); // 주석 처리된 라우터 네비게이션
		},
		(error) => {
		  console.error('Failed to place order:', error);
		});
  } catch (error) {
	console.error('Failed to place order:', error);
  }
};

const totalSelectedPrice = computed(() => {
  return orderItems.value
	  .filter(item => selectedItems.value.has(item.id))
	  .reduce((total, item) => total + item.price, 0);
});
</script>

<template>
  <div class="cart-container">
	<div class="cart-list">
	  <h1 class="text-2xl font-bold mb-4">장바구니</h1>
	  <ul>
		<li v-for="item in orderItems" :key="item.id" class="mb-4 flex items-center justify-between">
		  <SmallSheetCard :sheet="item" class="flex-grow">
			<input type="checkbox" :checked="selectedItems.has(item.id)" @change="toggleSelection(item.id, $event.target.checked)" class="checkbox" />
		  </SmallSheetCard>
		  <div class="flex items-center ml-4">
			<button @click="removeItem(item.id)" class="btn btn-danger">Remove</button>
		  </div>
		</li>
	  </ul>
	</div>
	<div class="cart-summary">
	  <h2 class="text-xl font-bold mb-4">주문 요약</h2>
	  <p class="text-lg mb-4">선택한 항목 총액: {{ totalSelectedPrice }}원</p>
	  <button @click="checkout" class="btn btn-primary w-full mb-2">결제하기</button>
	  <button @click="clearAllItems" class="btn btn-warning w-full">장바구니 비우기</button>
	</div>
  </div>
</template>

<style scoped>
.cart-container {
  display: flex;
  gap: 2rem;
}

.cart-list {
  flex: 2;
}

.cart-summary {
  flex: 1;
  padding: 1rem;
  background-color: #f8f8f8;
  border-radius: 8px;
}

.btn {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: bold;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-warning {
  background-color: #ff9800;
  color: white;
}
</style>