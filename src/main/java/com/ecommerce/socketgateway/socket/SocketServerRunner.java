package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.ecommerce.socketgateway.config.SocketProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketServerRunner {

	private final SocketIOServer socketIOServer;
	private final SocketProperties socketProperties;
	private final SocketConnectListener socketConnectListener;
	private final SocketDisconnectListener socketDisconnectListener;
	private final SocketEventHandler socketEventHandler;

	@PostConstruct
	public void start() {
		socketIOServer.addConnectListener(socketConnectListener);
		socketIOServer.addDisconnectListener(socketDisconnectListener);

		socketIOServer.addEventListener("ping", String.class, socketEventHandler::onPing);
		socketIOServer.addEventListener("conversation:join", Long.class, socketEventHandler::onJoinConversation);
		socketIOServer.addEventListener("conversation:leave", Long.class, socketEventHandler::onLeaveConversation);

		socketIOServer.start();
		log.info("[SOCKET] Server started at {}:{} (auth.enabled={})",
				socketProperties.getHost(),
				socketProperties.getPort(),
				socketProperties.getAuth().isEnabled());
	}

	@PreDestroy
	public void stop() {
		if (socketIOServer != null) {
			socketIOServer.stop();
			log.info("[SOCKET] Server stopped");
		}
	}

}
