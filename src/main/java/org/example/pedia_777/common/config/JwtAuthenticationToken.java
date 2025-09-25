package org.example.pedia_777.common.config;

import org.example.pedia_777.common.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
	private final AuthUser authUser;

	public JwtAuthenticationToken(AuthUser authUser) {
		super(authUser.getAuthorities());
		this.authUser = authUser;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authUser;
	}
}
