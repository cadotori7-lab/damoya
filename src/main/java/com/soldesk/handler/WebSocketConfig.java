package com.soldesk.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 메시지 브로커를 활성화하는 어노테이션/ 중간 관리자(STOMP) 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private StompAuthChannelInterceptor stompAuthChannelInterceptor; // STOMP 인증 인터셉터 주입

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/queue"); // 클라이언트가 구독할 수 있는 메시지 브로커의 경로 설정
        config.setApplicationDestinationPrefixes("/app"); // 클라이언트가 메시지를 보낼 때 사용할 경로 설정/app/chat/1 같이 경로를 설정 가능
    } // 누가 누구에게 메시지를 보낼지 결정
    // topic: 여러명에게 브로드캐스트(같은 주소를 구독하고 있는 모든 클라이언트에 전달됨), queue: 1명에게만 메시지 전달

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor()) // 웹소켓 엔드포인트 등록 및 SockJS 사용 설정
                .setAllowedOriginPatterns("*") // 모든 도메인 허용
                .withSockJS(); // 웹소켓을 지원하지 않는 브라우저에서도 웹
    } //클라이언트가 처음 연결할 때 사용할 엔드포인트를 등록, SockJS는 웹소켓을 지원하지 않는 브라우저에서도 웹소켓과 유사한 기능을 제공

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor); // STOMP 인증 인터셉터 등록
     // 클라이언트에서 들어오는 메시지 채널에 대한 설정, STOMP 인증 인터셉터를 등록하여 클라이언트에서 들어오는 메시지를 가로채어 인증 및 권한 부여 처리
    }//시큐리티 인증을 위한 인터셉터 등록.
    
}
