package org.example.pedia_777.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;
import org.example.pedia_777.common.code.CommonErrorCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieRankingService;
import org.example.pedia_777.domain.movie.service.MovieServiceApi;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.request.ReviewUpdateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.error.ReviewErrorCode;
import org.example.pedia_777.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberServiceApi memberServiceApi;

    @Mock
    private MovieServiceApi movieServiceApi;

    @Mock
    private MovieRankingService movieRankingService;

    @InjectMocks
    private ReviewService reviewService;

    private Member member;
    private Movie movie;
    private Review review;

    @BeforeEach
    void setUp() {

        // 실제 엔티티 팩토리 메서드 사용
        member = Member.signUp("test@test.com", "password123", "tester");
        movie = Movie.of(
                "김박수", "고릴라", "김이박, 김삼순",
                "드라마", LocalDate.now(), 132,
                "한국", "test", "testUrl"
        );

        review = Review.create("재밌는 영화", 4.5, 0L, movie, member);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        // given
        AuthMember authMember = new AuthMember(1L, member.getEmail(), member.getNickname());
        ReviewCreateRequest request = new ReviewCreateRequest("재밌는 영화", 4.5, 1L);

        given(memberServiceApi.getMemberById(authMember.id())).willReturn(member);
        given(movieServiceApi.getMovieEntity(request.movieId())).willReturn(movie);
        given(reviewRepository.save(any(Review.class))).willReturn(review);
        doNothing().when(movieRankingService).addMovieScore(anyLong(), anyDouble());

        // when
        ReviewResponse response = reviewService.createReview(authMember, request);

        // then
        assertNotNull(response);
        assertThat(response.comment()).isEqualTo("재밌는 영화");
        assertThat(response.star()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("리뷰 작성자가 리뷰 수정 시 성공")
    void updateReview_success() {
        // given
        Long reviewId = 1L;
        AuthMember authMember = new AuthMember(1L, member.getEmail(), member.getNickname());
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 내용", 5.0);

        given(reviewRepository.findByIdAndMemberId(reviewId, authMember.id())).willReturn(Optional.of(review));

        // when
        ReviewResponse response = reviewService.updateReview(reviewId, authMember, request);

        // then
        assertNotNull(response);
        assertThat(response.comment()).isEqualTo("수정된 내용");
        assertThat(response.star()).isEqualTo(5);
        assertThat(response.nickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("리뷰 작성자가 아닌 사람이 리뷰 수정 시 실패")
    void updateReview_fail() {
        // given
        Long reviewId = 1L;
        AuthMember authMember = new AuthMember(2L, "JJIN@test.com", "JJIN");
        ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 내용", 5.0);

        given(reviewRepository.findByIdAndMemberId(reviewId, authMember.id())).willReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.updateReview(reviewId, authMember, request));

        // then
        assertEquals(CommonErrorCode.VALIDATION_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 작성자가 리뷰 삭제 성공")
    void deleteReview_success() {
        // given
        Long reviewId = 1L;
        AuthMember authMember = new AuthMember(1L, member.getEmail(), member.getNickname());

        given(reviewRepository.findByIdAndMemberId(reviewId, authMember.id())).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(reviewId, authMember);

        // then
        verify(reviewRepository).findByIdAndMemberId(reviewId, authMember.id());
        verify(reviewRepository).delete(review);
    }

    @Test
    @DisplayName("리뷰 작성자가 아닌 사람이 리뷰 삭제 시 실패")
    void deleteReview_fail() {
        // given
        Long reviewId = 1L;
        AuthMember authMember = new AuthMember(2L, "JJIN@test.com", "JJIN");

        given(reviewRepository.findByIdAndMemberId(reviewId, authMember.id())).willReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.deleteReview(reviewId, authMember));

        // then
        assertEquals(CommonErrorCode.VALIDATION_FAILED, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 조회 성공")
    void getReviewById_success() {
        // given
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when
        Review found = reviewService.getReviewById(1L);

        // then
        assertNotNull(found);
        assertThat(found.getComment()).isEqualTo("재밌는 영화");
        assertThat(found.getStar()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("리뷰 조회 실패 시 예외 발생")
    void getReviewById_notFound() {
        // given
        given(reviewRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> reviewService.getReviewById(99L));

        // then
        assertEquals(ReviewErrorCode.REVIEW_NOT_FOUND, exception.getErrorCode());
    }
}
