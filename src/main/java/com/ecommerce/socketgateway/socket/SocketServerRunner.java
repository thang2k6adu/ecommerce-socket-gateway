package com.ecommerce.socketgateway.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.ecommerce.socketgateway.config.SocketProperties;
import com.ecommerce.socketgateway.modules.chat.conversation.ConversationSocketHandler;
import com.ecommerce.socketgateway.modules.chat.message.MessageSocketHandler;
import com.ecommerce.socketgateway.modules.chat.message.dto.request.SendMessageRequest;
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
	private final InfrastructureSocketHandler infrastructureSocketHandler;
	private final ConversationSocketHandler conversationSocketHandler;
	private final MessageSocketHandler messageSocketHandler;

	@PostConstruct
	public void start() {
		socketIOServer.addConnectListener(socketConnectListener);
		socketIOServer.addDisconnectListener(socketDisconnectListener);

		socketIOServer.addEventListener("ping", String.class, infrastructureSocketHandler::onPing);
		socketIOServer.addEventListener("conversation:join", Long.class, conversationSocketHandler::onJoin);
		socketIOServer.addEventListener("conversation:leave", Long.class, conversationSocketHandler::onLeave);
		socketIOServer.addEventListener("message:send", SendMessageRequest.class, messageSocketHandler::onSend);

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
