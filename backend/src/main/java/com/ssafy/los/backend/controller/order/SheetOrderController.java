package com.ssafy.los.backend.controller.order;

import com.ssafy.los.backend.service.order.SheetOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SheetOrderController {

    private final SheetOrderService sheetOrderService;
}
