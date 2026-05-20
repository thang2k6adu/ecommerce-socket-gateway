package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipantEntity, Long> {

	List<ConversationParticipantEntity> findByConversationId(Long conversationId);

	Optional<ConversationParticipantEntity> findByConversationIdAndUserId(Long conversationId, String userId);

	boolean existsByConversationIdAndUserId(Long conversationId, String userId);

	long countByConversationId(Long conversationId);

}
