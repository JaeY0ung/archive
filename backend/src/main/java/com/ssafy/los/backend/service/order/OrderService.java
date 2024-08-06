package com.ssafy.los.backend.service.order;

import com.ssafy.los.backend.constant.PayType;

import java.util.List;

public interface OrderService {

    String createOrder(List<Long> sheetIds, PayType payType, Long totalPrice);

    void cancelOrder(Long orderId);
}
