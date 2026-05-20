package com.ecommerce.socketgateway.modules.chat;

import com.ecommerce.socketgateway.common.exception.ConflictException;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationService;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.response.ConversationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SupportConversationIntegrationTest {

	@Autowired
	private ConversationService conversationService;

	@Test
	void support_create_queue_claim_secondClaimConflict() {
		ConversationResponse created = conversationService.createOrGetSupportConversation("customer-1");
		assertThat(created.getType().name()).isEqualTo("SUPPORT");
		assertThat(created.getSupportCustomerUserId()).isEqualTo("customer-1");
		assertThat(created.isClaimed()).isFalse();

		ConversationResponse again = conversationService.createOrGetSupportConversation("customer-1");
		assertThat(again.getId()).isEqualTo(created.getId());

		var queue = conversationService.listUnclaimedSupportQueue();
		assertThat(queue).extracting(ConversationResponse::getId).contains(created.getId());

		ConversationResponse claimed = conversationService.claimSupportConversation(created.getId(), "admin-1");
		assertThat(claimed.isClaimed()).isTrue();
		assertThat(claimed.getParticipantUserIds()).containsExactlyInAnyOrder("customer-1", "admin-1");

		assertThat(conversationService.listUnclaimedSupportQueue())
				.noneMatch(c -> c.getId().equals(created.getId()));

		assertThatThrownBy(() -> conversationService.claimSupportConversation(created.getId(), "admin-2"))
				.isInstanceOf(ConflictException.class);
	}

}
