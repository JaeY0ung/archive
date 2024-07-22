package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    // TODO: 실제 로직으로 변경하기 ( Test 용 )
    @Override
    public User getLoginUser() {
        return userService.selectUserById(1L);
    }
}
