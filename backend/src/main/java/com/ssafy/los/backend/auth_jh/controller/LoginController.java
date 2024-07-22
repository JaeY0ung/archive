package com.ssafy.los.backend.auth_jh.controller;

import com.ssafy.los.backend.auth_jh.domain.Customer;
import com.ssafy.los.backend.auth_jh.dto.CustomerCreateRequest;
import com.ssafy.los.backend.auth_jh.repository.CustomerRepository;
import com.ssafy.los.backend.auth_jh.service.LoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class LoginController {

    private LoginService loginService;
    private CustomerRepository customerRepository;

    // 유저 등록하기
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody CustomerCreateRequest request) {
        log.info("request = " + request.toString());
        Long registerUserId = loginService.saveCustomer(request);
        try {
            if (registerUserId > 0) {
                return new ResponseEntity<String>(request.toString(), HttpStatus.CREATED);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @GetMapping("/user")
    public Customer getUserDetailsAfterLogin(Authentication authentication) {
        List<Customer> customers = customerRepository.findByUsername(authentication.getName());
        if (customers.size() > 0) {
            return customers.get(0);
        } else {
            return null;
        }

    }

}
