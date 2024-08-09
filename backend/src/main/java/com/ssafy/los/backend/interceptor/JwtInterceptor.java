package com.ssafy.los.backend.interceptor;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.CustomUserDetails;
import com.ssafy.los.backend.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("----- JWT 인터셉터에 검증요청이 왔습니다. -----");
        log.info("요청 URL: {}", channel.toString());

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");
            log.info("헤더에서 찾은 Authorization 정보입니다. = {}", authorization);

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0).replace("Bearer ", "");
                try {
                    if (jwtUtil.isExpired(token)) {
                        log.info("만료된 JWT 토큰입니다.");
                        return null; // 연결 거부
                    }
                    log.info("유효한 JWT 토큰입니다.");

                    // 유저 정보 저장하기
                    setAuthenticationToContext(token);
                    accessor.setUser(SecurityContextHolder.getContext().getAuthentication());
                } catch (Exception e) {
                    log.error("JWT 토큰 처리 중 예외 발생", e);
                    return null; // 연결 거부
                }
            } else {
                log.info("유효한 Authorization 헤더가 없습니다.");
                return null; // 연결 거부
            }
        }

        return message;
    }

    private void setAuthenticationToContext(String token) {
        String email = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        User user = User.builder()
                .email(email)
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}