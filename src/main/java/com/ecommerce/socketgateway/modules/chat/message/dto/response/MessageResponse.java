package com.ecommerce.socketgateway.modules.chat.message.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class MessageResponse {

	UUID id;
	Long conversationId;
	String senderId;
	String content;
	String clientMessageId;
	Instant createdAt;

}
