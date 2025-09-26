package org.example.pedia_777.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<GlobalApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewResponse response = reviewService.createReview(authMember, request);
        return ResponseHelper.success(CommonSuccessCode.CREATED_SUCCESS, response);
    }
}
