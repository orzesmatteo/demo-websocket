package it.orz.demo.websocket.handler;

import it.orz.demo.websocket.model.Message;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * Unit tests for MessageHandler using GWT pattern and Mockito BDD.
 */
@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private MessageHandler messageHandler;

    @Test
    void shouldHandleTextMessageSuccessfully() throws Exception {
        // Given
        String messagePayload = "{\"body\":\"Test message\",\"timestamp\":\"2025-12-16T10:00:00Z\"}";
        Message receivedMessage = new Message("Test message", Instant.parse("2025-12-16T10:00:00Z"));
        Message responseMessage = new Message("Output message.", Instant.now());
        String responseJson = "{\"body\":\"Output message.\",\"timestamp\":\"2025-12-16T10:00:05Z\"}";

        given(objectMapper.readValue(messagePayload, Message.class)).willReturn(receivedMessage);
        given(session.isOpen()).willReturn(true);
        given(objectMapper.writeValueAsString(any(Message.class))).willReturn(responseJson);

        TextMessage textMessage = new TextMessage(messagePayload);

        // When
        messageHandler.handleTextMessage(session, textMessage);

        // Then
        then(objectMapper).should(times(1)).readValue(messagePayload, Message.class);
        then(session).should(times(1)).isOpen();
        then(objectMapper).should(times(1)).writeValueAsString(any(Message.class));
        then(session).should(times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void shouldNotSendMessageWhenSessionIsClosed() throws Exception {
        // Given
        String messagePayload = "{\"body\":\"Test message\",\"timestamp\":\"2025-12-16T10:00:00Z\"}";
        Message receivedMessage = new Message("Test message", Instant.parse("2025-12-16T10:00:00Z"));

        given(objectMapper.readValue(messagePayload, Message.class)).willReturn(receivedMessage);
        given(session.isOpen()).willReturn(false);

        TextMessage textMessage = new TextMessage(messagePayload);

        // When
        messageHandler.handleTextMessage(session, textMessage);

        // Then
        then(objectMapper).should(times(1)).readValue(messagePayload, Message.class);
        then(session).should(times(1)).isOpen();
        then(session).should(never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void shouldHandleJsonParsingException() throws Exception {
        // Given
        String invalidPayload = "invalid json";
        given(objectMapper.readValue(anyString(), any(Class.class)))
                .willThrow(new RuntimeException("JSON parse error"));

        TextMessage textMessage = new TextMessage(invalidPayload);

        // When
        messageHandler.handleTextMessage(session, textMessage);

        // Then
        then(objectMapper).should(times(1)).readValue(invalidPayload, Message.class);
        then(session).should(never()).sendMessage(any(TextMessage.class));
    }
}