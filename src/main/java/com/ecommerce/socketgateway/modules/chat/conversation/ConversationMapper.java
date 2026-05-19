package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.modules.chat.conversation.dto.response.ConversationResponse;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationEntity;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationParticipantEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConversationMapper {

	public ConversationResponse toResponse(
			ConversationEntity entity,
			List<ConversationParticipantEntity> participants) {
		List<String> participantUserIds = participants.stream()
				.map(ConversationParticipantEntity::getUserId)
				.toList();

		return ConversationResponse.builder()
				.id(entity.getId())
				.type(entity.getType())
				.lastMessagePreview(entity.getLastMessagePreview())
				.lastMessageAt(entity.getLastMessageAt())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.participantUserIds(participantUserIds)
				.build();
	}

}
