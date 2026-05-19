package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketEventHandler {

	public static final String CONVERSATION_ROOM_PREFIX = "conversation:";

	private final SocketIOServer socketIOServer;

	public void onPing(SocketIOClient client, String data, AckRequest ackRequest) {
		log.debug("[SOCKET] ping from userId={}, data={}", client.get(SocketConnectListener.USER_ID_ATTR), data);
		client.sendEvent("pong", Map.of("ok", true, "echo", data));
		if (ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of("ok", true));
		}
	}

	public void onJoinConversation(SocketIOClient client, Long conversationId, AckRequest ackRequest) {
		String userId = client.get(SocketConnectListener.USER_ID_ATTR);
		if (!StringUtils.hasText(userId)) {
			reject(ackRequest, "Not authenticated");
			return;
		}
		if (conversationId == null) {
			reject(ackRequest, "conversationId is required");
			return;
		}

		String room = CONVERSATION_ROOM_PREFIX + conversationId;
		client.joinRoom(room);
		log.info("[SOCKET] userId={} joined room={}", userId, room);

		client.sendEvent("conversation:joined", Map.of(
				"conversationId", conversationId,
				"room", room
		));
		if (ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of("joined", true, "room", room));
		}
	}

	public void onLeaveConversation(SocketIOClient client, Long conversationId, AckRequest ackRequest) {
		if (conversationId == null) {
			reject(ackRequest, "conversationId is required");
			return;
		}

		String room = CONVERSATION_ROOM_PREFIX + conversationId;
		client.leaveRoom(room);
		log.info("[SOCKET] userId={} left room={}",
				client.get(SocketConnectListener.USER_ID_ATTR), room);

		client.sendEvent("conversation:left", Map.of(
				"conversationId", conversationId,
				"room", room
		));
		if (ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of("left", true, "room", room));
		}
	}

	/**
	 * Dev helper: broadcast to a user room without Kafka.
	 */
	public void emitToUser(String userId, String event, Object payload) {
		socketIOServer.getRoomOperations(SocketConnectListener.USER_ROOM_PREFIX + userId)
				.sendEvent(event, payload);
	}

	private void reject(AckRequest ackRequest, String message) {
		if (ackRequest != null && ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of("error", message));
		}
	}

}
