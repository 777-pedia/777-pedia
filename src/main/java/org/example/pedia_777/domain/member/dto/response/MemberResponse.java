package org.example.pedia_777.domain.member.dto.response;

import java.time.LocalDateTime;

public record MemberResponse(
	Long id,
	String email,
	String nickname,
	LocalDateTime createdAt) {

	public static MemberResponse of(Long id, String email, String nickname, LocalDateTime createdAt) {
		return new MemberResponse(id, email, nickname, createdAt);
	}
}