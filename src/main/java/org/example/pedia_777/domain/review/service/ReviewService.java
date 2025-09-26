package org.example.pedia_777.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieServiceApi;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewServiceApi {

    private final ReviewRepository reviewRepository;
    private final MemberServiceApi memberServiceApi;
    private final MovieServiceApi movieServiceApi;

    @Transactional
    public ReviewResponse createReview(AuthMember authMember, ReviewCreateRequest request) {
        Member member = memberServiceApi.findMemberById(authMember.id());
        Movie movie = movieServiceApi.findMovieById(request.movieId());

        Review review = Review.create(
                request.comment(),
                request.star(),
                0L, // likeCount 기본값 0
                movie,
                member
        );

        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviews(Long movieId, Pageable pageable) {
        movieServiceApi.findMovieById(movieId);

        // 영화 ID로 리뷰 조회 (페이징 적용)
        Page<Review> reviewPage = reviewRepository.findByMovieId(movieId, pageable);

        // 엔티티 -> DTO 매핑
        Page<ReviewResponse> reviewResponsePage = reviewPage.map(ReviewResponse::from);

        return PageResponse.from(reviewResponsePage);
    }

    @Override
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
