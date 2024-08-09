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

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("----- JWT 인터셉터에 검증요청이 왔습니다. -----");

        // 요청 URL 로깅
        String requestUrl = getRequestUrl(message);
        log.info("요청 URL: {}", requestUrl);

        // 모든 헤더 로깅
//        log.info("모든 헤더:");
//        for (Map.Entry<String, Object> header : message.getHeaders().entrySet()) {
//            log.info("  {}: {}", header.getKey(), header.getValue());
//        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorization = accessor.getNativeHeader("Authorization").get(0);
            log.info("헤더에서 찾은 Authorization 정보입니다. = {}", authorization);

            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.replace("Bearer ", "");
                try {
                    if (jwtUtil.isExpired(token)) {
                        log.info("만료된 JWT 토큰입니다.");
                        return null; // 연결 거부
                    }
                    log.info("유효한 JWT 토큰입니다.");

                    // 유저 정보 저장하기
                    log.info("유저 정보를 저장합니다.");
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

    private String getRequestUrl(Message<?> message) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            String destination = accessor.getDestination();
            if (destination != null) {
                return destination;
            }
            // CONNECT 명령의 경우 destination이 없을 수 있으므로, 연결 URL을 반환
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                return "STOMP CONNECT";
            }
        }
        return "Unknown URL";
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