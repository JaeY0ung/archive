package com.ssafy.los.backend.auth_jh.controller.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping("/")
    public String getAccountDetails() {
        return "모두 접근이 가능!";
    }

}

