package it.orz.demo.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the /broadcast endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BroadcastIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldDeliverMessageToAllConnectedSessions() throws Exception {
        // Given
        String url = "ws://localhost:" + port + "/broadcast";
        StandardWebSocketClient client = new StandardWebSocketClient();

        CompletableFuture<String> senderReceived = new CompletableFuture<>();
        CompletableFuture<String> listenerReceived = new CompletableFuture<>();

        WebSocketSession sender = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage m) {
                senderReceived.complete(m.getPayload());
            }
        }, url).get(5, TimeUnit.SECONDS);

        WebSocketSession listener = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage m) {
                listenerReceived.complete(m.getPayload());
            }
        }, url).get(5, TimeUnit.SECONDS);

        // When
        sender.sendMessage(new TextMessage("hello room"));

        // Then
        assertThat(senderReceived.get(5, TimeUnit.SECONDS)).isEqualTo("hello room");
        assertThat(listenerReceived.get(5, TimeUnit.SECONDS)).isEqualTo("hello room");

        sender.close();
        listener.close();
    }
}
