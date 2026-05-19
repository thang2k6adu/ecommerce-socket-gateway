package com.ecommerce.socketgateway.socket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendRequest {

	private Long conversationId;

	/** Recipient user id (until chat service resolves participants). */
	private String recipientId;

	private String content;

	/** Optional client-side id for optimistic UI / ACK correlation. */
	private String clientMessageId;

}
