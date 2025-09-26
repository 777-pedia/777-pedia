package org.example.pedia_777.domain.like.dto.response;

public record LikeResponse(
        Long reviewId,
        Long likeCount,
        Boolean isLiked
) {

    public static LikeResponse of(Long reviewId, Long likeCount, Boolean isLiked) {
        return new LikeResponse(reviewId, likeCount, isLiked);
    }
}
