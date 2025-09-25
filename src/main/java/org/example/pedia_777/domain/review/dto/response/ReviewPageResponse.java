package org.example.pedia_777.domain.review.dto.response;

import java.util.List;

public record ReviewPageResponse(
        List<ReviewResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number
) {
}