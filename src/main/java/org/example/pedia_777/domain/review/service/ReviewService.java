package org.example.pedia_777.domain.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.member.service.MemberService;
import org.example.pedia_777.domain.movie.entity.Movies;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewPageResponse;
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
    private final MemberService memberService;
    private final MovieService movieService;

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        Members member = memberService.findMemberById(request.memberId());
        Movies movie = movieService.findMovieById(request.movieId());

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
    @Transactional(readOnly = true)
    public ReviewPageResponse getComments(Long movieId, Pageable pageable) {
        // 영화 존재 여부 확인 (없으면 예외 발생)
        movieService.findMovieById(movieId);

        // 영화 ID로 리뷰 조회 (페이징 적용)
        Page<Review> reviewPage = reviewRepository.findByMovies_Id(movieId, pageable);

        // 엔티티 -> DTO 매핑
        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(ReviewResponse::from)
                .toList();

        // 레코드 생성
        return new ReviewPageResponse(
                content,
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.getSize(),
                reviewPage.getNumber()
        );
    }

    @Override
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));
    }
}
