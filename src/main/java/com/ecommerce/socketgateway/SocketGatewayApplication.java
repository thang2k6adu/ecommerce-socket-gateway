package com.ecommerce.socketgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SocketGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocketGatewayApplication.class, args);
	}

}
