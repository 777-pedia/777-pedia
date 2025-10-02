package org.example.pedia_777.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessMessage;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.dto.Response;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.request.ReviewUpdateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.ReviewSort;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/reviews")
    public Response<ReviewResponse> createReview(
            @AuthenticationPrincipal AuthMember authMember,
            @Valid @RequestBody ReviewCreateRequest request) {

        ReviewResponse response = reviewService.createReview(authMember, request);
        return Response.of(SuccessMessage.CREATED_SUCCESS, response);
    }

    @GetMapping("/reviews")
    public Response<PageResponse<ReviewResponse>> getReviewList(
            @RequestParam Long movieId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") ReviewSort sort
    ) {
        PageResponse<ReviewResponse> response = reviewService.getReviews(movieId, page, size, sort);
        return Response.of(SuccessMessage.REQUEST_SUCCESS, response);
    }

    @PutMapping("/reviews/{reviewId}")
    public Response<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthMember authMember,
            @RequestBody ReviewUpdateRequest request) {

        ReviewResponse response = reviewService.updateReview(reviewId, authMember, request);
        return Response.of(SuccessMessage.REQUEST_SUCCESS, response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public Response<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal AuthMember authMember) {

        reviewService.deleteReview(reviewId, authMember);
        return Response.of(SuccessMessage.DELETED_SUCCESS, null);
    }
}
