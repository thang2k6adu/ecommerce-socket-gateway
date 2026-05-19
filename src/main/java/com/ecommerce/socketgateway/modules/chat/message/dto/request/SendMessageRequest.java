package com.ecommerce.socketgateway.modules.chat.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

	@NotNull(message = "conversationId is required")
	private Long conversationId;

	@NotBlank(message = "content is required")
	private String content;

	private String clientMessageId;

}
