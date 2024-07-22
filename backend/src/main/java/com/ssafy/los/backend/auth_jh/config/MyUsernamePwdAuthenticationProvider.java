package com.ssafy.los.backend.auth_jh.config;

import com.ssafy.los.backend.auth_jh.domain.Customer;
import com.ssafy.los.backend.auth_jh.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * userDetailManager 역할을 대신하고 있다.
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class MyUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();
        List<Customer> customer = customerRepository.findByUsername(username);
        log.info("userToke1 = {} {}", username, pwd);
        // 유저가 있다면
        if (customer.size() > 0) {
            // 동일한 비밀번호라면
            if (passwordEncoder.matches(pwd, customer.get(0).getPassword())) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(customer.get(0).getRole())); // 권한 추가
                log.info("userToke2 = {} {} {}", username, pwd, authorities.toString());
                return new UsernamePasswordAuthenticationToken(username, pwd, authorities); // 토큰 발행 및 Provider Manager에게 다시 이동
            } else {
                throw new BadCredentialsException("Invalid password!");
            }
        }else {
            throw new BadCredentialsException("No user registered with this details!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
