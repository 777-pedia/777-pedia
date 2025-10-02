package org.example.pedia_777.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.pedia_777.common.config.JwtUtil;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.dto.request.MemberLoginRequest;
import org.example.pedia_777.domain.member.dto.request.MemberRequest;
import org.example.pedia_777.domain.member.dto.response.MemberLoginResponse;
import org.example.pedia_777.domain.member.dto.response.MemberResponse;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

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

        Member savedMember = Member.signUp(memberRequest.email(), "encodedPassword", memberRequest.nickname());
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        //when
        MemberResponse signup = memberService.signup(memberRequest);

        //then
        assertThat(signup.email()).isEqualTo(email);
        assertThat(signup.nickname()).isEqualTo(nickname);

        verify(memberRepository).existsByEmail(eq(email));
        verify(memberRepository).save(any(Member.class));
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

    @Test
    @DisplayName("로그인 시 이메일과 비밀번호가 올바르지 않을 경우 예외가 발생한다.")
    public void notMatchEmailPasswordException() {
        String password = "test";
        String encodedPassword = "encodedPassword";

        //given
        Member member = Member.signUp("test@test.com", password, "tester");
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(eq(encodedPassword), eq(member.getPassword()))).thenReturn(false);

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(member.getEmail(), encodedPassword);

        //when && then
        assertThrows(BusinessException.class, () ->
                memberService.login(memberLoginRequest));
    }

    @Test
    @DisplayName("존재하는 회원이 로그인 시 정상적으로 토큰이 반환된다.")
    public void successLoginGetToken() {
        //given
        Member member = Member.signUp("test@test.com", "test", "tester");
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(member.getPassword(), "test")).thenReturn(true);
        when(jwtUtil.createToken(any(), eq(member.getEmail()), eq(member.getNickname()))).thenReturn("fakeToken");

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(member.getEmail(), member.getPassword());

        //when
        MemberLoginResponse login = memberService.login(memberLoginRequest);

        //then
        assertEquals(login.token(), "fakeToken");
    }
}