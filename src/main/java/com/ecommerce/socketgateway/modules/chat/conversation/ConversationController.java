package com.ecommerce.socketgateway.modules.chat.conversation;

import com.ecommerce.socketgateway.common.api.ApiResponse;
import com.ecommerce.socketgateway.common.exception.ForbiddenException;
import com.ecommerce.socketgateway.common.security.ChatRoleHelper;
import com.ecommerce.socketgateway.common.security.ChatUserContext;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.request.CreateDirectConversationRequest;
import com.ecommerce.socketgateway.modules.chat.conversation.dto.response.ConversationResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/chat/conversations")
@RequiredArgsConstructor
public class ConversationController {

	private final ConversationService conversationService;
	private final ChatUserContext chatUserContext;

	@PostMapping("/support")
	public ResponseEntity<ApiResponse<ConversationResponse>> createOrGetSupport(Authentication authentication) {
		if (ChatRoleHelper.isAdmin(authentication)) {
			throw new ForbiddenException("Admins cannot use the customer support conversation endpoint");
		}
		String userId = chatUserContext.requireUserId(authentication);
		ConversationResponse response = conversationService.createOrGetSupportConversation(userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
	}

	@GetMapping("/support/queue")
	public ResponseEntity<ApiResponse<List<ConversationResponse>>> supportQueue(Authentication authentication) {
		if (!ChatRoleHelper.isAdmin(authentication)) {
			throw new ForbiddenException("Admin role required");
		}
		chatUserContext.requireUserId(authentication);
		return ResponseEntity.ok(ApiResponse.success(conversationService.listUnclaimedSupportQueue()));
	}

	@PostMapping("/{conversationId}/claim")
	public ResponseEntity<ApiResponse<ConversationResponse>> claimSupport(
			@PathVariable Long conversationId,
			Authentication authentication) {
		if (!ChatRoleHelper.isAdmin(authentication)) {
			throw new ForbiddenException("Admin role required");
		}
		String adminId = chatUserContext.requireUserId(authentication);
		ConversationResponse response = conversationService.claimSupportConversation(conversationId, adminId);
		return ResponseEntity.ok(ApiResponse.success(response));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ConversationResponse>> createDirect(
			@Valid @RequestBody CreateDirectConversationRequest request,
			Authentication authentication) {
		String userId = chatUserContext.requireUserId(authentication);
		ConversationResponse response = conversationService.createOrGetDirectConversation(userId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ConversationResponse>>> list(Authentication authentication) {
		String userId = chatUserContext.requireUserId(authentication);
		return ResponseEntity.ok(ApiResponse.success(conversationService.listConversations(userId)));
	}

	@GetMapping("/{conversationId}")
	public ResponseEntity<ApiResponse<ConversationResponse>> get(
			@PathVariable Long conversationId,
			Authentication authentication) {
		String userId = chatUserContext.requireUserId(authentication);
		return ResponseEntity.ok(ApiResponse.success(
				conversationService.getConversation(conversationId, userId)));
	}

}
