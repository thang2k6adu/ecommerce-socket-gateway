# Socket Gateway

Realtime Socket.IO gateway for ecommerce. Handles WebSocket connections, room management, and event fan-out (Kafka integration planned).

## Ports

| Port | Purpose |
|------|---------|
| 8090 | Spring Boot HTTP (actuator health) |
| 9093 | Socket.IO (netty-socketio) |

## Run locally

```bash
cp .env.example .env   # optional
./mvnw spring-boot:run
```

Health: `curl http://localhost:8090/actuator/health`

## Dev connect (auth disabled)

Default `SOCKET_AUTH_ENABLED=false`. Connect with `userId` query param:

```javascript
import { io } from "socket.io-client";

const socket = io("http://localhost:9093", {
  query: { userId: "123" },
  transports: ["websocket", "polling"],
});

socket.on("connect", () => console.log("connected", socket.id));
socket.on("connected", (data) => console.log("server ack", data));
socket.emit("ping", "hello");
socket.on("pong", (data) => console.log("pong", data));
```

## Events

| Client → Server | Server → Client |
|-----------------|-----------------|
| `ping` | `pong` |
| `conversation:join` | `conversation:joined` |
| `conversation:leave` | `conversation:left` |

On connect, server joins `user:{userId}` and emits `connected`.

## JWT auth (production)

Set `SOCKET_AUTH_ENABLED=true` and pass token:

```javascript
io("http://localhost:9093", {
  auth: { token: accessToken },
  transports: ["websocket", "polling"],
});
```

Or query `?token=...` / header `Authorization: Bearer ...`.
