package it.orz.demo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.orz.demo.websocket.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for WebSocket functionality using GWT pattern.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private String webSocketUrl;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        webSocketUrl = "ws://localhost:" + port + "/message";
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldConnectToWebSocketEndpoint() throws Exception {
        // Given
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> sessionFuture = new CompletableFuture<>();

        // When
        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                sessionFuture.complete(session);
            }
        }, webSocketUrl).get(5, TimeUnit.SECONDS);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.isOpen()).isTrue();
        session.close();
    }

    @Test
    void shouldSendAndReceiveMessage() throws Exception {
        // Given
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<String> responseFuture = new CompletableFuture<>();

        Message requestMessage = new Message("Test message from integration test", Instant.now());
        String requestJson = objectMapper.writeValueAsString(requestMessage);

        // When
        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                responseFuture.complete(message.getPayload());
            }
        }, webSocketUrl).get(5, TimeUnit.SECONDS);

        session.sendMessage(new TextMessage(requestJson));
        String responseJson = responseFuture.get(10, TimeUnit.SECONDS);

        // Then
        assertThat(responseJson).isNotNull();
        Message responseMessage = objectMapper.readValue(responseJson, Message.class);
        assertThat(responseMessage.getBody()).isEqualTo("Output message.");
        assertThat(responseMessage.getTimestamp()).isNotNull();

        session.close();
    }

    @Test
    void shouldHandleMultipleMessages() throws Exception {
        // Given
        StandardWebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<Integer> messageCountFuture = new CompletableFuture<>();
        final int[] receivedCount = {0};

        Message requestMessage = new Message("Test message", Instant.now());
        String requestJson = objectMapper.writeValueAsString(requestMessage);

        // When
        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                receivedCount[0]++;
                if (receivedCount[0] == 2) {
                    messageCountFuture.complete(receivedCount[0]);
                }
            }
        }, webSocketUrl).get(5, TimeUnit.SECONDS);

        session.sendMessage(new TextMessage(requestJson));
        session.sendMessage(new TextMessage(requestJson));

        Integer count = messageCountFuture.get(15, TimeUnit.SECONDS);

        // Then
        assertThat(count).isEqualTo(2);

        session.close();
    }
}