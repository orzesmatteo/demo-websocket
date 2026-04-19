package it.orz.demo.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BroadcastHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(BroadcastHandler.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        logger.info("Session connected: {} (total: {})", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        logger.info("Session closed: {} (total: {})", session.getId(), sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        logger.info("Broadcasting from {}: {}", session.getId(), payload);

        TextMessage outgoing = new TextMessage(payload);
        for (WebSocketSession target : sessions) {
            if (!target.isOpen()) continue;
            try {
                target.sendMessage(outgoing);
            } catch (IOException e) {
                logger.warn("Failed to deliver to {}: {}", target.getId(), e.getMessage());
            }
        }
    }
}
