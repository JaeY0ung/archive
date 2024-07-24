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


    // TODO: 실제 로직으로 변경하기 ( Test 용 )
    @Override
    public User getLoginUser() {
        // Retrieve the authentication object from the security context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            String email = ((CustomUserDetails) principal).getUsername();
            // 가져오기
            User findUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
            return findUser;
//            return LoginUser.builder()
//                    .id(findUser.getId())
//                    .email(findUser.getEmail())
//                    .build();
        } else {
            throw new UsernameNotFoundException("User not authenticated");
        }
    }



}
