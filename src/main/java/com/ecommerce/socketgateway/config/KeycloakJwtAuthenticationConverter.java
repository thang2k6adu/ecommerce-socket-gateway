package com.ecommerce.socketgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@Configuration
public class KeycloakJwtAuthenticationConverter {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter(
			@Value("${app.security.keycloak-client-id:ecommerce-backend}") String keycloakClientId) {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Collection<GrantedAuthority> authorities = new HashSet<>();

			Map<String, Object> realmAccess = jwt.getClaim("realm_access");
			if (realmAccess != null && realmAccess.get("roles") instanceof List<?> realmRoles) {
				realmRoles.stream()
						.map(String::valueOf)
						.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
			}

			Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
			if (resourceAccess != null && resourceAccess.get(keycloakClientId) instanceof Map<?, ?> clientAccess) {
				Object roles = clientAccess.get("roles");
				if (roles instanceof List<?> clientRoles) {
					clientRoles.stream()
							.map(String::valueOf)
							.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
				}
			}

			return authorities;
		});
		return converter;
	}

}
