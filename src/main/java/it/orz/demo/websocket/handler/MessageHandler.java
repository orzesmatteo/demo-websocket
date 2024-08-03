package it.orz.demo.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.orz.demo.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;

/**
 * Class responsible for handling incoming text messages in a WebSocket session.
 */
@Component
public class MessageHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private final ObjectMapper objectMapper;

    public MessageHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Handle incoming text messages in a WebSocket session.
     *
     * @param session The WebSocket session that received the message.
     * @param message The text message received.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            Message receivedMessage = objectMapper.readValue(message.getPayload(), Message.class);
            logger.info(receivedMessage.toString());
            Thread.sleep(5000); // simulated delay
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new Message("Output message.", Instant.now()))));
        } catch (IOException e) {
            logger.error("An error occurred while parsing the message payload.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("An error occurred while putting the thread to sleep.", e);
        }
    }
}