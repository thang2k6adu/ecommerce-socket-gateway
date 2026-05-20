package com.ecommerce.socketgateway.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public final class ChatRoleHelper {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	private ChatRoleHelper() {
	}

	public static boolean isAdmin(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (ROLE_ADMIN.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

}
