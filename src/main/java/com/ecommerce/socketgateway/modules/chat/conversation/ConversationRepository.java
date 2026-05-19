package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationEntity;
import com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

	@Query("""
			SELECT c FROM ConversationEntity c
			WHERE c.type = :type
			AND EXISTS (
				SELECT 1 FROM ConversationParticipantEntity p
				WHERE p.conversation = c AND p.userId = :userA
			)
			AND EXISTS (
				SELECT 1 FROM ConversationParticipantEntity p
				WHERE p.conversation = c AND p.userId = :userB
			)
			AND (
				SELECT COUNT(p2) FROM ConversationParticipantEntity p2
				WHERE p2.conversation = c
			) = 2
			""")
	Optional<ConversationEntity> findDirectConversation(
			@Param("type") ConversationType type,
			@Param("userA") String userA,
			@Param("userB") String userB);

	@Query("""
			SELECT DISTINCT c FROM ConversationEntity c
			JOIN ConversationParticipantEntity p ON p.conversation = c
			WHERE p.userId = :userId
			ORDER BY c.lastMessageAt DESC NULLS LAST, c.updatedAt DESC
			""")
	List<ConversationEntity> findAllForUser(@Param("userId") String userId);

}
