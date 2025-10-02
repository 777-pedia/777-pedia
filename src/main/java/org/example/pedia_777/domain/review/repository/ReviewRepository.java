package org.example.pedia_777.domain.review.repository;

import java.util.Optional;
import org.example.pedia_777.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"member"})
    Page<Review> findByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.id = :reviewId AND r.member.id = :memberId")
    Optional<Review> findByIdAndMemberId(@Param("reviewId") Long reviewId,
                                         @Param("memberId") Long memberId);
}
