package com.soldesk.handler;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    // 웹소켓 메시지가 서버 채널로 들어오기 전 가로채서 처리
    // STOMP 메시지 채널에 대한 인증 및 권한 부여를 처리하는 인터셉터

    private static final Logger logger = LoggerFactory.getLogger(StompAuthChannelInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor ==null || StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        } // CONNECT 명령어가 아닌 경우 메시지를 그대로 반환하여 인증을 수행하지 않음 - 연결 시점에서만 검사한다
        Principal user = accessor.getUser();
        if (user == null) {
                System.out.println("🚨 STOMP 연결 거부: 미 로그인 사용자");
                throw new IllegalArgumentException("인증되지 않은 사용자의 메시지 차단");
        }
        accessor.setUser(user);
        logger.info("STOMP 연결 허용: 사용자={}, 명령={}", user.getName(), accessor.getCommand());
        return message;
    } //인증된 사용자인지 확인하고, 인증되지 않은 사용자의 메시지를 차단하거나 필요한 경우 추가적인 인증 절차를 수행
}
