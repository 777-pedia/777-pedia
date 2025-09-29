package org.example.pedia_777.domain.review.service;

import org.example.pedia_777.domain.review.entity.Review;

public interface ReviewServiceApi {
    Review findReviewById(Long reviewId);

    void incrementLikeCount(Long reviewId);

    void decrementLikeCount(Long reviewId);

    Long getLikeCount(Long reviewId);
}
