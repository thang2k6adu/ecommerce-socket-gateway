package com.ecommerce.socketgateway.socket;

public final class ChatRoomNames {

	public static final String USER_PREFIX = "user:";
	public static final String CONVERSATION_PREFIX = "conversation:";

	private ChatRoomNames() {
	}

	public static String userRoom(String userId) {
		return USER_PREFIX + userId;
	}

	public static String conversationRoom(Long conversationId) {
		return CONVERSATION_PREFIX + conversationId;
	}

}
