package com.ecommerce.socketgateway.socket.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MessageNewPayload {

	String messageId;
	Long conversationId;
	String senderId;
	String recipientId;
	String content;
	String clientMessageId;
	String createdAt;

}
