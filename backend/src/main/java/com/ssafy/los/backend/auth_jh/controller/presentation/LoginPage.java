package com.ssafy.los.backend.auth_jh.controller.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class LoginPage {

    @GetMapping("/loginPage")
    public String getAccountDetails() {
        return "loginpp";
    }

}