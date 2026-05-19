package com.ecommerce.socketgateway.modules.chat.conversation;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.ecommerce.socketgateway.socket.ChatRoomNames;
import com.ecommerce.socketgateway.socket.SocketConnectListener;
import com.ecommerce.socketgateway.socket.support.SocketAckHelper;
import com.ecommerce.socketgateway.socket.support.SocketClientHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConversationSocketHandler {

	private final ConversationService conversationService;
	private final SocketClientHelper clientHelper;
	private final SocketAckHelper ackHelper;

	public void onJoin(SocketIOClient client, Long conversationId, AckRequest ack) {
		try {
			String userId = clientHelper.requireUserId(client);
			if (conversationId == null) {
				ackHelper.reject(ack, "conversationId is required");
				return;
			}

			conversationService.joinConversation(conversationId, userId);

			String room = ChatRoomNames.conversationRoom(conversationId);
			client.joinRoom(room);
			log.info("[SOCKET] userId={} joined room={}", userId, room);

			client.sendEvent("conversation:joined", Map.of(
					"conversationId", conversationId,
					"room", room
			));
			ackHelper.success(ack, Map.of("joined", true, "room", room));
		} catch (RuntimeException ex) {
			ackHelper.reject(ack, ex.getMessage());
		}
	}

	public void onLeave(SocketIOClient client, Long conversationId, AckRequest ack) {
		if (conversationId == null) {
			ackHelper.reject(ack, "conversationId is required");
			return;
		}

		String room = ChatRoomNames.conversationRoom(conversationId);
		client.leaveRoom(room);
		log.info("[SOCKET] userId={} left room={}",
				client.get(SocketConnectListener.USER_ID_ATTR), room);

		client.sendEvent("conversation:left", Map.of(
				"conversationId", conversationId,
				"room", room
		));
		ackHelper.success(ack, Map.of("left", true, "room", room));
	}

}
