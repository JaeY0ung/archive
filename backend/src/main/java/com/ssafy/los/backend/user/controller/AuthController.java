package com.ssafy.los.backend.user.controller;


import com.ssafy.los.backend.config.RefreshToken;
import com.ssafy.los.backend.user.filter.JWTFilter;
import com.ssafy.los.backend.user.model.dto.response.UserDetailDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.RefreshTokenRepository;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.user.model.service.UserService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 접두사를 제거하고 실제 토큰만 반환
            String accessToken = bearerToken.substring(7);

            String email = jwtUtil.getUsername(accessToken);
            User user = userService.selectUserByEmail(email);

            UserDetailDto userDetailDto = UserDetailDto.builder()
                    .id(user.getId())
                    .role(user.getRole())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .userImg(user.getUserImg())
                    .birthDate(user.getBirthDate())
                    .gender(user.getGender())
                    .cash(user.getCash())
                    .singleScore(user.getSingleScore())
                    .multiScore(user.getMultiScore())
                    .deletedAt(user.getDeletedAt())
                    .build();
            return new ResponseEntity<>(userDetailDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }


    // TODO: POST mapping으로 바꾸기
    @GetMapping("/refresh")
    public ResponseEntity<?> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            log.info("토큰이 없습니다.");
            return new ResponseEntity<>("Refresh token이 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        if (jwtUtil.isExpired(refreshToken)) {
            log.info("만료된 JWT 토큰입니다.");
            return new ResponseEntity<>("Refresh token이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
        }

        log.info("인증이 되었고 만료되지 않은 JWT 토큰입니다.");

        if (!refreshTokenRepository.existsById(refreshToken)) {
            log.info("Redis에 존재하지 않는 Refresh 토큰입니다.");
            return new ResponseEntity<>("Refresh token이 Redis에 존재하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        log.info("Redis에서 해당하는 Refresh 토큰을 찾았습니다.");

        // 새로운 accessToken 발행
        // TODO : refreshToken Rotate 사용 가능
        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String accessToken = jwtUtil.createJwt(email, role, 60*60*10L);
        response.addHeader("Authorization", "Bearer " + accessToken);

        log.info("발급한 accessToken 입니다. = {}", accessToken);

        return new ResponseEntity<>(HttpStatus.OK);
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

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("logout 요청이 들어왔습니다.");

        // 쿠키에서 refreshToken 찾기
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            // refreshToken 쿠키 제거
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            log.info("refreshToken 쿠키를 제거했습니다.");

            // Redis에서 refreshToken 제거
            refreshTokenRepository.deleteById(refreshToken);
             log.info("Redis에서 refreshToken을 제거했습니다. = {}", refreshToken);
        } else {
            log.info("refreshToken을 쿠키에서 찾을 수 없습니다.");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/token")
    public ResponseEntity<?> requestAccess(HttpServletRequest request, HttpServletResponse response) {

        // 임시 JWT 검증하기
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
        response.addCookie(createCookie("refreshToken", refreshToken));
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