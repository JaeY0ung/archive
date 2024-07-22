package com.ssafy.los.backend.payment.controller;

import com.ssafy.los.backend.payment.model.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
}
