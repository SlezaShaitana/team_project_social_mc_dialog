package com.project.mc_dialog.websocket;

import com.project.mc_dialog.security.JwtUtils;
import com.project.mc_dialog.service.DialogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DialogService messageService;
    private final JwtUtils jwtUtils;

    @Bean
    public WebSocketHandler getWebSocketHandler() {
        return new WebSocketHandler(jwtUtils, messageService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getWebSocketHandler(), "/api/v1/streaming/ws")
                .setAllowedOrigins("*");

    }
}
