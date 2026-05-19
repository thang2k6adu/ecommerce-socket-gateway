package com.ecommerce.socketgateway.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketServerConfig {

	@Bean
	public SocketIOServer socketIOServer(SocketProperties socketProperties) {
		// Tránh trùng tên class với Configuration, socketIO cũng có Configuration
		com.corundumstudio.socketio.Configuration configuration =
				new com.corundumstudio.socketio.Configuration();
		configuration.setHostname(socketProperties.getHost());
		configuration.setPort(socketProperties.getPort());
		configuration.setOrigin(socketProperties.getAllowedOrigin());
		return new SocketIOServer(configuration);
	}

}
