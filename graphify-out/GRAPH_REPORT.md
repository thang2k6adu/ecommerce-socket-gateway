# Graph Report - SOCKET-GATEWAY-ECOMMERCE  (2026-05-19)

## Corpus Check
- 14 files · ~2,731 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 60 nodes · 60 edges · 13 communities (1 shown, 12 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 1 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d032a903`
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

## God Nodes (most connected - your core abstractions)
1. `SocketEventHandler` - 7 edges
2. `SocketAuthService` - 6 edges
3. `Socket Gateway` - 6 edges
4. `SocketConnectListener` - 4 edges
5. `SecurityConfig` - 3 edges
6. `SocketDisconnectListener` - 3 edges
7. `SocketServerRunner` - 3 edges
8. `SocketGatewayApplication` - 2 edges
9. `SocketServerConfig` - 2 edges
10. `SocketGatewayApplicationTests` - 2 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities (13 total, 12 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.17
Nodes (11): code:bash (cp .env.example .env   # optional), code:javascript (import { io } from "socket.io-client";), code:javascript (// User A), code:javascript (io("http://localhost:9093", {), Dev connect (auth disabled), Events, JWT auth (production), Message flow (no DB yet) (+3 more)

## Knowledge Gaps
- **9 isolated node(s):** `SocketProperties`, `Auth`, `MessageSendRequest`, `MessageNewPayload`, `Ports` (+4 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **12 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What connects `SocketProperties`, `Auth`, `MessageSendRequest` to the rest of the system?**
  _9 weakly-connected nodes found - possible documentation gaps or missing edges._