package com.ecommerce.socketgateway.integrations.chatcore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "chat-core")
public class ChatCoreProperties {
	private String baseUrl = "http://localhost:8091";
}
