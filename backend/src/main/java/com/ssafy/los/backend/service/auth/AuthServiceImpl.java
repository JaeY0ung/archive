package com.ssafy.los.backend.service.auth;

import com.ssafy.los.backend.config.security.RefreshToken;
import com.ssafy.los.backend.constant.LoginMessage;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.auth.RefreshTokenRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.user.CustomUserDetails;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.response.UserDetailDto;
import com.ssafy.los.backend.service.user.UserStatusService;
import com.ssafy.los.backend.util.FileUploadUtil;
import com.ssafy.los.backend.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final OAuth2UserService oAuth2UserService;
    private final UserStatusService userStatusService;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public UserDetailDto getUserInfo(HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        }

        UserDetailDto userDetailDto = UserDetailDto.builder()
                .id(loginUser.getId())
                .role(loginUser.getRole())
                .nickname(loginUser.getNickname())
                .email(loginUser.getEmail())
                .userImgName(loginUser.getUserImg())
                .cash(loginUser.getCash())
                .singleScore(loginUser.getSingleScore())
                .multiScore(loginUser.getMultiScore())
                .build();

        userDetailDto.loadUserImg(fileUploadUtil);
        return userDetailDto;
    }

    @Override
    public Long saveOAuth2User(UserCreateDto userCreateDto) {
        return oAuth2UserService.saveOAuth2User(userCreateDto);
    }

    @Override
    public String getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null || jwtUtil.isExpired(refreshToken)) {
            return null;
        }

        if (!refreshTokenRepository.existsById(refreshToken)) {
            log.info("Redis에 존재하지 않는 Refresh 토큰입니다: " + refreshToken);
            return null;
        }

        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        log.info("email: {}, role: {}에 해당하는 accessToken을 재발급합니다. ", email, role);
        return jwtUtil.createAccessJwt(email, role);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            log.info("refreshToken을 쿠키에서 찾을 수 없습니다.");
            return;
        }

        String email = jwtUtil.getUsername(refreshToken);
        User user = userRepository.findByEmailAndDeletedAtNull(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이메일의 유저가 없습니다"));
        String userId = user.getId().toString();

        // refreshToken 쿠키 제거
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        refreshTokenRepository.deleteById(refreshToken);
        userStatusService.setUserOffline(Long.parseLong(userId));
    }

    @Override
    public void requestAccess(HttpServletRequest request, HttpServletResponse response) {
        String authorization = extractOAuthAuthorizationFromCookie(request);
        if (authorization == null || jwtUtil.isExpired(authorization)) {
            log.info("토큰이 없습니다. 혹은 만료되었습니다");
            return;
        }

        String email = jwtUtil.getUsername(authorization);
        String role = jwtUtil.getRole(authorization);

        String accessToken = jwtUtil.createAccessJwt(email, role);
        response.addHeader("Authorization", "Bearer " + accessToken);

        String refreshToken = jwtUtil.createRefreshJwt(email, role);
        response.addCookie(createCookie("refreshToken", refreshToken));

        User user = userRepository.findByEmailAndDeletedAtNull(email)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 이메일의 유저가 없습니다"));
        RefreshToken redis = new RefreshToken(refreshToken, user.getId());
        refreshTokenRepository.save(redis);

        // OAuthAuthorization 쿠키 제거
        removeOAuthAuthorizationCookie(response);
    }

    @Override
    public User getLoginUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isLogin(principal)) {
            return null;
        }
        String email = ((CustomUserDetails) principal).getUsername();
        return userRepository.findByEmailAndDeletedAtNull(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        LoginMessage.WRONG_LOGIN_REQUEST.getValue()));
    }

    private boolean isLogin(Object principal) {
        return principal instanceof CustomUserDetails;
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

    private String extractOAuthAuthorizationFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("OAuthAuthorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void removeOAuthAuthorizationCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("OAuthAuthorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

}
