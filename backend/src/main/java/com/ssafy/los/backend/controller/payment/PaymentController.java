package com.ssafy.los.backend.controller.payment;

import com.ssafy.los.backend.domain.entity.Order;
import com.ssafy.los.backend.domain.repository.order.OrderRepository;
import com.ssafy.los.backend.dto.payment.response.ApproveResponse;
import com.ssafy.los.backend.service.order.OrderService;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PaymentController {

    private final KakaoPayService kakaoPayService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @GetMapping("/completed")
    public void payCompleted(@RequestParam("pg_token") String pgToken,
            HttpServletResponse response)
            throws IOException {
        try {
            // 카카오 결제 요청하기
            ApproveResponse kakaoApprove = kakaoPayService.payApprove(pgToken);
            log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
            log.info("결제 고유번호: " + kakaoApprove.getTid());

//            log.info("결제 총 금액: " + kakaoApprove.getAmount());
            Optional<Order> orderItem = orderRepository.findByTid(kakaoApprove.getTid());
            int totalPrice = orderItem.get().getTotalPrice();
            String redirectUrl = UriComponentsBuilder.fromUriString(
                            allowedOrigins + "/payment/result")
                    .queryParam("status", "success")
                    .queryParam("orderId", kakaoApprove.getPartner_order_id())
                    .queryParam("itemName", kakaoApprove.getItem_name())
                    .queryParam("amount", totalPrice)
                    .toUriString();

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            log.error("결제 승인 중 오류 발생", e);
            String errorRedirectUrl = UriComponentsBuilder.fromUriString(
                            allowedOrigins + "/payment/result")
                    .queryParam("status", "error")
                    .queryParam("message", "결제 처리 중 오류가 발생했습니다.")
                    .toUriString();

            response.sendRedirect(errorRedirectUrl);
        }

        // 결제 완료 후 프론트엔드 페이지로 리디렉션
//        response.sendRedirect(allowedOrigins + "/order"); // 프론트엔드의 성공 페이지로 리디렉션
//        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public void cancel(HttpServletResponse response) throws IOException {
        log.info("결제 중간에 취소");
        String errorRedirectUrl = UriComponentsBuilder.fromUriString(
                        allowedOrigins + "/payment/result")
                .queryParam("status", "error")
                .queryParam("message", "결제 중 취소하였습니다..")
                .toUriString();
        response.sendRedirect(errorRedirectUrl);
    }

    @GetMapping("/fail")
    public void fail(HttpServletResponse response) throws IOException {
        log.info("결제 실패");
        String errorRedirectUrl = UriComponentsBuilder.fromUriString(
                        allowedOrigins + "/payment/result")
                .queryParam("status", "error")
                .queryParam("message", "결제가 실패였습니다..")
                .toUriString();
        response.sendRedirect(errorRedirectUrl);
    }

}
