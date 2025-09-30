package org.example.pedia_777.domain.review.service;

import org.example.pedia_777.domain.review.entity.Review;

public interface ReviewServiceApi {
    Review getReviewById(Long reviewId);

    Review findReviewByIdForUpdate(Long reviewId);
}
