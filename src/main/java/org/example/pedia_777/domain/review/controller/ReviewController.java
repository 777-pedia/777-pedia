package org.example.pedia_777.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    @PostMapping("/api/v1/movies/{movieId}/reviews")
    public ResponseEntity<GlobalApiResponse<ReviewResponse>> createComment(
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewResponse request
            //@AuthenticationPrincipal UserPrincipal aushUser
    ) {
        ReviewResponse response = reviewService.createComment(movieId, request);

        return ResponseHelper.success(SuccessCode.REVIEW_SUCCESS, response);
    }


}
