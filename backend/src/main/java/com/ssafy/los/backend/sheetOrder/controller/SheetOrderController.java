package com.ssafy.los.backend.sheetOrder.controller;

import com.ssafy.los.backend.sheetOrder.model.service.SheetOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SheetOrderController {

    private final SheetOrderService sheetOrderService;
}
