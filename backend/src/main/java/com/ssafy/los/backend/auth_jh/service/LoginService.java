package com.ssafy.los.backend.auth_jh.service;

import com.ssafy.los.backend.auth_jh.domain.Customer;
import com.ssafy.los.backend.auth_jh.dto.CustomerCreateRequest;
import com.ssafy.los.backend.auth_jh.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final CustomerRepository userRepository;
    private PasswordEncoder passwordEncoder;


    // 유저 등록하기
    public Long saveCustomer(CustomerCreateRequest request) {

        String hashPwd = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashPwd); // 패스워드 변환
        Customer customer = Customer.builder()
                .username(request.getUsername())
                .password(hashPwd)
                .role(request.getRole())
                .build();
        userRepository.save(customer);

        return customer.getId();
    }


}
