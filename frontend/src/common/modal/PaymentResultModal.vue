<script setup>
import Swal from "sweetalert2";
import { watch } from 'vue';

// Props와 Emits 정의
const props = defineProps({
  isVisible: Boolean,
  paymentResult: Object
});

const emit = defineEmits(['close']);

// isVisible이 변경될 때 Swal을 실행
watch(
	() => props.isVisible,
	(newVal) => {
	  if (newVal) {
		const icon = props.paymentResult.status === 'success' ? 'success' : 'error';

		Swal.fire({
		  title: '결제 결과',
		  html: props.paymentResult.status === 'success' ?
			  `          <p>
            주문 번호: ${props.paymentResult.orderId}<br>
            주문 상품: ${props.paymentResult.itemName}<br>
            결제 금액: ${props.paymentResult.amount}원
          </p>
        ` : `<p>${props.paymentResult.message}</p>`,
		  icon: icon,
		  confirmButtonText: '닫기'
		}).then(() => {
		  emit('close'); // 모달 닫기 이벤트 발생
		});
	  }
	}
);
</script>

<template>
  <!-- Swal 모달로 대체되었으므로, template 부분은 비워둡니다 -->
</template>


<!--<script setup>-->
<!--import Swal from "sweetalert2";-->
<!--defineProps({-->
<!--  isVisible: Boolean,-->
<!--  paymentResult: Object-->
<!--});-->

<!--defineEmits(['close']);-->
<!--</script>-->

<!--<template>-->
<!--  <div v-if="isVisible" class="modal-overlay">-->
<!--	<div class="modal-content">-->
<!--	  <h2>결제 결과</h2>-->
<!--	  <p>{{ paymentResult.message }}</p>-->
<!--	  <p v-if="paymentResult.status === 'success'">-->
<!--		주문 번호: {{ paymentResult.orderId }}<br>-->
<!--		결제 금액: {{ paymentResult.amount }}원-->
<!--	  </p>-->
<!--	  <button @click="$emit('close')">닫기</button>-->
<!--	</div>-->
<!--  </div>-->
<!--</template>-->

<!--<style scoped>-->
<!--.modal-overlay {-->
<!--  position: fixed;-->
<!--  top: 0;-->
<!--  left: 0;-->
<!--  width: 100%;-->
<!--  height: 100%;-->
<!--  background-color: rgba(0, 0, 0, 0.5);-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--}-->

<!--.modal-content {-->
<!--  background-color: white;-->
<!--  padding: 20px;-->
<!--  border-radius: 5px;-->
<!--  text-align: center;-->
<!--}-->
<!--</style>-->

<!--<template>-->
<!--  <div v-if="isVisible" class="modal-overlay" @click="closeModal">-->
<!--	<div class="modal
-content" @click.stop>-->
<!--	  <h2>Payment Result</h2>-->
<!--	  <p><strong>Transaction ID:</strong> {{ paymentResult.tid }}</p>-->
<!--	  <p><strong>Message:</strong> {{ paymentResult.msg }}</p>-->
<!--	  <button @click="closeModal" class="btn btn-primary">Close</button>-->
<!--	</div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--export default {-->
<!--  props: {-->
<!--	isVisible: Boolean,-->
<!--	paymentResult: Object,-->
<!--  },-->
<!--  methods: {-->
<!--	closeModal() {-->
<!--	  this.$emit('close');-->
<!--	},-->
<!--  },-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.modal-overlay {-->
<!--  position: fixed;-->
<!--  top: 0;-->
<!--  left: 0;-->
<!--  width: 100%;-->
<!--  height: 100%;-->
<!--  background-color: rgba(0, 0, 0, 0.5);-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--  align-items: center;-->
<!--  z-index: 1000;-->
<!--}-->

<!--.modal-content {-->
<!--  background-color: white;-->
<!--  padding: 20px;-->
<!--  border-radius: 8px;-->
<!--  width: 300px;-->
<!--  text-align: center;-->
<!--}-->

<!--.btn-primary {-->
<!--  margin-top: 10px;-->
<!--}-->
<!--</style>-->
