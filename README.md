# Socket Gateway

Realtime Socket.IO gateway + modular chat domain (designed for future extraction to **Chat Service**).

## Architecture

```txt
socket/                    # Transport infra — stays in gateway
  support/                 # SocketClientHelper, SocketAckHelper
  InfrastructureSocketHandler.java   # ping
  SocketServerRunner.java            # event registry
modules/chat/
  conversation/
    ConversationSocketHandler.java   # conversation:join / leave
  message/
    MessageSocketHandler.java        # message:send
  realtime/                # Fan-out to Socket.IO rooms
common/                    # Shared API, exceptions, pagination
```

## Ports

| Port | Purpose |
|------|---------|
| 8090 | HTTP REST + actuator |
| 9093 | Socket.IO |

## Database

PostgreSQL database `chat_db` (default port `5434` in `.env.example`).

```bash
createdb chat_db   # or via Docker
./mvnw spring-boot:run
```

## REST API (dev: header `X-User-Id`)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/chat/conversations` | Create/get direct conversation `{ "otherUserId": "user-b" }` |
| GET | `/api/chat/conversations` | List my conversations |
| GET | `/api/chat/conversations/{id}` | Get conversation |
| POST | `/api/chat/messages` | Send message `{ "conversationId", "content", "clientMessageId?" }` |
| GET | `/api/chat/conversations/{id}/messages` | List messages (paginated) |

## Socket events

| Emit | Description |
|------|-------------|
| `message:send` | Same body as REST `SendMessageRequest` — saves DB + realtime |
| `conversation:join` | Join `conversation:{id}` (membership checked) |
| `ping` | Health |

| Listen | Description |
|--------|-------------|
| `message:new` | Inbox realtime (user room) |
| `message:created` | Open chat screen (conversation room) |
| `connected` | After connect |

## Dev connect

```javascript
const socket = io("http://localhost:9093", { query: { userId: "user-a" } });
socket.emit("message:send", {
  conversationId: 1,
  content: "Hi",
  clientMessageId: "tmp-1",
});
```

```bash
curl -X POST http://localhost:8090/api/chat/conversations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-a" \
  -d '{"otherUserId":"user-b"}'
```
