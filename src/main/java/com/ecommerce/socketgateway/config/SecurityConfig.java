package com.ecommerce.socketgateway.config;

import com.ecommerce.socketgateway.common.security.DevUserAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final SocketProperties socketProperties;
	private final DevUserAuthenticationFilter devUserAuthenticationFilter;

	@Bean
	public JwtDecoder jwtDecoder(
			@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri) {
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(devUserAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		if (socketProperties.getAuth().isEnabled()) {
			http.authorizeHttpRequests(auth -> auth
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers("/api/chat/**").authenticated()
					.anyRequest().denyAll())
					.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
		} else {
			http.authorizeHttpRequests(auth -> auth
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers("/api/chat/**").permitAll()
					.anyRequest().denyAll());
		}

		return http.build();
	}

}
