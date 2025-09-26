package org.example.pedia_777.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberService;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService implements ReviewServiceApi {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final MovieService movieService;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        Member member = memberService.findMemberById(request.memberId());
        Movie movie = movieService.findMovieById(request.movieId());

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

    @Override
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
