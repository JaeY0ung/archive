package com.ssafy.los.backend.service.order;

import com.ssafy.los.backend.constant.PayType;
import com.ssafy.los.backend.domain.entity.Order;
import com.ssafy.los.backend.domain.entity.OrderSheet;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.order.OrderRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.dto.payment.response.ReadyResponse;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.payment.KakaoPayService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final SheetRepository sheetRepository;
    private final AuthService authService;
    private final KakaoPayService kakaoPayService;

    @Transactional
    @Override
    public String createOrder(List<Long> sheetIds, PayType payType, Long totalPrice) {
        User loginUser = authService.getLoginUser();
        List<OrderSheet> orderSheets = new ArrayList<>();

        for (Long sheetId : sheetIds) {
            createOrderSheets(sheetId, orderSheets);
        }

        Order createOrder = Order.createOrder(loginUser, orderSheets);
        orderRepository.save(createOrder);

        ReadyResponse readyResponse = kakaoPayService.payReady(createOrder, totalPrice);
        createOrder.setTid(readyResponse.getTid());

        log.info("================{}", readyResponse.getNext_redirect_pc_url());

        return readyResponse.getNext_redirect_pc_url();
    }

    @Override
    public void cancelOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    private void createOrderSheets(Long sheetId, List<OrderSheet> orderSheets) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        OrderSheet orderSheet = OrderSheet.createOrderSheet(sheet);
        orderSheets.add(orderSheet);
    }
}
