package org.example.pedia_777.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.*;

import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.dto.request.MemberRequest;
import org.example.pedia_777.domain.member.dto.response.MemberResponse;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("사용자는 올바른 이메일, 닉네임, 비밀번호 입력 시 회원가입에 성공하며 정상적으로 회원 정보가 반환된다.")
	public void signupSuccess() {
		//given
		String email = "test@test.com";
		String password = "test";
		String nickname = "tester";

		MemberRequest memberRequest = new MemberRequest(email, password, nickname);
		when(memberRepository.existsByEmail(eq(memberRequest.email()))).thenReturn(false);
		when(passwordEncoder.encode(memberRequest.password())).thenReturn("encodedPassword");

		Members savedMember = Members.signUp(memberRequest.email(), "encodedPassword", memberRequest.nickname());
		when(memberRepository.save(any(Members.class))).thenReturn(savedMember);

		//when
		MemberResponse signup = memberService.signup(memberRequest);

		//then
		assertThat(signup.email()).isEqualTo(email);
		assertThat(signup.nickname()).isEqualTo(nickname);

		verify(memberRepository).existsByEmail(eq(email));
		verify(memberRepository).save(any(Members.class));
	}

	@Test
	@DisplayName("이미 존재하는 이메일로 회원가입을 시도할 시 예외가 발생한다.")
	public void overlapEmailException() {
		//given
		String email = "test@test.com";
		String password = "test";
		String nickname = "tester";

		MemberRequest memberRequest = new MemberRequest(email, password, nickname);
		when(memberRepository.existsByEmail(memberRequest.email())).thenReturn(true);

		//when && then
		assertThrows(BusinessException.class,
			() -> memberService.signup(memberRequest));
	}

	@Test
	@DisplayName("이미 존재하는 닉네임으로 회원가입을 시도할 시 예외가 발생한다.")
	public void overlapNicknameException() {
		//given
		String email = "test@test.com";
		String password = "test";
		String nickname = "tester";

		MemberRequest memberRequest = new MemberRequest(email, password, nickname);
		when(memberRepository.existsByNickname(memberRequest.nickname())).thenReturn(true);

		//when && then
		assertThrows(BusinessException.class,
			() -> memberService.signup(memberRequest));
	}
}