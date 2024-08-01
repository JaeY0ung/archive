package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.common.model.entity.message.auth.LoginMessage;
import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public User getLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isLogin(principal)) {
            return null;
        }
        String email = ((CustomUserDetails) principal).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        LoginMessage.WRONG_LOGIN_REQUEST.getValue()));
    }

    private boolean isLogin(Object principal) {
        return principal instanceof CustomUserDetails;
    }
}
