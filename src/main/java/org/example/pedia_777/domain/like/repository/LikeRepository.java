package org.example.pedia_777.domain.like.repository;

import org.example.pedia_777.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMemberIdAndReviewId(Long memberId, Long reviewId);

}
