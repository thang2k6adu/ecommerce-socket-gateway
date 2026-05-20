# Socket Gateway

Realtime Socket.IO gateway + modular chat domain (designed for future extraction to **Chat Service**).

## Architecture

```txt
socket/                    # Transport infra — stays in gateway
  support/                 # SocketClientHelper, SocketAckHelper
  InfrastructureSocketHandler.java   # ping
  SocketServerRunner.java            # event registry
  ChatRoomNames.java                 # user: / conversation: room helpers
modules/chat/
  conversation/
    ConversationSocketHandler.java   # conversation:join / leave
  message/
    MessageSocketHandler.java        # message:send (inbound)
    MessageSocketPublisher.java      # message fan-out (outbound)
    dto/socket/MessageRealtimePayload.java
common/                    # Shared API, exceptions, pagination
```

## Ports

| Port | Purpose |
|------|---------|
| 8090 | HTTP REST + actuator |
| 9093 | Socket.IO |

## Security (Keycloak JWT — default)

Same pattern as Product Service: OAuth2 Resource Server with `issuer-uri` / `jwk-set-uri`.

- REST: `Authorization: Bearer <access_token>`
- Socket: connect with token

```javascript
const socket = io("http://localhost:9093", {
  auth: { token: accessToken },
  transports: ["websocket", "polling"],
});
```

`userId` in chat DB = JWT **`sub`** (Keycloak user id).

## Local dev (no Keycloak)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Profile `local` sets `socket.auth.enabled=false` and enables `X-User-Id` / `?userId=` for quick tests.

## Database

PostgreSQL `chat_db` (default port `5434`).

```bash
./mvnw spring-boot:run
```

Flyway is enabled by default in main profile:

- `V2__init_chat_schema.sql`: creates base chat tables for new databases.
- `R__sync_support_conversation_schema.sql`: repeatable safety migration that aligns
  `conversations` schema and check constraint with `SUPPORT` type.

Notes:

- Existing databases are adopted via `baseline-on-migrate=true`.
- The repeatable migration fixes legacy `conversations_type_check` that only allowed `DIRECT`.
- Flyway history table is isolated per service via `FLYWAY_TABLE` (default:
  `flyway_schema_history_socket_gateway`) to avoid collisions in shared DB setups.

## REST API

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/chat/conversations/support` | Customer: create/get single **SUPPORT** thread (JWT must not be admin) |
| GET | `/api/chat/conversations/support/queue` | Admin: list unclaimed support conversations |
| POST | `/api/chat/conversations/{id}/claim` | Admin: claim support thread (409 if already claimed) |
| POST | `/api/chat/conversations` | Create/get direct `{ "otherUserId": "<jwt-sub>" }` |
| GET | `/api/chat/conversations` | List conversations |
| GET | `/api/chat/conversations/{id}` | Get conversation |
| POST | `/api/chat/messages` | Send message |
| GET | `/api/chat/conversations/{id}/messages` | List messages (paginated) |

Expose REST via API Gateway: route **`/api/chat/**`** → Socket Gateway (see `API-GATEWAY-ECOMMERCE` `application.yaml`).

## Socket events

| Emit | Description |
|------|-------------|
| `message:send` | Save + realtime |
| `conversation:join` | Join room (membership checked) |
| `ping` | Health |

| Listen | Description |
|--------|-------------|
| `message:new` | User inbox room |
| `message:created` | Open conversation room |
| `connected` | After connect |
