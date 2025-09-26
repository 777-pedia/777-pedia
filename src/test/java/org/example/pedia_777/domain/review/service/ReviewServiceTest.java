package org.example.pedia_777.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberService;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.review.dto.request.ReviewCreateRequest;
import org.example.pedia_777.domain.review.dto.response.ReviewResponse;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private MovieService movieService;

    @InjectMocks
    private ReviewService reviewService;

    private Member member;
    private Movie movie;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 실제 엔티티 팩토리 메서드 사용
        member = Member.signUp("test@test.com", "password123", "tester");
        movie = Movie.of(
                "김박수", "고릴라", "김이박, 김삼순",
                "드라마", LocalDateTime.now(), 132L,
                "한국", "test", "test2"
        );

        review = Review.create("재밌는 영화", 4.5, 0L, movie, member);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_success() {
        // given
        AuthMember authMember = new AuthMember(1L, member.getEmail(), member.getNickname());
        ReviewCreateRequest request = new ReviewCreateRequest("재밌는 영화", 4.5);

        given(memberService.findMemberById(authMember.id())).willReturn(member);
        given(movieService.findMovieById(1L)).willReturn(movie);
        given(reviewRepository.save(any(Review.class))).willReturn(review);

        // when
        ReviewResponse response = reviewService.createReview(1L, authMember, request);

        // then
        assertThat(response.comment()).isEqualTo("재밌는 영화");
        assertThat(response.star()).isEqualTo(4.5);
        assertThat(response.likeCount()).isEqualTo(0L);
        assertThat(response.nickname()).isEqualTo("tester");

        then(memberService).should().findMemberById(authMember.id());
        then(movieService).should().findMovieById(1L);
        then(reviewRepository).should().save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 조회 성공")
    void findReviewById_success() {
        // given
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when
        Review found = reviewService.findReviewById(1L);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getComment()).isEqualTo("재밌는 영화");
        assertThat(found.getStar()).isEqualTo(4.5);
    }

    @Test
    @DisplayName("리뷰 조회 실패 시 예외 발생")
    void findReviewById_notFound() {
        // given
        given(reviewRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.findReviewById(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getMessage());
    }
}
