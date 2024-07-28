package com.ssafy.los.backend.user.filter;

import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    /**
     * 특정 경로에 대해 이 필터를 적용하지 않도록 결정합니다.
     * 로그인, 회원가입에 필요한 경로는 JWT 검증을 거치지 않습니다.
     *
     * @param request 현재 HTTP 요청
     * @return 필터를 적용하지 않아야 하면 true, 그렇지 않으면 false
     * @throws ServletException 서블릿 처리 중 오류 발생 시
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/auth/login")
                || path.equals("/users")
                || path.equals("/users/check-email")
                || path.equals("/auth/access");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, ServletException, IOException {

        log.info("----- JWT 필터에 검증요청이 왔습니다. -----");
        log.info("요청 URL: {}", request.getRequestURL());
        log.info("Servlet 경로: {}", request.getServletPath());
        
        // 1.헤더 또는 쿠키에서 Authorization 찾기
        String authorization = null;
        // 헤더에서 찾기
//        authorization = request.getHeader("Authorization");
//        log.info("헤더에서 Access 토큰을 찾았습니다. = {}", authorization);
        // 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            log.info("cookie : {}", cookie.getName());
            if (cookie.getName().equals("Authorization")) {
                authorization = cookie.getValue();
                log.info("쿠키에서 Refresh 토큰을 찾았습니다. = {}", authorization);
            }
        }

        // TODO : 레디스 추가 검증
        // 1. Authorization 헤더 검증하기
        if (authorization == null) {
            log.info("토큰이 없습니다. ");
            filterChain.doFilter(request, response);
            // 종료 조건
            return;
        }

        log.info("인증된 JWT  토큰입니다.");
        String token = null;
        // 헤더에서 온 경우
        if (authorization.startsWith("Bearer ")) {
            // Bearer 부분 제거 후 순수 토큰만 획득
            token = authorization.split(" ")[1];
        } else {
            token = authorization;
        }

        // 2. 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            log.info("만료된 JWT 토큰입니다.");
            filterChain.doFilter(request, response);
            // 종료 조건
            return;
        }

        log.info("만료되지 않은 JWT 토큰입니다.");

        // 토큰에서 username과 role 획득
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // user를 생성하여 값 set
        User user = User.builder()
                .email(email)
                .role(role)
                .build();

        // 3. UsernamePasswordAuthenticationToken 토큰 만들어서 SecurityContextHolder에 저장하기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}