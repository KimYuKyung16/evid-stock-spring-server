package com.evid.stockgame.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // ✅ 클라이언트가 서버에 보내는 메시지의 prefix 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // topic = 여러 명에게 보내는 메시지
        // queue = 특정 유저에게 보내는 메시지
        config.enableSimpleBroker("/topic", "/queue");  // 서버 → 클라이언트 구독용 prefix
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 → 서버 전송용 prefix
    }

    // ✅ WebSocket 연결 엔드포인트 설정 (SockJS 포함)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
