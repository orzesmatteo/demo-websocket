package it.orz.demo.websocket.config;

import it.orz.demo.websocket.handler.BroadcastHandler;
import it.orz.demo.websocket.handler.CounterHandler;
import it.orz.demo.websocket.handler.MessageHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * Registers every WebSocket endpoint exposed by the demo.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageHandler messageHandler;
    private final BroadcastHandler broadcastHandler;
    private final CounterHandler counterHandler;

    public WebSocketConfig(MessageHandler messageHandler,
                           BroadcastHandler broadcastHandler,
                           CounterHandler counterHandler) {
        this.messageHandler = messageHandler;
        this.broadcastHandler = broadcastHandler;
        this.counterHandler = counterHandler;
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/echo")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        registry.addHandler(broadcastHandler, "/broadcast")
                .setAllowedOrigins("*");

        registry.addHandler(counterHandler, "/counter")
                .setAllowedOrigins("*");
    }
}
