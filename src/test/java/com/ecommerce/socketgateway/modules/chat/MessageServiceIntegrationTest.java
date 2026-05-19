package com.ecommerce.socketgateway.modules.chat;

import com.ecommerce.socketgateway.common.pagination.PageParams;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationService;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.request.CreateDirectConversationRequest;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.response.ConversationResponse;
import com.ecommerce.socketgateway.modules.chat.message.MessageService;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MessageServiceIntegrationTest {

	@Autowired
	private ConversationService conversationService;

	@Autowired
	private MessageService messageService;

	@Test
	void sendMessage_persistsAndReturnsResponse() {
		ConversationResponse conversation = conversationService.createOrGetDirectConversation(
				"user-a",
				new CreateDirectConversationRequest("user-b"));

		MessageResponse saved = messageService.sendMessage("user-a", new SendMessageRequest(
				conversation.getId(),
				"Hello from test",
				"client-1"));

		assertThat(saved.getContent()).isEqualTo("Hello from test");
		assertThat(saved.getSenderId()).isEqualTo("user-a");
		assertThat(saved.getConversationId()).isEqualTo(conversation.getId());

		var page = messageService.listMessages(conversation.getId(), "user-b", new PageParams());
		assertThat(page.getContent()).hasSize(1);
		assertThat(page.getContent().get(0).getContent()).isEqualTo("Hello from test");
	}

}
