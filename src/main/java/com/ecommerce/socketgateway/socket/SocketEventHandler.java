package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationService;
import com.ecommerce.socketgateway.modules.chat.message.MessageService;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.realtime.ChatRealtimePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketEventHandler {

	private final ConversationService conversationService;
	private final MessageService messageService;

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

		try {
			conversationService.assertParticipant(conversationId, userId);
		} catch (RuntimeException ex) {
			reject(ackRequest, ex.getMessage());
			return;
		}

		String room = ChatRealtimePublisher.CONVERSATION_ROOM_PREFIX + conversationId;
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

		String room = ChatRealtimePublisher.CONVERSATION_ROOM_PREFIX + conversationId;
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

	public void onMessageSend(SocketIOClient client, SendMessageRequest request, AckRequest ackRequest) {
		String senderId = client.get(SocketConnectListener.USER_ID_ATTR);
		if (!StringUtils.hasText(senderId)) {
			reject(ackRequest, "Not authenticated");
			return;
		}
		if (request == null) {
			reject(ackRequest, "Request body is required");
			return;
		}

		try {
			MessageResponse saved = messageService.sendMessage(senderId, request);
			if (ackRequest.isAckRequested()) {
				ackRequest.sendAckData(Map.of(
						"ok", true,
						"messageId", saved.getId().toString(),
						"conversationId", saved.getConversationId(),
						"clientMessageId", saved.getClientMessageId() != null ? saved.getClientMessageId() : "",
						"createdAt", saved.getCreatedAt().toString()
				));
			}
		} catch (RuntimeException ex) {
			log.warn("[SOCKET] message:send failed: {}", ex.getMessage());
			reject(ackRequest, ex.getMessage());
		}
	}

	private void reject(AckRequest ackRequest, String message) {
		if (ackRequest != null && ackRequest.isAckRequested()) {
			ackRequest.sendAckData(Map.of("ok", false, "error", message));
		}
	}

}
