package com.ecommerce.socketgateway.integrations.chatcore;

import com.ecommerce.socketgateway.common.api.ApiResponse;
import com.ecommerce.socketgateway.integrations.chatcore.dto.InternalSendMessageResult;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ChatCoreClient {

	private final RestClient client;

	public ChatCoreClient(ChatCoreProperties properties) {
    String baseUrl = properties.getBaseUrl();

    if (baseUrl == null || baseUrl.isBlank()) {
        throw new IllegalStateException("chat-core baseUrl must not be null or blank");
    }

    this.client = RestClient.builder()
            .baseUrl(baseUrl)
            .build();
}

	public InternalSendMessageResult sendMessage(String senderId, SendMessageRequest payload) {
		ApiResponse<InternalSendMessageResult> response = client.post()
				.uri("/internal/chat/messages")
				.body(Map.of("senderId", senderId, "payload", payload))
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});

		if (response == null || response.getData() == null) {
			throw new IllegalStateException("Empty response from chat-core send message");
		}
		return response.getData();
	}

	public void assertConversationJoinable(Long conversationId, String userId) {
		client.post()
				.uri("/internal/chat/conversations/{id}/join-check", conversationId)
				.body(Map.of("userId", userId))
				.retrieve()
				.body(new ParameterizedTypeReference<ApiResponse<Void>>() {
				});
	}

	public List<String> getParticipants(Long conversationId) {
		ApiResponse<List<String>> response = client.get()
				.uri("/internal/chat/conversations/{id}/participants", conversationId)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});

		if (response == null || response.getData() == null) {
			throw new IllegalStateException("Empty response from chat-core participants");
		}
		return response.getData();
	}
}
