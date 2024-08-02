package com.ssafy.los.backend.service.order;

import com.ssafy.los.backend.domain.repository.order.SheetOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SheetOrderServiceImpl implements SheetOrderService {

    private final SheetOrderRepository sheetOrderRepository;

}
