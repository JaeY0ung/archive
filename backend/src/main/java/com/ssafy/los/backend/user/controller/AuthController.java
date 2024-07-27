package com.ssafy.los.backend.user.controller;


import com.ssafy.los.backend.config.RefreshToken;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @GetMapping("/auth/token")
    public ResponseEntity<?> requestAccess(HttpServletRequest request, HttpServletResponse response) {

        // 임시 JWT를 검증하기
        String authorization = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("cookie : {}", cookie.getName());
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                    log.info("쿠키에서 임시 JWT 토큰을 찾았습니다. = {}", authorization);
                }
            }
        }

        if (authorization == null) {
            log.info("토큰이 없습니다. ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("인증된 JWT  토큰입니다.");
        String token = authorization;

        // 2. 토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            log.info("만료된 JWT 토큰입니다.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // LoginFilter처럼, 헤더와 쿠키에 저장하기
        // 토큰에서 username과 role 획득
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // accessToken 발급하기
        String accessToken = jwtUtil.createJwt(email, role, 60*60*10L);
        response.addHeader("Authorization", "Bearer " + accessToken);

        // refreshToken 발급하기
        String refreshToken = jwtUtil.createJwt(email, role, 7 * 24 * 60 * 60 * 1000L);
        response.addCookie(createCookie("Authorization", refreshToken));
        // redis 저장하기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(String.format("해당하는 이메일의 유저가 없습니다. = %s", email)));
        RefreshToken redis = new RefreshToken(refreshToken, user.getId()); // 임시
        refreshTokenRepository.save(redis);

        log.info("발급한 accessToken - {}", accessToken);
        log.info("발급한 refreshToken - {}", refreshToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
