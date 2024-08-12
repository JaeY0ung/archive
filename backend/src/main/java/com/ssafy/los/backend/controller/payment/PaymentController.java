package com.ssafy.los.backend.controller.payment;

import com.ssafy.los.backend.dto.payment.response.ApproveResponse;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

//    @GetMapping("/ready")
//    public void readyToKakaoPay(HttpServletResponse response) throws IOException {
//        ReadyResponse readyResponse = kakaoPayService.payReady();
//        String redirectUrl = readyResponse.getNext_redirect_pc_url();
//        response.sendRedirect(redirectUrl);
//        String tid = readyResponse.getTid();
//    }

    @GetMapping("/completed")
    public void payCompleted(@RequestParam("pg_token") String pgToken,
            HttpServletResponse response)
            throws IOException {
        try {
            // 카카오 결제 요청하기
            ApproveResponse kakaoApprove = kakaoPayService.payApprove(pgToken);
            log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
            log.info("결제 고유번호: " + kakaoApprove.getTid());

            String redirectUrl = UriComponentsBuilder.fromUriString(
                            allowedOrigins + "/payment/result")
                    .queryParam("status", "success")
                    .queryParam("orderId", kakaoApprove.getPartner_order_id())
//                    .queryParam("amount", kakaoApprove.getAmount().getTotal())
                    .toUriString();

            response.sendRedirect(redirectUrl);

            // 결제 결과 정보를 담은 객체를 생성
//            Map<String, Object> result = new HashMap<>();
//            result.put("status", "success");
//            result.put("message", "결제가 성공적으로 완료되었습니다.");
//            result.put("orderId", kakaoApprove.getPartner_order_id());
//            result.put("amount", kakaoApprove.getAmount().getTotal());
//            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("결제 승인 중 오류 발생", e);
            String errorRedirectUrl = UriComponentsBuilder.fromUriString(
                            allowedOrigins + "/payment/result")
                    .queryParam("status", "error")
                    .queryParam("message", "결제 처리 중 오류가 발생했습니다.")
                    .toUriString();

            response.sendRedirect(errorRedirectUrl);
//            log.error("결제 승인 중 오류 발생", e);
//            Map<String, Object> errorResult = new HashMap<>();
//            errorResult.put("status", "error");
//            errorResult.put("message", "결제 처리 중 오류가 발생했습니다.");
//
//            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 결제 완료 후 프론트엔드 페이지로 리디렉션
//        response.sendRedirect(allowedOrigins + "/order"); // 프론트엔드의 성공 페이지로 리디렉션
//        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }
}
