package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.ecommerce.socketgateway.socket.support.SocketAckHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InfrastructureSocketHandler {

	private final SocketAckHelper ackHelper;

	public void onPing(SocketIOClient client, String data, AckRequest ack) {
		log.debug("[SOCKET] ping from userId={}, data={}", client.get(SocketConnectListener.USER_ID_ATTR), data);
		client.sendEvent("pong", Map.of("ok", true, "echo", data));
		ackHelper.success(ack, Map.of("ok", true));
	}

}
