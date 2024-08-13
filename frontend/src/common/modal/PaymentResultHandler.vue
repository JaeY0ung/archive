<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PaymentResultModal from "@/common/modal/PaymentResultModal.vue";

const route = useRoute();
const router = useRouter();
const isModalVisible = ref(false);
const paymentResult = ref({});

const handlePaymentResult = () => {
  const { status, orderId, itemName, amount, message } = route.query;

  paymentResult.value = {
	status,
	orderId,
	itemName,
	amount,
	message: status === 'success'
		? `결제가 성공적으로 완료되었습니다. 주문번호: ${orderId}, 주문상품: ${itemName}, 결제금액: ${amount}원`
		: message || '결제 처리 중 오류가 발생했습니다.'
  };

  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
  router.push('/'); // 또는 다른 적절한 페이지로 이동
};

onMounted(() => {
  handlePaymentResult();
});
</script>

<template>
  <div>
	<h1>결제 처리 완료</h1>
	<PaymentResultModal
		:isVisible="isModalVisible"
		:paymentResult="paymentResult"
		@close="closeModal"
	/>
  </div>
</template>
