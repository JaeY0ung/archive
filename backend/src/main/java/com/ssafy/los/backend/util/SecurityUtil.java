package com.ssafy.los.backend.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Slf4j
@RequiredArgsConstructor
public class SecurityUtil {
    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static Long getCurrentUserId() {
        /**
         * static method인 getCurrentUsername()을 호출하여 SecurityContext에 저장된 현재 요청을
         * 보낸 회원의 username을 간단하게 얻을 수 있습니다.
         * 이 메서드를 활용하여 해당 회원의 특정 LIST를 조회하는 작업등을 진행
         */
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw  new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }

}
