package com.ssafy.los.backend.user.controller;

import com.ssafy.los.backend.user.model.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private AuthService authService;


    @GetMapping("/")
    public String home() {
        return "Hello World";
    }

    @GetMapping("/test/user")
    public String home1() {
        log.info("현재 로그인 유저 정보입니다. " + authService.getLoginUser().getId());
        return "유저만 들어올 수 있는 곳입니다. ROLE_USER";
    }

    @GetMapping("/test/admin")
    public String home2() {
        return "관리자만 들어올 수 있는 곳입니다. ROLE_ADMIN";
    }


}
