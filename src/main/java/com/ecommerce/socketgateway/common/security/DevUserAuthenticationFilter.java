package com.ecommerce.socketgateway.common.security;

import com.ecommerce.socketgateway.config.SocketProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * When socket JWT auth is disabled, allows REST chat APIs via {@code X-User-Id} header (local dev).
 */
@Component
@RequiredArgsConstructor
public class DevUserAuthenticationFilter extends OncePerRequestFilter {

	private final SocketProperties socketProperties;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (!socketProperties.getAuth().isEnabled()
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			String userId = request.getHeader(ChatUserContext.DEV_USER_HEADER);
			if (StringUtils.hasText(userId)) {
				var authentication = new UsernamePasswordAuthenticationToken(
						userId.trim(),
						null,
						List.of(new SimpleGrantedAuthority("ROLE_USER")));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

}
