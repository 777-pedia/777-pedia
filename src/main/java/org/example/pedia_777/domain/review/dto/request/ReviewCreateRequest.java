package org.example.pedia_777.domain.review.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    @NotBlank(message = "영화 ID는 필수입니다.")
    private Long movieId;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(min = 10, max = 500, message = "리뷰는 10자 이상 500자 이하여야 합니다.")
    private String comment;

    @NotBlank(message = "별점은 필수입니다.")
    @DecimalMin(value = "0.5", message = "별점은 최소 0.5점입니다.")
    @DecimalMax(value = "5.0", message = "별점은 최대 5.0점입니다.")
    private Double star;

    public ReviewCreateRequest(Long movieId, String comment, Double star) {
        this.movieId = movieId;
        this.comment = comment;
        this.star = star;
    }

}
