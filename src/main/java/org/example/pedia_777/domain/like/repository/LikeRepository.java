package org.example.pedia_777.domain.like.repository;

import java.util.Optional;
import org.example.pedia_777.domain.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMemberIdAndReviewId(Long memberId, Long reviewId);

    Optional<Like> findByMemberIdAndReviewId(Long memberId, Long reviewId);

    @EntityGraph(attributePaths = {"review", "review.movie"})
    Page<Like> findByMemberId(Long memberId, Pageable pageable);

    long countByReviewId(Long id);
}
