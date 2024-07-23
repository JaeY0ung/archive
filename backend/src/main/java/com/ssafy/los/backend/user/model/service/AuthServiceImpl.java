package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserCreateRequest;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void registerUser(UserCreateRequest request) {

        User user = User.builder()
                .nickname(request.getUsername())
                .pwdHash(bCryptPasswordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    // TODO: 실제 로직으로 변경하기 ( Test 용 )
    @Override
    public User getLoginUser() {
        return userService.selectUserById(1L);
    }



}
