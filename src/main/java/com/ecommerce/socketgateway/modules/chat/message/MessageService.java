package com.ecommerce.socketgateway.modules.chat.message;

import com.ecommerce.socketgateway.common.api.PageResponse;
import com.ecommerce.socketgateway.common.exception.ResourceNotFoundException;
import com.ecommerce.socketgateway.common.pagination.PageParams;
import com.ecommerce.socketgateway.common.pagination.PageResponses;
import com.ecommerce.socketgateway.common.pagination.PageableFactory;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationRepository;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationService;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationEntity;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.modules.chat.message.entity.MessageEntity;
import com.ecommerce.socketgateway.modules.chat.realtime.ChatRealtimePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

	private static final Set<String> MESSAGE_SORT_FIELDS = Set.of("createdAt");

	private final MessageRepository messageRepository;
	private final ConversationRepository conversationRepository;
	private final ConversationService conversationService;
	private final MessageMapper messageMapper;
	private final ChatRealtimePublisher chatRealtimePublisher;

	public MessageResponse sendMessage(String senderId, SendMessageRequest request) {
		Long conversationId = request.getConversationId();
		conversationService.assertParticipant(conversationId, senderId);

		ConversationEntity conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

		String content = request.getContent().trim();
		MessageEntity message = messageRepository.saveAndFlush(MessageEntity.builder()
				.conversation(conversation)
				.senderId(senderId)
				.content(content)
				.clientMessageId(StringUtils.hasText(request.getClientMessageId())
						? request.getClientMessageId().trim()
						: null)
				.build());

		conversation.setLastMessagePreview(truncatePreview(content));
		conversation.setLastMessageAt(message.getCreatedAt() != null ? message.getCreatedAt() : Instant.now());

		MessageResponse response = messageMapper.toResponse(message);
		List<String> participantIds = conversationService.getParticipantUserIds(conversationId);
		chatRealtimePublisher.publishMessageCreated(response, participantIds);

		log.info("[CHAT] Message saved id={}, conversationId={}, senderId={}",
				message.getId(), conversationId, senderId);
		return response;
	}

	@Transactional(readOnly = true)
	public PageResponse<MessageResponse> listMessages(Long conversationId, String userId, PageParams pageParams) {
		conversationService.assertParticipant(conversationId, userId);

		Pageable pageable = PageableFactory.sorted(
				pageParams.getPage(),
				pageParams.getSize(),
				pageParams.getSortBy(),
				pageParams.getSortDirection(),
				MESSAGE_SORT_FIELDS);

		Page<MessageEntity> page = messageRepository.findByConversation_IdOrderByCreatedAtDesc(conversationId, pageable);

		return PageResponses.map(page, messageMapper::toResponse);
	}

	private String truncatePreview(String content) {
		if (content.length() <= 500) {
			return content;
		}
		return content.substring(0, 497) + "...";
	}

}
