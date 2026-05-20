package com.ecommerce.socketgateway.security;

import com.corundumstudio.socketio.HandshakeData;
import com.ecommerce.socketgateway.common.security.ChatUserContext;
import com.ecommerce.socketgateway.config.SocketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
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
				firstHeader(handshake, ChatUserContext.DEV_USER_HEADER)
		);
		if (!StringUtils.hasText(userId)) {
			log.warn("[SOCKET-AUTH] Local mode: missing userId query param or {} header",
					ChatUserContext.DEV_USER_HEADER);
			return Optional.empty();
		}
		return Optional.of(userId.trim());
	}

	private Optional<String> resolveJwtUserId(HandshakeData handshake) {
		String token = extractToken(handshake);
		if (!StringUtils.hasText(token)) {
			log.warn("[SOCKET-AUTH] Missing JWT in auth payload or Authorization header");
			return Optional.empty();
		}

		try {
			Jwt jwt = jwtDecoder.decode(token.trim());
			String userId = jwt.getSubject();
			if (!StringUtils.hasText(userId)) {
				log.warn("[SOCKET-AUTH] JWT has no subject claim");
				return Optional.empty();
			}
			return Optional.of(userId.trim());
		} catch (JwtException ex) {
			log.warn("[SOCKET-AUTH] JWT validation failed: {}", ex.getMessage());
			return Optional.empty();
		}
	}

	private String extractToken(HandshakeData handshake) {
		String fromAuthPayload = extractTokenFromAuthPayload(handshake);
		if (StringUtils.hasText(fromAuthPayload)) {
			return fromAuthPayload;
		}

		String fromQuery = handshake.getSingleUrlParam("token");
		if (StringUtils.hasText(fromQuery)) {
			return fromQuery;
		}

		List<String> authHeaders = handshake.getHttpHeaders().getAll("Authorization");
		if (authHeaders != null) {
			for (String header : authHeaders) {
				if (header != null && header.startsWith(BEARER_PREFIX)) {
					return header.substring(BEARER_PREFIX.length()).trim();
				}
			}
		}
		return null;
	}

	private String extractTokenFromAuthPayload(HandshakeData handshake) {
		Map<String, List<String>> urlParams = handshake.getUrlParams();
		if (urlParams == null) {
			return null;
		}
		List<String> tokenParams = urlParams.get("token");
		if (tokenParams != null && !tokenParams.isEmpty() && StringUtils.hasText(tokenParams.get(0))) {
			return tokenParams.get(0);
		}
		return null;
	}

	private String firstHeader(HandshakeData handshake, String headerName) {
		List<String> values = handshake.getHttpHeaders().getAll(headerName);
		if (values == null || values.isEmpty()) {
			return null;
		}
		return values.get(0);
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
