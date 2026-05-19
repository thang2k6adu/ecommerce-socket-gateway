package com.ecommerce.socketgateway.common.security;

import com.ecommerce.socketgateway.common.exception.BadRequestException;
import com.ecommerce.socketgateway.config.SocketProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class ChatUserContext {

	public static final String DEV_USER_HEADER = "X-User-Id";

	private final SocketProperties socketProperties;

	public String requireUserId(Authentication authentication) {
		if (!socketProperties.getAuth().isEnabled()) {
			String devUserId = resolveDevUserIdFromRequest();
			if (StringUtils.hasText(devUserId)) {
				return devUserId.trim();
			}
		}

		if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
			String subject = jwt.getSubject();
			if (StringUtils.hasText(subject)) {
				return subject.trim();
			}
		}

		throw new BadRequestException("Missing user identity. Use JWT or header " + DEV_USER_HEADER);
	}

	private String resolveDevUserIdFromRequest() {
		ServletRequestAttributes attributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return null;
		}
		HttpServletRequest request = attributes.getRequest();
		return request.getHeader(DEV_USER_HEADER);
	}

}
