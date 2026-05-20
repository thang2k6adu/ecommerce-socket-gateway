package com.ecommerce.socketgateway.common.security;

import com.ecommerce.socketgateway.common.exception.BadRequestException;
import com.ecommerce.socketgateway.config.SocketProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class ChatUserContext {

	public static final String DEV_USER_HEADER = "X-User-Id";

	private final SocketProperties socketProperties;

	public String requireUserId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new BadRequestException("Unauthorized");
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof Jwt jwt) {
			return requireJwtSubject(jwt);
		}

		if (!socketProperties.getAuth().isEnabled() && principal instanceof String userId) {
			if (StringUtils.hasText(userId)) {
				return userId.trim();
			}
		}

		throw new BadRequestException("Missing user identity");
	}

	private String requireJwtSubject(Jwt jwt) {
		String subject = jwt.getSubject();
		if (!StringUtils.hasText(subject)) {
			throw new BadRequestException("JWT subject is missing");
		}
		return subject.trim();
	}

}
