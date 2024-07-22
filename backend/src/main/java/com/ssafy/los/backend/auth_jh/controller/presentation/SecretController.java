package com.ssafy.los.backend.auth_jh.controller.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecretController {

    @GetMapping("/secret")
    public String getAccountDetails() {
        return "인증된 사람만 올 수 있는 곳";
    }

}
