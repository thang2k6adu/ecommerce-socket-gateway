# Socket Gateway

Realtime Socket.IO transport service for chat.

Chat business logic and chat database are moved to `ecommerce-chat-service`.  
This service now handles socket connect/auth, room join/leave, and realtime fan-out only.

## Architecture

```txt
socket/                    # Transport infra
  support/                 # SocketClientHelper, SocketAckHelper
  InfrastructureSocketHandler.java   # ping
  SocketServerRunner.java            # event registry
  ChatRoomNames.java                 # user:/conversation: room helpers
modules/chat/
  conversation/ConversationSocketHandler.java  # conversation:join / leave
  message/MessageSocketHandler.java            # message:send -> call chat-core
  message/MessageSocketPublisher.java          # message:new / message:created
integrations/chatcore/
  ChatCoreClient.java                          # HTTP client to ecommerce-chat-service
```

## Ports

- HTTP actuator: `8090`
- Socket.IO: `9093`

## Runtime config

- `CHAT_CORE_BASE_URL` (default `http://localhost:8091`)
- `SOCKET_AUTH_ENABLED=true` for JWT auth on socket connect

## Socket events

- Emit: `message:send`, `conversation:join`, `conversation:leave`, `ping`
- Listen: `message:new`, `message:created`, `conversation:joined`, `conversation:left`, `connected`
