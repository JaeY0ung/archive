package com.ssafy.los.backend.config.web_socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/wait"); // 발행자가 /room 경로로 준비완료 신호를 보내면 구독자들에게 전달.
        config.setApplicationDestinationPrefixes(
                "/app"); // 발행자가 /app 경로로 메시지를 보내면 '가공'해서 구독자들에게 전달.
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Front에서 /battle 페이지로 들어갔을 때, 해당 엔드포인트로 연결되도록 설정해야 한다.

        // local
//        registry.addEndpoint("/archive-websocket").setAllowedOrigins(allowedOrigins)
//                .withSockJS(); // 커넥션을 맺는 경로 설정
        // https 웹소켓 지원
        registry.addEndpoint("/archive-websocket").setAllowedOrigins(allowedOrigins)
                .withSockJS(); // 커넥션을 맺는 경로 설정
    }

}
