package org.example.pedia_777.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull Long memberId,
        @NotNull Long movieId,
        @NotBlank String comment,
        @Min(0) @Max(5) double star
) {
}
