package com.ecommerce.socketgateway.modules.chat.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectConversationRequest {

	@NotBlank(message = "otherUserId is required")
	private String otherUserId;

}
