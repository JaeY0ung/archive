package com.ssafy.los.backend.user.filter;

import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, ServletException, IOException {

        // request에서 Authorization 헤더 찾기
        String authorization= request.getHeader("Authorization");

        // 1. Authorization 헤더 검증하기
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            // 종료 조건
            return;
        }

        log.info("인증된 JWT  토큰입니다.");
        // Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 2. 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            log.info("만료된 JWT  토큰입니다.");
            filterChain.doFilter(request, response);

            // 종료 조건
            return;
        }

        // 토큰에서 username과 role 획득
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // userEntity를 생성하여 값 set
        User user = User.builder()
                .email(email)
                .pwdHash("temp-pw") // 채워놓는 용
                .role(role)
                .build();

        // 3. UsernamePasswordAuthenticationToken 토큰 만들어서 SecurityContextHolder에 저장하기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}