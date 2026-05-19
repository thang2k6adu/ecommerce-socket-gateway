package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.ecommerce.socketgateway.security.SocketAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketConnectListener implements ConnectListener {

	public static final String USER_ID_ATTR = "userId";

	private final SocketAuthService socketAuthService;

	@Override
	public void onConnect(SocketIOClient client) {
		socketAuthService.resolveUserId(client.getHandshakeData())
				.ifPresentOrElse(
						userId -> registerClient(client, userId),
						() -> {
							log.warn("[SOCKET] Rejected connection: sessionId={}", client.getSessionId());
							client.disconnect();
						}
				);
	}

	private void registerClient(SocketIOClient client, String userId) {
		client.set(USER_ID_ATTR, userId);
		String userRoom = ChatRoomNames.userRoom(userId);
		client.joinRoom(userRoom);

		log.info("[SOCKET] Connected: sessionId={}, userId={}, room={}",
				client.getSessionId(), userId, userRoom);

		client.sendEvent("connected", Map.of(
				"userId", userId,
				"sessionId", client.getSessionId().toString(),
				"room", userRoom
		));
	}

}
