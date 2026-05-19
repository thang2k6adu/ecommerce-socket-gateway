package com.ecommerce.socketgateway.modules.chat.conversation.dto.response;

import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationType;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class ConversationResponse {

	Long id;
	ConversationType type;
	String lastMessagePreview;
	Instant lastMessageAt;
	Instant createdAt;
	Instant updatedAt;
	List<String> participantUserIds;

}
