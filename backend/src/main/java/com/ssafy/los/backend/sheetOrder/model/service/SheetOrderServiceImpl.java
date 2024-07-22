package com.ssafy.los.backend.sheetOrder.model.service;

import com.ssafy.los.backend.sheetOrder.model.repository.SheetOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SheetOrderServiceImpl implements SheetOrderService {

    private final SheetOrderRepository sheetOrderRepository;

}
