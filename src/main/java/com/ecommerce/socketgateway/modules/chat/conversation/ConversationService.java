package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.common.exception.BadRequestException;
import com.ecommerce.socketgateway.common.exception.ConflictException;
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

	/**
	 * Customer-only: one SUPPORT thread per JWT subject.
	 */
	public ConversationResponse createOrGetSupportConversation(String customerUserId) {
		return conversationRepository
				.findByTypeAndSupportCustomerUserId(ConversationType.SUPPORT, customerUserId)
				.map(conversation -> toResponse(conversation.getId()))
				.orElseGet(() -> createSupportConversation(customerUserId));
	}

	@Transactional(readOnly = true)
	public List<ConversationResponse> listUnclaimedSupportQueue() {
		return conversationRepository.findUnclaimedSupportConversations(ConversationType.SUPPORT).stream()
				.map(conversation -> toResponse(conversation.getId()))
				.toList();
	}

	public ConversationResponse claimSupportConversation(Long conversationId, String adminUserId) {
		ConversationEntity conversation = conversationRepository.findByIdForUpdate(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

		if (conversation.getType() != ConversationType.SUPPORT) {
			throw new BadRequestException("Only support conversations can be claimed");
		}

		long participantCount = participantRepository.countByConversationId(conversationId);
		if (participantCount != 1) {
			throw new ConflictException("Conversation already claimed");
		}

		List<ConversationParticipantEntity> participants = participantRepository.findByConversationId(conversationId);
		if (participants.size() != 1) {
			throw new ConflictException("Conversation already claimed");
		}

		ConversationParticipantEntity sole = participants.get(0);
		String customerId = conversation.getSupportCustomerUserId();
		if (customerId == null || !customerId.equals(sole.getUserId())) {
			throw new BadRequestException("Support conversation is missing customer participant");
		}
		if (adminUserId.equals(customerId)) {
			throw new ForbiddenException("Cannot claim a support conversation as the same user as the customer");
		}

		participantRepository.save(ConversationParticipantEntity.builder()
				.conversation(conversation)
				.userId(adminUserId)
				.build());

		log.info("[CHAT] Support conversation id={} claimed by adminId={}", conversationId, adminUserId);
		return toResponse(conversationId);
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
						.supportCustomerUserId(null)
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

	private ConversationResponse createSupportConversation(String customerUserId) {
		ConversationEntity conversation = conversationRepository.save(
				ConversationEntity.builder()
						.type(ConversationType.SUPPORT)
						.supportCustomerUserId(customerUserId)
						.build());

		participantRepository.save(ConversationParticipantEntity.builder()
				.conversation(conversation)
				.userId(customerUserId)
				.build());

		log.info("[CHAT] Created support conversation id={} for customerId={}", conversation.getId(), customerUserId);
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
