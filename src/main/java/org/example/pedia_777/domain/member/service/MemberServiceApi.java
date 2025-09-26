package org.example.pedia_777.domain.member.service;

import org.example.pedia_777.domain.member.entity.Member;

public interface MemberServiceApi {

    Member findMemberById(Long memberId);

    void existsByEmail(String email);

    Member findMemberByEmail(String email);
}
