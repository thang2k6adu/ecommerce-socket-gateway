package com.ecommerce.socketgateway.modules.chat.message;

import com.ecommerce.socketgateway.common.api.ApiResponse;
import com.ecommerce.socketgateway.common.api.PageResponse;
import com.ecommerce.socketgateway.common.pagination.PageParams;
import com.ecommerce.socketgateway.common.security.ChatUserContext;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
import com.ecommerce.socketgateway.modules.chat.message.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class MessageController {

	private final MessageService messageService;
	private final ChatUserContext chatUserContext;

	@PostMapping("/messages")
	public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
			@Valid @RequestBody SendMessageRequest request,
			Authentication authentication) {
		String userId = chatUserContext.requireUserId(authentication);
		MessageResponse response = messageService.sendMessage(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
	}

	@GetMapping("/conversations/{conversationId}/messages")
	public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> listMessages(
			@PathVariable Long conversationId,
			PageParams pageParams,
			Authentication authentication) {
		String userId = chatUserContext.requireUserId(authentication);
		PageResponse<MessageResponse> page = messageService.listMessages(conversationId, userId, pageParams);
		return ResponseEntity.ok(ApiResponse.successWithMeta(page, page.toMeta()));
	}

}
