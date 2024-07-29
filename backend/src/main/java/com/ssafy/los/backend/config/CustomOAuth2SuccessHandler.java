package com.ssafy.los.backend.config;

import com.ssafy.los.backend.user.model.dto.CustomOAuth2User;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = customOAuth2User.getEmail();

        // 회원가입
        if (customOAuth2User.getAuthorities().isEmpty()) {
            // TODO : 더 많은 정보를 회원가입에 제공하기
            response.sendRedirect(allowedOrigins + "/register?email=" + email);
        } else {
            // 로그인
            Collection<? extends GrantedAuthority> authorities = customOAuth2User.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority authority = iterator.next();
            String role = authority.getAuthority();

            // JWT 토큰 발급
            String token = jwtUtil.createJwt(email, role, 60 * 60 * 60L);
            log.info("OAuth2에서 임시 JWT를 발급한 뒤 쿠키에 담아 보냈습니다. = {} ", token);

            response.addCookie(createCookie("Authorization", token));

            // 프론트 엔드 주소 -> /auth/request
            response.sendRedirect(allowedOrigins + "/auth-success");
        }

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}