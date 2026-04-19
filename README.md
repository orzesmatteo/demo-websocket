# demo-websocket

Small Spring Boot app I keep around to remember how WebSockets wire up. Three endpoints showing different patterns.

## Run

    ./gradlew bootRun

Open http://localhost:8080. There's a tiny page for poking each endpoint.

## Endpoints

| URL | What it does |
| --- | --- |
| `/echo` | Waits 5s then echoes a JSON `Message`. |
| `/broadcast` | Relays every frame to all connected sessions. |
| `/counter` | Increments a counter stored on the session. |

## Tests

    ./gradlew test

Unit tests (Mockito BDD) and integration tests that boot a real server and connect with `StandardWebSocketClient`.

## Stack

Spring Boot 4, Java 21, Jackson 3 (`tools.jackson.*`).
