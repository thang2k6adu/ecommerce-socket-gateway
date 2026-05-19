package com.ecommerce.socketgateway.socket.support;

import com.corundumstudio.socketio.AckRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SocketAckHelper {

	public void reject(AckRequest ack, String message) {
		if (ack != null && ack.isAckRequested()) {
			ack.sendAckData(Map.of("ok", false, "error", message));
		}
	}

	public void success(AckRequest ack, Map<String, Object> data) {
		if (ack != null && ack.isAckRequested()) {
			ack.sendAckData(data);
		}
	}

}
