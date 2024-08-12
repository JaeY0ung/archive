<script setup>
import { ref, computed, onMounted } from 'vue';
import { getOrder, removeFromOrder, clearOrder } from '@/util/order';
import SmallSheetCard from "@/common/sheet/SmallSheetCard.vue";
import {addSheetToOrderAPI} from "@/api/order";
import PaymentResultModal from "@/common/modal/PaymentResultModal.vue";

const orderItems = ref([]);
const selectedItems = ref(new Set());
const isModalVisible = ref(false);
const paymentResult = ref({});

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
		  	window.location.href = redirectUrl;
		  	// paymentResult.value = response.data; // 결제 응답 저장
		  	// console.log(paymentResult.value);
		  	// isModalVisible.value = true; // 모달 표시
		},
		// (redirectUrl) => {
		//   clearOrder();
		//   console.log(redirectUrl);
      // window.location.href = redirectUrl; // 결제 페이지로 리디렉션
		//   alert('Order placed successfully!');
		//   // router.push({name: 'home'}); // 주석 처리된 라우터 네비게이션
		// },
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

const selectedItemsList = computed(() => {
  return orderItems.value.filter(item => selectedItems.value.has(item.id));
});

// 결제 완료 후 처리를 위한 새로운 함수
const handlePaymentCompletion = async () => {
  const urlParams = new URLSearchParams(window.location.search);
  const pgToken = urlParams.get('pg_token');

  if (pgToken) {
	try {
	  const response = await fetch(`/pay/completed?pg_token=${pgToken}`);
	  const result = await response.json();

	  paymentResult.value = result;
	  isModalVisible.value = true;
	} catch (error) {
	  console.error('Error fetching payment result:', error);
	  paymentResult.value = {
		status: 'error',
		message: '결제 결과를 불러오는 데 실패했습니다.'
	  };
	  isModalVisible.value = true;
	}
  }
};

onMounted(() => {
  orderItems.value = getOrder();
  handlePaymentCompletion();
});
</script>

<template>
  <div class="flex justify-around w-full h-full">
    <div class="flex flex-col w-[60vw] bg-white bg-opacity-50 pl-4 pr-4">
		<h1 class="text-2xl text-gray-600 font-bold mb-3 mt-3 text-center">장바구니</h1>
		<hr class="bg-gray-800">
		<div class="cart-list flex-2 max-h-[80vh] overflow-y-auto hide-scrollbar">
			<ul>
				<li v-for="item in orderItems" :key="item.id" class="mb-4 flex items-center justify-between">
					<SmallSheetCard :sheet="item" class="flex-grow relative">
						<input type="checkbox" 
							:checked="selectedItems.has(item.id)" 
							@change="toggleSelection(item.id, $event.target.checked)" 
							class="checkbox absolute right-0 top-1/2 transform -translate-y-1/2 mr-4" />
					</SmallSheetCard>
				<div class="flex items-center ml-4">
					<button @click="removeItem(item.id)" class="btn btn-danger">
						<img src="@/assets/img/common/trash.png" alt="Remove" style="width:25px; height: 25px;">
					</button>
				</div>
				</li>
			</ul>
		</div>
	</div>
	
	<div class="w-[30vw] p-[1rem] bg-white bg-opacity-60 rounded-md">
	  	<h2 class="text-xl text-center text-gray-700 font-bold mb-4">주문 요약</h2>
	  	<hr class="bg-gray-800 mb-2">
	 	<div class="flex flex-row bg-white justify-around bg-opacity-50 mb-2 shadow-md">
			
			<p class="text-center mt-2 text-xs font-semibold text-gray-800">선택 악보 총 주문 금액 </p>
			<div class="flex">
				<img src="@/assets/img/cash.png" class="mt-2 ml-2 mr-2 " style="width:15px; height: 15px;">
				<p class="text-center mb-1 text-lg font-semibold text-gray-800"> {{ totalSelectedPrice }} </p>
			</div>
		
	  	</div>
	  <div v-for="item in selectedItemsList" :key="item.id" class="flex justify-between p-4 mb-2 bg-white bg-opacity-70 shadow-md rounded-lg">
        <h3 class="font-semibold">{{ item.title }}</h3>
        <p class="flex text-gray-600 text-sm">
			<img src="@/assets/img/cash.png" class="mt-0.5 mr-2" style="width:15px; height: 15px;">
		{{ item.price }}\</p>
      </div>
	  
	  <button @click="checkout"
              :disabled="selectedItems.size === 0"
              :class="{'bg-gray-300': selectedItems.size === 0, 'bg-sky-300': selectedItems.size > 0}"
              class="btn w-full mb-2 shadow-md">
        결제하기
      </button>
	  <button @click="clearAllItems" class="btn bg-pink-400 bg-opacity-60 w-full shadow-md">장바구니 비우기</button>
	</div>
  </div>

<!--  <div class="flex justify-around w-full h-full">-->
<!--	&lt;!&ndash; Your existing template code here &ndash;&gt;-->

<!--	<PaymentResultModal-->
<!--		:isVisible="isModalVisible"-->
<!--		:paymentResult="paymentResult"-->
<!--		@close="isModalVisible.value = false" />-->
<!--  </div>-->

  <PaymentResultModal
	  :isVisible="isModalVisible"
	  :paymentResult="paymentResult"
	  @close="isModalVisible = false"
  />
</template>

<style scoped>


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
  padding: 0.5rem 0.7rem;
  border-radius: 4px;
  font-weight: bold;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-danger {
  background-color: #f44336;
  
}

.btn-warning {
  background-color: #ff9800;
  color: white;
}

.hide-scrollbar::-webkit-scrollbar {
  display: none;
}

.hide-scrollbar {
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}
</style>