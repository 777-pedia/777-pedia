package org.example.pedia_777.domain.like.dto.response;

import org.example.pedia_777.domain.like.entity.Like;

public record LikedReviewResponse(
        Long reviewId,
        String title,
        String comment
) {

    public static LikedReviewResponse from(Like like) {
        return new LikedReviewResponse(
                like.getReview().getId(), like.getReview().getMovie().getTitle(), like.getReview().getComment()
        );
    }
}
