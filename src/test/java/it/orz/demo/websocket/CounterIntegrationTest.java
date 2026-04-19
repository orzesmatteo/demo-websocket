package it.orz.demo.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the /counter endpoint.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CounterIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldIncrementPerSession() throws Exception {
        // Given
        String url = "ws://localhost:" + port + "/counter";
        StandardWebSocketClient client = new StandardWebSocketClient();

        List<String> replies = new ArrayList<>();
        CompletableFuture<Void> gotThree = new CompletableFuture<>();

        WebSocketSession session = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage m) {
                synchronized (replies) {
                    replies.add(m.getPayload());
                    if (replies.size() == 3) gotThree.complete(null);
                }
            }
        }, url).get(5, TimeUnit.SECONDS);

        // When
        session.sendMessage(new TextMessage("tick"));
        session.sendMessage(new TextMessage("tick"));
        session.sendMessage(new TextMessage("tick"));
        gotThree.get(5, TimeUnit.SECONDS);

        // Then
        assertThat(replies).containsExactly("1", "2", "3");

        session.close();
    }

    @Test
    void shouldResetForNewSession() throws Exception {
        // Given
        String url = "ws://localhost:" + port + "/counter";
        StandardWebSocketClient client = new StandardWebSocketClient();

        // When - first session counts up then closes
        CompletableFuture<String> first = new CompletableFuture<>();
        WebSocketSession a = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage m) {
                first.complete(m.getPayload());
            }
        }, url).get(5, TimeUnit.SECONDS);
        a.sendMessage(new TextMessage("tick"));
        assertThat(first.get(5, TimeUnit.SECONDS)).isEqualTo("1");
        a.close();

        // And - a new session starts from zero
        CompletableFuture<String> second = new CompletableFuture<>();
        WebSocketSession b = client.execute(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession s, TextMessage m) {
                second.complete(m.getPayload());
            }
        }, url).get(5, TimeUnit.SECONDS);
        b.sendMessage(new TextMessage("tick"));

        // Then
        assertThat(second.get(5, TimeUnit.SECONDS)).isEqualTo("1");
        b.close();
    }
}
