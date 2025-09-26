package org.example.pedia_777.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(

        @NotBlank
        @Size(max = 1000, message = "댓글은 최대 1000자까지 입력 가능합니다.")
        String comment,

        @Min(0) @Max(5)
        double star
) {
}
