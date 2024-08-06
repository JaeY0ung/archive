package com.ssafy.los.backend.controller.order;

import com.ssafy.los.backend.dto.order.response.OrderSheetDto;
import com.ssafy.los.backend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.ssafy.los.backend.controller.order fileName       : OrderController author :
 * moongi date           : 8/5/24 description    :
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    // 주문 내역 저장
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderSheetDto orderSheetDto) {
        String redirectUrl = orderService.createOrder(orderSheetDto.getSheetIds(), orderSheetDto.getPayType(), orderSheetDto.getTotalPrice());
        return new ResponseEntity<>(redirectUrl, HttpStatus.CREATED);
    }
}
