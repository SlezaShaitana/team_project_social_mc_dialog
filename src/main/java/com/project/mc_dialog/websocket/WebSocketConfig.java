package com.project.mc_dialog.websocket;

import com.project.mc_dialog.service.AuthenticationService;
import com.project.mc_dialog.service.MessageService;
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

    private final MessageService messageService;

    @Bean
    public WebSocketHandler getWebSocketHandler() {
        return new WebSocketHandler(messageService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getWebSocketHandler(), "/api/v1/streaming/ws")
                .setAllowedOrigins("*");

    }
}
