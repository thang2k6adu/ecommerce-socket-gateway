package com.ecommerce.socketgateway.modules.chat.message;

import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.message.entity.MessageEntity;
import com.ecommerce.socketgateway.modules.chat.realtime.MessageRealtimePayload;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

	public MessageResponse toResponse(MessageEntity entity) {
		return MessageResponse.builder()
				.id(entity.getId())
				.conversationId(entity.getConversation().getId())
				.senderId(entity.getSenderId())
				.content(entity.getContent())
				.clientMessageId(entity.getClientMessageId())
				.createdAt(entity.getCreatedAt())
				.build();
	}

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
