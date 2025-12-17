package it.orz.demo.websocket.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Message model using GWT pattern.
 */
class MessageTest {

    @Test
    void shouldCreateMessageWithConstructor() {
        // Given
        String body = "Test message";
        Instant timestamp = Instant.parse("2025-12-16T10:00:00Z");

        // When
        Message message = new Message(body, timestamp);

        // Then
        assertThat(message.getBody()).isEqualTo(body);
        assertThat(message.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void shouldSetAndGetBody() {
        // Given
        Message message = new Message("Initial", Instant.now());
        String newBody = "Updated message";

        // When
        message.setBody(newBody);

        // Then
        assertThat(message.getBody()).isEqualTo(newBody);
    }

    @Test
    void shouldSetAndGetTimestamp() {
        // Given
        Message message = new Message("Test", Instant.now());
        Instant newTimestamp = Instant.parse("2025-12-16T12:00:00Z");

        // When
        message.setTimestamp(newTimestamp);

        // Then
        assertThat(message.getTimestamp()).isEqualTo(newTimestamp);
    }

    @Test
    void shouldGenerateToString() {
        // Given
        String body = "Test message";
        Instant timestamp = Instant.parse("2025-12-16T10:00:00Z");
        Message message = new Message(body, timestamp);

        // When
        String result = message.toString();

        // Then
        assertThat(result).contains("Test message");
        assertThat(result).contains("2025-12-16T10:00:00Z");
        assertThat(result).startsWith("Message{");
    }
}