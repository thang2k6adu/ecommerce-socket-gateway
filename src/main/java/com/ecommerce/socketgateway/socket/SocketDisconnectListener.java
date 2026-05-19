package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketDisconnectListener implements DisconnectListener {

	@Override
	public void onDisconnect(SocketIOClient client) {
		String userId = client.get(SocketConnectListener.USER_ID_ATTR);
		log.info("[SOCKET] Disconnected: sessionId={}, userId={}", client.getSessionId(), userId);
	}

}
