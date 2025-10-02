package org.example.pedia_777.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.config.JwtUtil;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.code.MemberErrorCode;
import org.example.pedia_777.domain.member.dto.request.MemberLoginRequest;
import org.example.pedia_777.domain.member.dto.request.MemberRequest;
import org.example.pedia_777.domain.member.dto.response.MemberLoginResponse;
import org.example.pedia_777.domain.member.dto.response.MemberResponse;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements MemberServiceApi {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public MemberResponse signup(MemberRequest memberRequest) {
        existsByEmail(memberRequest.email());

        if (memberRepository.existsByNickname(memberRequest.nickname())) {
            throw new BusinessException(MemberErrorCode.NICKNAME_DUPLICATED);
        }

        String encodedPassword = passwordEncoder.encode(memberRequest.password());

        Member member = Member.signUp(memberRequest.email(), encodedPassword,
                memberRequest.nickname());

        memberRepository.save(member);

        return MemberResponse.of(member.getId(), member.getEmail(), memberRequest.nickname(), member.getCreatedAt());
    }

    public void existsByEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(MemberErrorCode.EMAIL_DUPLICATED);
        }
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    @Override
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(MemberErrorCode.NOT_FOUND_MEMBER));
    }

    public MemberLoginResponse login(MemberLoginRequest memberLoginRequest) {
        Member findMember = getMemberByEmail(memberLoginRequest.email());

        if (!passwordEncoder.matches(memberLoginRequest.password(), findMember.getPassword())) {
            throw new BusinessException(MemberErrorCode.NOT_FOUND_EMAIL_PASSWORD);
        }

        String bearerToken = jwtUtil.createToken(findMember.getId(), findMember.getEmail(), findMember.getNickname());

        return MemberLoginResponse.of(bearerToken);
    }
}
