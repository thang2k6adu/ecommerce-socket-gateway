package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.common.exception.BadRequestException;
import com.ecommerce.socketgateway.common.exception.ForbiddenException;
import com.ecommerce.socketgateway.common.exception.ResourceNotFoundException;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.request.CreateDirectConversationRequest;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.response.ConversationResponse;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationEntity;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationParticipantEntity;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConversationService {

	private final ConversationRepository conversationRepository;
	private final ConversationParticipantRepository participantRepository;
	private final ConversationMapper conversationMapper;

	public ConversationResponse createOrGetDirectConversation(String currentUserId, CreateDirectConversationRequest request) {
		String otherUserId = request.getOtherUserId().trim();
		if (currentUserId.equals(otherUserId)) {
			throw new BadRequestException("Cannot create conversation with yourself");
		}

		return conversationRepository
				.findDirectConversation(ConversationType.DIRECT, currentUserId, otherUserId)
				.map(conversation -> toResponse(conversation.getId()))
				.orElseGet(() -> createDirectConversation(currentUserId, otherUserId));
	}

	public List<ConversationResponse> listConversations(String currentUserId) {
		return conversationRepository.findAllForUser(currentUserId).stream()
				.map(conversation -> toResponse(conversation.getId()))
				.toList();
	}

	public ConversationResponse getConversation(Long conversationId, String currentUserId) {
		assertParticipant(conversationId, currentUserId);
		return toResponse(conversationId);
	}

	public void joinConversation(Long conversationId, String userId) {
		assertParticipant(conversationId, userId);
	}

	public void assertParticipant(Long conversationId, String userId) {
		if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
			throw new ForbiddenException("You are not a participant of conversation " + conversationId);
		}
	}

	public List<String> getParticipantUserIds(Long conversationId) {
		return participantRepository.findByConversationId(conversationId).stream()
				.map(ConversationParticipantEntity::getUserId)
				.toList();
	}

	private ConversationResponse createDirectConversation(String userA, String userB) {
		ConversationEntity conversation = conversationRepository.save(
				ConversationEntity.builder()
						.type(ConversationType.DIRECT)
						.build());

		participantRepository.save(ConversationParticipantEntity.builder()
				.conversation(conversation)
				.userId(userA)
				.build());
		participantRepository.save(ConversationParticipantEntity.builder()
				.conversation(conversation)
				.userId(userB)
				.build());

		log.info("[CHAT] Created direct conversation id={} between {} and {}", conversation.getId(), userA, userB);
		return toResponse(conversation.getId());
	}

	private ConversationResponse toResponse(Long conversationId) {
		ConversationEntity conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
		List<ConversationParticipantEntity> participants =
				participantRepository.findByConversationId(conversationId);
		return conversationMapper.toResponse(conversation, participants);
	}

}
