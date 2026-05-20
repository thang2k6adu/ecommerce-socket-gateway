package com.ecommerce.socketgateway.integrations.chatcore.dto;

import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InternalSendMessageResult {
	private MessageResponse message;
	private List<String> participantUserIds;
}
