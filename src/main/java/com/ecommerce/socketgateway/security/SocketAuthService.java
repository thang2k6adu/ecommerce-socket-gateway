package com.ecommerce.socketgateway.security;

import com.corundumstudio.socketio.HandshakeData;
import com.ecommerce.socketgateway.config.SocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocketAuthService {

	private static final String BEARER_PREFIX = "Bearer ";

	private final SocketProperties socketProperties;
	private final JwtDecoder jwtDecoder;

	public Optional<String> resolveUserId(HandshakeData handshake) {
		if (!socketProperties.getAuth().isEnabled()) {
			return resolveDevUserId(handshake);
		}
		return resolveJwtUserId(handshake);
	}

	private Optional<String> resolveDevUserId(HandshakeData handshake) {
		String userId = firstNonBlank(
				handshake.getSingleUrlParam("userId"),
				handshake.getHttpHeaders().get("X-User-Id")
		);
		if (!StringUtils.hasText(userId)) {
			log.warn("[SOCKET-AUTH] Dev mode: missing userId query param or X-User-Id header");
			return Optional.empty();
		}
		return Optional.of(userId.trim());
	}

	private Optional<String> resolveJwtUserId(HandshakeData handshake) {
		String token = extractToken(handshake);
		if (!StringUtils.hasText(token)) {
			log.warn("[SOCKET-AUTH] JWT mode: missing token in auth or Authorization header");
			return Optional.empty();
		}

		try {
			Jwt jwt = jwtDecoder.decode(token.trim());
			String userId = firstNonBlank(jwt.getSubject(), jwt.getClaimAsString("preferred_username"));
			if (!StringUtils.hasText(userId)) {
				log.warn("[SOCKET-AUTH] JWT mode: token has no subject or preferred_username");
				return Optional.empty();
			}
			return Optional.of(userId.trim());
		} catch (JwtException ex) {
			log.warn("[SOCKET-AUTH] JWT validation failed: {}", ex.getMessage());
			return Optional.empty();
		}
	}

	private String extractToken(HandshakeData handshake) {
		String authToken = handshake.getSingleUrlParam("token");
		if (StringUtils.hasText(authToken)) {
			return authToken;
		}

		List<String> authHeaders = handshake.getHttpHeaders().getAll("Authorization");
		if (authHeaders == null || authHeaders.isEmpty()) {
			return null;
		}

		for (String header : authHeaders) {
			if (header != null && header.startsWith(BEARER_PREFIX)) {
				return header.substring(BEARER_PREFIX.length()).trim();
			}
		}
		return null;
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (StringUtils.hasText(value)) {
				return value;
			}
		}
		return null;
	}

}
