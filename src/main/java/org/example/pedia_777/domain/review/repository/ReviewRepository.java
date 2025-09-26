package org.example.pedia_777.domain.review.repository;

import org.example.pedia_777.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByMovie_Id(Long movieId, Pageable pageable);
}
