package com.ssafy.los.backend.user.filter;

import com.ssafy.los.backend.config.RefreshToken;
import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    private final RefreshTokenRepository refreshTokenRepository;

    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/login", "/users", "/users/check-email", "/auth/access", "/sheets"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return EXCLUDE_PATHS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("----- JWT 필터에 검증요청이 왔습니다. -----");
        log.info("요청 URL: {}", request.getRequestURL());
        log.info("Servlet 경로: {}", request.getServletPath());

        String authorization = request.getHeader("Authorization");
        // TODO : 프론트에서 헤더를 포함해서 요청보내기
        log.info("헤더에서 찾은 Authorization 정보입니다. = {}", authorization);

        if (authorization != null && authorization.startsWith("Bearer ")) {
            log.info("access 토큰 인증을 시작합니다.");
            handleAccessToken(authorization, request, response);
        } else {
            log.info("access 토큰이 불가하여 refresh 토큰 인증을 시작합니다.");
            // TODO: 프론트에서 헤더를 포함해서 보내는 것 가능해지면 지우기
            handleRefreshToken(request, response);
        }

        filterChain.doFilter(request, response);
    }

    private void handleAccessToken(String authorization, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            log.info("만료된 JWT 토큰입니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // TODO : vue axios 인터셉터와 연동하기
            response.getWriter().write("AccessTokenExpired"); 
            return;
        }

        log.info("인증이 되었고 만료되지 않은 JWT 토큰입니다.");

        // 해당 정보를 SecurityContextHolder에 넣기 (반복)
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = User.builder()
                .email(email)
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            log.info("토큰이 없습니다.");
            return;
        }

        if (jwtUtil.isExpired(refreshToken)) {
            log.info("만료된 JWT 토큰입니다.");
            return;
        }

        log.info("인증이 되었고 만료되지 않은 JWT 토큰입니다.");

        if (!refreshTokenRepository.existsById(refreshToken)) {
            log.info("Redis에 존재하지 않는 Refresh 토큰입니다.");
            return;
        }
        log.info("Redis에서 해당하는 Refresh 토큰을 찾았습니다.");

        // 새로운 accessToken 발행
        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String accessToken = jwtUtil.createJwt(email, role, 60*60*10L);
        response.addHeader("Authorization", "Bearer " + accessToken);

        log.info("발급한 accessToken 입니다. = {}", accessToken);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


}