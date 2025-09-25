package org.example.pedia_777.domain.member.dto.response;

public record MemberLoginResponse(
	String token
) {
	public static MemberLoginResponse of(String token) {
		return new MemberLoginResponse(token);
	}
}
