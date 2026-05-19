package com.ecommerce.socketgateway.socket.support;

import com.corundumstudio.socketio.SocketIOClient;
import com.ecommerce.socketgateway.socket.SocketConnectListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SocketClientHelper {

	public String requireUserId(SocketIOClient client) {
		String userId = client.get(SocketConnectListener.USER_ID_ATTR);
		if (!StringUtils.hasText(userId)) {
			throw new IllegalStateException("Not authenticated");
		}
		return userId;
	}

}
