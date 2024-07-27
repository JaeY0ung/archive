package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;


    @Override
    public User getLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            String email = ((CustomUserDetails) principal).getUsername();
            // 가져오기
            User findUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 이메일의 유저를 찾을 수 없습니다. = {}" + email));
            return findUser;
//            return LoginUser.builder()
//                    .id(findUser.getId())
//                    .email(findUser.getEmail())
//                    .build();
        } else {
            throw new UsernameNotFoundException("인증되지 않는 유저입니다.");
        }
    }



}
