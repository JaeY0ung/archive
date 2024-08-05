package com.ssafy.los.backend.controller.order;

import com.ssafy.los.backend.dto.payment.response.ApproveResponse;
import com.ssafy.los.backend.dto.payment.response.ReadyResponse;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * packageName    : com.ssafy.los.backend.controller.order
 * fileName       : OrderController
 * author         : moongi
 * date           : 8/5/24
 * description    :
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final KakaoPayService kakaoPayService;

    @GetMapping("/pay/ready")
    public ReadyResponse readyToKakaoPay() {
        return kakaoPayService.payReady();
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

    @GetMapping("/pay/completed")
    public String payCompleted(@RequestParam("pg_token") String pgToken,  @ModelAttribute("tid") String tid/*, @ModelAttribute("order") Order order,  Model model*/) {

//        String tid = SessionUtils.getStringAttributeValue("tid");
        log.info("결제승인 요청을 인증하는 토큰: " + pgToken);
        log.info("결제 고유번호: " + tid);

        // 카카오 결제 요청하기
        ApproveResponse approveResponse = kakaoPayService.payApprove(tid, pgToken);

        return "redirect:/order/completed";
    }
}
