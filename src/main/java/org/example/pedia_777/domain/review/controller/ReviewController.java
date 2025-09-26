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
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.ReviewSort;
import org.example.pedia_777.domain.review.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
        return ResponseHelper.success(CommonSuccessCode.CREATED_SUCCESS, response);
    }

    @GetMapping("/reviews")
    public ResponseEntity<GlobalApiResponse<PageResponse<ReviewResponse>>> getReviewList(
            @RequestParam Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LIKES") ReviewSort sort
    ) {
        // 라이크가 많은 순으로 디폴트 정렬, 작성일 정렬
        Sort sortOrder;
        if (sort == ReviewSort.OLDEST) {
            sortOrder = Sort.by("createdAt").ascending();
        } else if (sort == ReviewSort.NEWEST) {
            sortOrder = Sort.by("createdAt").descending();
        } else {
            sortOrder = Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt"));
        }

        // 페이지와 정렬 조건을 포함한 pageable 생성 (페이지 시작값을 1로 설정)
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sortOrder);
        PageResponse<ReviewResponse> response = reviewService.getReviews(movieId, pageable);
        return ResponseHelper.success(ReviewSuccessCode.REVIEW_LIST_VIEWED, response);
    }
}
