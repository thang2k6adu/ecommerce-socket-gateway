package com.ecommerce.socketgateway.modules.chat.realtime;

import com.corundumstudio.socketio.SocketIOServer;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.message.MessageMapper;
import com.ecommerce.socketgateway.socket.SocketConnectListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Transport layer: fan-out persisted chat events to Socket.IO rooms.
 * Keeps {@code modules/chat} domain free of netty-socketio types in services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRealtimePublisher {

	public static final String EVENT_MESSAGE_NEW = "message:new";
	public static final String EVENT_MESSAGE_CREATED = "message:created";
	public static final String CONVERSATION_ROOM_PREFIX = "conversation:";

	private final SocketIOServer socketIOServer;
	private final MessageMapper messageMapper;

	public void publishMessageCreated(MessageResponse message, List<String> participantUserIds) {
		MessageRealtimePayload payload = messageMapper.toRealtimePayload(message);

		for (String participantId : participantUserIds) {
			socketIOServer
					.getRoomOperations(SocketConnectListener.USER_ROOM_PREFIX + participantId)
					.sendEvent(EVENT_MESSAGE_NEW, payload);
		}

		socketIOServer
				.getRoomOperations(CONVERSATION_ROOM_PREFIX + message.getConversationId())
				.sendEvent(EVENT_MESSAGE_CREATED, payload);

		log.debug("[REALTIME] message published conversationId={}, messageId={}",
				message.getConversationId(), message.getId());
	}

}
