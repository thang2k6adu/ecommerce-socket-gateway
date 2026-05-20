package com.ecommerce.socketgateway.modules.chat.message;

import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.message.dto.socket.MessageRealtimePayload;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

	public MessageRealtimePayload toRealtimePayload(MessageResponse response) {
		return MessageRealtimePayload.builder()
				.messageId(response.getId().toString())
				.conversationId(response.getConversationId())
				.senderId(response.getSenderId())
				.content(response.getContent())
				.clientMessageId(response.getClientMessageId())
				.createdAt(response.getCreatedAt() != null ? response.getCreatedAt().toString() : null)
				.build();
	}

}
