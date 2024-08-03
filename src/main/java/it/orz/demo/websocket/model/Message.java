package it.orz.demo.websocket.model;

import java.time.Instant;

/**
 * The Message class represents a message object with a body and timestamp.
 */
public class Message {

    private String body;

    private Instant timestamp;

    public Message(String body, Instant timestamp) {
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "body='" + body + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
