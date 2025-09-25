package org.example.pedia_777.domain.member.service;

import org.example.pedia_777.domain.member.entity.Members;

public interface MemberServiceApi {

	Members findMemberById(Long memberId);
}
