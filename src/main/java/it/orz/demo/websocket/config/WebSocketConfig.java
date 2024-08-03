package it.orz.demo.websocket.config;

import it.orz.demo.websocket.handler.MessageHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * Configuration class for WebSocket.
 * Configures WebSocket handling and message handling.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageHandler messageHandler;

    public WebSocketConfig(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Registers a WebSocket handler for a specific URL pattern and sets the allowed origins and interceptors.
     *
     * @param registry The WebSocketHandlerRegistry used to register the WebSocket handler.
     * @see WebSocketHandlerRegistry
     */
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/message")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

}
