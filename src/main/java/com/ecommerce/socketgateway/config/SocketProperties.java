package com.ecommerce.socketgateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "socket")
public class SocketProperties {

	private String host = "0.0.0.0";

	private int port = 9093;

	private String allowedOrigin = "*";

	private final Auth auth = new Auth();

	@Getter
	@Setter
	public static class Auth {
		private boolean enabled = false;
	}

}
