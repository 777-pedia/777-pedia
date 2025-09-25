package org.example.pedia_777.domain.member.entity;

import org.example.pedia_777.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Members extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	private String nickname;

	@Builder(access = AccessLevel.PRIVATE)
	public Members(String email, String password, String nickname) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}

	public static Members signUp(String email, String password, String nickname) {
		return Members.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.build();
	}
}
