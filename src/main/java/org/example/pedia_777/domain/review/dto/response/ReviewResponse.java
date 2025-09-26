package org.example.pedia_777.domain.review.dto.response;

import java.time.LocalDateTime;
import org.example.pedia_777.domain.review.entity.Review;

public record ReviewResponse(
        Long id,
        String comment,
        double star,
        Long likeCount,
        String nickname,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getComment(),
                review.getStar(),
                review.getLikeCount(),
                review.getMember().getNickname(),
                review.getCreatedAt()
        );
    }
}
