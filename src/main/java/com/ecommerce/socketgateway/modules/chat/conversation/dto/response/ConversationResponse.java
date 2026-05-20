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
	/** For SUPPORT: customer user id; null for DIRECT. */
	String supportCustomerUserId;
	/** For SUPPORT: true when an admin has joined (second participant). */
	boolean claimed;
	String lastMessagePreview;
	Instant lastMessageAt;
	Instant createdAt;
	Instant updatedAt;
	List<String> participantUserIds;

}
