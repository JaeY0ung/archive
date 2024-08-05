package com.ssafy.los.backend.service.order;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.repository.order.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Sheet> searchSheetByIds(List<Long> ids) {
        return orderRepository.findSheetsByIds(ids);
    }
}
