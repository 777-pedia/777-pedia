package org.example.pedia_777.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewPageResponse;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        return ResponseHelper.success(SuccessCode.REVIEW_SUCCESS, response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<GlobalApiResponse<ReviewPageResponse>> getReviewList(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort
    ) {

        Sort sortOrder = sort.equalsIgnoreCase("oldest") ?
                Sort.by("createdAt").ascending() :
                Sort.by("createdAt").descending();// 기본값 newest

        // 쿼리로 받은 정보를 담는다?
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        ReviewPageResponse response = reviewService.getComments(movieId, pageable);

        return ResponseHelper.success(SuccessCode.REVIEW_LIST_VIEWED, response);
    }
}
