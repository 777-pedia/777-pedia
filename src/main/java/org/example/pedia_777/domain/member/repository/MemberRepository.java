package org.example.pedia_777.domain.member.repository;

import java.util.Optional;

import org.example.pedia_777.domain.member.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Members, Long> {
	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	Optional<Members> findByEmail(String email);
}
