package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ecommerce.socketgateway.socket.dto.MessageNewPayload;
import com.ecommerce.socketgateway.socket.dto.MessageSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketEventHandler {

	public static final String CONVERSATION_ROOM_PREFIX = "conversation:";
	public static final String EVENT_MESSAGE_NEW = "message:new";

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

	public void onMessageSend(SocketIOClient client, MessageSendRequest request, AckRequest ackRequest) {
		String senderId = client.get(SocketConnectListener.USER_ID_ATTR);
		if (!StringUtils.hasText(senderId)) {
			reject(ackRequest, "Not authenticated");
			return;
		}
		if (request == null) {
			reject(ackRequest, "Request body is required");
			return;
		}
		if (request.getConversationId() == null) {
			reject(ackRequest, "conversationId is required");
			return;
		}
		if (!StringUtils.hasText(request.getRecipientId())) {
			reject(ackRequest, "recipientId is required");
			return;
		}
		if (!StringUtils.hasText(request.getContent())) {
			reject(ackRequest, "content is required");
			return;
		}

		String recipientId = request.getRecipientId().trim();
		if (senderId.equals(recipientId)) {
			reject(ackRequest, "Cannot send message to yourself");
			return;
		}

		String messageId = UUID.randomUUID().toString();
		String createdAt = Instant.now().toString();
		MessageNewPayload payload = MessageNewPayload.builder()
				.messageId(messageId)
				.conversationId(request.getConversationId())
				.senderId(senderId)
				.recipientId(recipientId)
				.content(request.getContent().trim())
				.clientMessageId(request.getClientMessageId())
				.createdAt(createdAt)
				.build();

		emitToUser(recipientId, EVENT_MESSAGE_NEW, payload);
		log.info("[SOCKET] message:new conversationId={}, from={}, to={}, messageId={}",
				request.getConversationId(), senderId, recipientId, messageId);

		if (ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of(
					"ok", true,
					"messageId", messageId,
					"conversationId", request.getConversationId(),
					"clientMessageId", request.getClientMessageId() != null ? request.getClientMessageId() : "",
					"createdAt", createdAt
			));
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
			ackRequest.sendAckData(Map.of("ok", false, "error", message));
		}
	}

}
