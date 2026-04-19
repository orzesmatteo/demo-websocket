package it.orz.demo.websocket.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class CounterHandler extends TextWebSocketHandler {

    private static final String COUNT_ATTR = "count";

    private final Logger logger = LoggerFactory.getLogger(CounterHandler.class);

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        int next = ((Integer) session.getAttributes().getOrDefault(COUNT_ATTR, 0)) + 1;
        session.getAttributes().put(COUNT_ATTR, next);

        logger.info("Session {} count -> {}", session.getId(), next);
        session.sendMessage(new TextMessage(Integer.toString(next)));
    }
}
