package org.example.pedia_777.common.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Builder;

@Builder
public record AuthMember(
	Long id,
	String email,
	String nickname) {

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}
}
