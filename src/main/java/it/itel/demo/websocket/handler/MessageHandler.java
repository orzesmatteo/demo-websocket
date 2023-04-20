package it.itel.demo.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.itel.demo.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;

@Component
public class MessageHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final ObjectMapper objectMapper;

    public MessageHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Message receivedMessage = objectMapper.readValue(message.getPayload(), Message.class);
        logger.info(receivedMessage.toString());
        Thread.sleep(5000); // simulated delay 5 seconds
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new Message("Output message.", Instant.now()))));
    }
}
