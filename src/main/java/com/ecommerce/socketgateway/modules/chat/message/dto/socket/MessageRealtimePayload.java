package com.ecommerce.socketgateway.modules.chat.message.dto.socket;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MessageRealtimePayload {

	String messageId;
	Long conversationId;
	String senderId;
	String content;
	String clientMessageId;
	String createdAt;

}
