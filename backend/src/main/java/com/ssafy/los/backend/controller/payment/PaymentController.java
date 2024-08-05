package com.ssafy.los.backend.controller.payment;

import com.ssafy.los.backend.dto.payment.response.ApproveResponse;
import com.ssafy.los.backend.dto.payment.response.ReadyResponse;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    @GetMapping("/ready")
    public void readyToKakaoPay(HttpServletResponse response) throws IOException {
        ReadyResponse readyResponse = kakaoPayService.payReady();
        String redirectUrl = readyResponse.getNext_redirect_pc_url();
        response.sendRedirect(redirectUrl);
        String tid = readyResponse.getTid();

//        return kakaoPayService.payReady();
//        return new ResponseEntity<>(kakaoPayService.payReady(), HttpStatus.OK);
    }

//    @PostMapping("/pay/ready")
//    public @ResponseBody ReadyResponse payReady(@RequestBody OrderCreateForm orderCreateForm) {
//
//        String name = orderCreateForm.getName();
//        int totalPrice = orderCreateForm.getTotalPrice();
//
//        log.info("주문 상품 이름: " + name);
//        log.info("주문 금액: " + totalPrice);
//
//        // 카카오 결제 준비하기
//        ReadyResponse readyResponse = kakaoPayService.payReady(name, totalPrice);
//        // 세션에 결제 고유번호(tid) 저장
//        SessionUtils.addAttribute("tid", readyResponse.getTid());
//        log.info("결제 고유번호: " + readyResponse.getTid());
//
//        return readyResponse;
//    }

    @GetMapping("/completed")
    public ResponseEntity<?> payCompleted(@RequestParam("pg_token") String pgToken) {

        // 카카오 결제 요청하기
        ApproveResponse kakaoApprove = kakaoPayService.payApprove(pgToken);
        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + kakaoApprove.getTid());

        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }

}
