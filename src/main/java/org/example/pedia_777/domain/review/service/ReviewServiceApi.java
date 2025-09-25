package org.example.pedia_777.domain.review.service;

import org.example.pedia_777.domain.review.dto.response.ReviewPageResponse;
import org.example.pedia_777.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;

public interface ReviewServiceApi {

    Review findReviewById(Long reviewId);

    ReviewPageResponse getComments(Long movieId, Pageable pageable);

}
