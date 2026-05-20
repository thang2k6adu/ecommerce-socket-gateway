package com.ecommerce.socketgateway.modules.chat.message;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.ecommerce.socketgateway.integrations.chatcore.ChatCoreClient;
import com.ecommerce.socketgateway.integrations.chatcore.dto.InternalSendMessageResult;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import com.ecommerce.socketgateway.socket.support.SocketAckHelper;
import com.ecommerce.socketgateway.socket.support.SocketClientHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageSocketHandler {

	private final ChatCoreClient chatCoreClient;
	private final MessageSocketPublisher messageSocketPublisher;
	private final SocketClientHelper clientHelper;
	private final SocketAckHelper ackHelper;

	public void onSend(SocketIOClient client, SendMessageRequest request, AckRequest ack) {
		try {
			String senderId = clientHelper.requireUserId(client);
			if (request == null) {
				ackHelper.reject(ack, "Request body is required");
				return;
			}

			InternalSendMessageResult result = chatCoreClient.sendMessage(senderId, request);
			MessageResponse saved = result.getMessage();
			messageSocketPublisher.publishMessageCreated(saved, result.getParticipantUserIds());
			ackHelper.success(ack, Map.of(
					"ok", true,
					"messageId", saved.getId().toString(),
					"conversationId", saved.getConversationId(),
					"clientMessageId", saved.getClientMessageId() != null ? saved.getClientMessageId() : "",
					"createdAt", saved.getCreatedAt() != null ? saved.getCreatedAt().toString() : ""
			));
		} catch (RuntimeException ex) {
			log.warn("[SOCKET] message:send failed: {}", ex.getMessage());
			ackHelper.reject(ack, ex.getMessage());
		}
	}

}
