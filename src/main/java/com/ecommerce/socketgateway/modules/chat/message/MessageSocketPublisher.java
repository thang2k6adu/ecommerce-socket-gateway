package com.ecommerce.socketgateway.modules.chat.message;

import com.corundumstudio.socketio.SocketIOServer;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.message.dto.socket.MessageRealtimePayload;
import com.ecommerce.socketgateway.socket.ChatRoomNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSocketPublisher {

	public static final String EVENT_MESSAGE_NEW = "message:new";
	public static final String EVENT_MESSAGE_CREATED = "message:created";

	private final SocketIOServer socketIOServer;
	private final MessageMapper messageMapper;

	public void publishMessageCreated(MessageResponse message, List<String> participantUserIds) {
		MessageRealtimePayload payload = messageMapper.toRealtimePayload(message);

		for (String participantId : participantUserIds) {
			socketIOServer
					.getRoomOperations(ChatRoomNames.userRoom(participantId))
					.sendEvent(EVENT_MESSAGE_NEW, payload);
		}

		socketIOServer
				.getRoomOperations(ChatRoomNames.conversationRoom(message.getConversationId()))
				.sendEvent(EVENT_MESSAGE_CREATED, payload);

		log.debug("[SOCKET] message published conversationId={}, messageId={}",
				message.getConversationId(), message.getId());
	}

}
