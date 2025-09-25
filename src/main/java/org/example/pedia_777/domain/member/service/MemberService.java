package org.example.pedia_777.domain.member.service;

import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.dto.request.MemberRequest;
import org.example.pedia_777.domain.member.dto.response.MemberResponse;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberResponse signup(MemberRequest memberRequest) {
		existsByEmail(memberRequest.email());

		if (memberRepository.existsByNickname(memberRequest.nickname())) {
			throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(memberRequest.password());

		Members member = Members.signUp(memberRequest.email(), encodedPassword,
			memberRequest.nickname());

		memberRepository.save(member);

		return MemberResponse.of(member.getId(), member.getEmail(), memberRequest.nickname(), member.getCreatedAt());
	}

	@Transactional(readOnly = true)
	public void existsByEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
		}
	}

	public Members findMemberById(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(
			() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
	}
}
