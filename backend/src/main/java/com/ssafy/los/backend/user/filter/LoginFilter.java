package com.ssafy.los.backend.user.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.los.backend.config.RefreshToken;
import com.ssafy.los.backend.user.model.dto.CustomUserDetails;
import com.ssafy.los.backend.user.model.dto.response.Response;
import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/login"); // Set the login endpoint to /auth/login
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        log.info("로그인 시도: email - {}, pwd - {}", email, password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }


    //로그인 성공시 실행하는 메소드 (여기서 JWT 발급)
    // authentication : Auth에 대한 결과
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        //UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // accessToken
        String accessToken = jwtUtil.createJwt(email, role, 60*60*10L);
        response.addHeader("Authorization", "Bearer " + accessToken);

        // refreshToken
        String refreshToken = jwtUtil.createJwt(email, role, 7 * 24 * 60 * 60 * 1000L);
        response.addHeader("Set-Cookie", createHttpOnlyCookie("refreshToken", refreshToken, "/auth/refresh"));
        RefreshToken redis = new RefreshToken(refreshToken, customUserDetails.getUser().getId());
        refreshTokenRepository.save(redis);
        setTokenResponse(response, accessToken, refreshToken);

        log.info("userDetails.getUser().getId() = {}", customUserDetails.getUser().getId());
        log.info("accessToken - {}", accessToken);
        log.info("refreshToken - {}", refreshToken);
    }

    private String createHttpOnlyCookie(String name, String value, String path) {
        return String.format("%s=%s; Path=%s; HttpOnly; SameSite=None;", name, value, path);
//        return String.format("%s=%s; Path=%s; HttpOnly; Secure; SameSite=Strict;", name, value, path);
    }


    private void setTokenResponse(HttpServletResponse response, String accessToken,
            String refreshToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Map<String, Object> result = new HashMap<>();

        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);

        response.getWriter().println(
                objectMapper.writeValueAsString(
                        Response.success(result)));

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}
