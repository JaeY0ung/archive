package com.ssafy.los.backend.auth_jh.controller.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HiController {

    @GetMapping("/hi")
    public String getAccountDetails() {
        return "Here are the account details from the DB";
    }

}
