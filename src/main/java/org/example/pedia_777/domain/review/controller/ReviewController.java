package org.example.pedia_777.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.review.code.ReviewSuccessCode;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.request.ReviewUpdateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.ReviewSort;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/reviews")
    public ResponseEntity<GlobalApiResponse<PageResponse<ReviewResponse>>> getReviewList(
            @RequestParam Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") ReviewSort sort
    ) {
        PageResponse<ReviewResponse> response = reviewService.getReviews(movieId, page, size, sort);
        return ResponseHelper.success(ReviewSuccessCode.REVIEW_LIST_VIEWED, response);
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<GlobalApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody ReviewUpdateRequest request) {

        ReviewResponse response = reviewService.updateReview(reviewId, authMember, request);
        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<GlobalApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthMember authMember) {

        reviewService.deleteReview(reviewId, authMember);
        return ResponseHelper.success(CommonSuccessCode.DELETED_SUCCESS);
    }
}
