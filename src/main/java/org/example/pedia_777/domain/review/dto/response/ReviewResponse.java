package org.example.pedia_777.domain.review.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private final Long id;
    //private final String nickname; User 에서 받아와야 한다
    private final double star;
    private final String comment;
    private final Long likeCount;
    private final LocalDateTime createdAt;


    public static ReviewResponse of(
            Long id,
            double star,
            String comment,
            Long likeCount,
            LocalDateTime createdAt
    ) {
        return new ReviewResponse(id, star, comment, likeCount, createdAt);
    }

}
