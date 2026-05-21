# Graph Report - SOCKET-GATEWAY-ECOMMERCE  (2026-05-21)

## Corpus Check
- 37 files · ~3,896 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 241 nodes · 292 edges · 35 communities (8 shown, 27 thin omitted)
- Extraction: 75% EXTRACTED · 25% INFERRED · 0% AMBIGUOUS · INFERRED: 72 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7976fb8c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]

## God Nodes (most connected - your core abstractions)
1. `Socket Gateway` - 13 edges
2. `ConversationService` - 13 edges
3. `SocketAuthService` - 8 edges
4. `GlobalExceptionHandler` - 7 edges
5. `ConversationController` - 7 edges
6. `SocketEventHandler` - 7 edges
7. `ConversationRepository` - 6 edges
8. `ChatCoreClient` - 5 edges
9. `ConversationParticipantRepository` - 5 edges
10. `SocketConnectListener` - 4 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (35 total, 27 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.11
Nodes (5): SupportConversationIntegrationTest, ConversationMapper, ConversationParticipantRepository, ConversationRepository, ConversationService

### Community 1 - "Community 1"
Cohesion: 0.12
Nodes (6): MessageServiceIntegrationTest, DisconnectListener, SocketAuthService, InfrastructureSocketHandler, SocketDisconnectListener, SocketClientHelper

### Community 2 - "Community 2"
Cohesion: 0.11
Nodes (5): ChatCoreClient, ConversationSocketHandler, MessageSocketHandler, ChatRoomNames, SocketAckHelper

### Community 3 - "Community 3"
Cohesion: 0.14
Nodes (4): ApiResponse, PageResponse, GlobalExceptionHandler, MessageController

### Community 4 - "Community 4"
Cohesion: 0.11
Nodes (5): MessageMapper, MessageRepository, MessageService, MessageSocketPublisher, PageableFactory

### Community 5 - "Community 5"
Cohesion: 0.13
Nodes (18): Architecture, code:txt (socket/                    # Transport infra), code:javascript (const socket = io("http://localhost:9093", {), code:bash (./mvnw spring-boot:run -Dspring-boot.run.profiles=local), code:bash (./mvnw spring-boot:run), Database, Dev connect (auth disabled), Events (+10 more)

### Community 6 - "Community 6"
Cohesion: 0.15
Nodes (5): BadRequestException, ConflictException, ForbiddenException, ResourceNotFoundException, RuntimeException

## Knowledge Gaps
- **20 isolated node(s):** `SocketProperties`, `Auth`, `PageMeta`, `SendMessageRequest`, `MessageResponse` (+15 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **27 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ConversationController` connect `Community 7` to `Community 1`?**
  _High betweenness centrality (0.020) - this node is a cross-community bridge._
- **What connects `SocketProperties`, `Auth`, `PageMeta` to the rest of the system?**
  _20 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.11 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.11 - nodes in this community are weakly interconnected._
- **Should `Community 3` be split into smaller, more focused modules?**
  _Cohesion score 0.14 - nodes in this community are weakly interconnected._
- **Should `Community 4` be split into smaller, more focused modules?**
  _Cohesion score 0.11 - nodes in this community are weakly interconnected._