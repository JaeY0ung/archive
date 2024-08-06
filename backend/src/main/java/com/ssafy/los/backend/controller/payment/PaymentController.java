package com.ssafy.los.backend.controller.payment;

import com.ssafy.los.backend.dto.payment.response.ApproveResponse;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PaymentController {

    private final KakaoPayService kakaoPayService;

//    @GetMapping("/ready")
//    public void readyToKakaoPay(HttpServletResponse response) throws IOException {
//        ReadyResponse readyResponse = kakaoPayService.payReady();
//        String redirectUrl = readyResponse.getNext_redirect_pc_url();
//        response.sendRedirect(redirectUrl);
//        String tid = readyResponse.getTid();
//    }

    @GetMapping("/completed")
    public void payCompleted(@RequestParam("pg_token") String pgToken, HttpServletResponse response) throws IOException {

        // 카카오 결제 요청하기
        ApproveResponse kakaoApprove = kakaoPayService.payApprove(pgToken);
        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + kakaoApprove.getTid());
//        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
        // 결제 완료 후 프론트엔드 페이지로 리디렉션
        response.sendRedirect("http://localhost:3000/order"); // 프론트엔드의 성공 페이지로 리디렉션
    }

}
