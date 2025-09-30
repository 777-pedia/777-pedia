package org.example.pedia_777.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.like.code.LikeErrorCode;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.dto.response.LikedReviewResponse;
import org.example.pedia_777.domain.like.entity.Like;
import org.example.pedia_777.domain.like.repository.LikeRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.service.ReviewServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    final long memberId = 1L;
    final long reviewId = 1L;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private ReviewServiceApi reviewServiceApi;
    @Mock
    private MemberServiceApi memberServiceApi;
    @InjectMocks
    private LikeService likeService;
    //테스트 데이터
    private Member mockMember;
    private Review mockReview;
    private Review mockReview2;

    @BeforeEach
    void setUp() {
        mockMember = Mockito.mock(Member.class);
        // 현재 시간
        // 이미지 URL
        Movie mockMovie = Movie.of(
                "테스트 감독",
                "테스트 영화 제목",
                "배우1, 배우2",
                "장르1, 장르2",
                LocalDate.now(), // 현재 시간
                120,
                "테스트 국가",
                "이 영화는 테스트를 위해 만들어졌습니다.",
                "https://image.com/150" // 이미지 URL
        );
        mockReview = Review.create("comment", 4, 1L, mockMovie, mockMember);
        mockReview2 = Review.create("comment", 2, 1L, mockMovie, mockMember);
    }

    @Test
    @DisplayName("좋아요 성공 테스트")
    void addLike_Success() {
        //Given
        given(likeRepository.existsByMemberIdAndReviewId(memberId, reviewId)).willReturn(false);
        given(reviewServiceApi.getReviewById(reviewId)).willReturn(mockReview);
        given(memberServiceApi.getMemberById(memberId)).willReturn(mockMember);

        //When
        LikeResponse response = likeService.addLike(memberId, reviewId);

        //Then
        assertThat(response.reviewId()).isEqualTo(reviewId);
        assertThat(response.isLiked()).isTrue();

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    @DisplayName("이미 좋아요를 누른 리뷰에 다시 좋아요를 시도할 경우 예외 발생 테스트")
    void addLike_AlreadyExists_ThrowsException() {
        // Given
        given(likeRepository.existsByMemberIdAndReviewId(memberId, reviewId)).willReturn(true);
        // When & Then
        assertThatThrownBy(() -> likeService.addLike(memberId, reviewId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(LikeErrorCode.LIKE_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("좋아요 취소 성공 테스트")
    void cancelLike_Success() {
        // Given
        Like mockLike = Like.of(mockMember, mockReview);

        given(likeRepository.findByMemberIdAndReviewId(memberId, reviewId)).willReturn(Optional.of(mockLike));
        given(reviewServiceApi.getReviewById(reviewId)).willReturn(mockReview);

        // When
        likeService.cancelLike(memberId, reviewId);

        // Then
        verify(likeRepository, times(1)).delete(mockLike);
        assertThat(mockReview.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("존재하지 않는 좋아요 취소시 예외 발생 테스트")
    void cancelLike_NotFound_ThrowsException() {
        //Given
        given(likeRepository.findByMemberIdAndReviewId(memberId, reviewId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> likeService.cancelLike(memberId, reviewId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(LikeErrorCode.LIKE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("좋아요를 누른 리뷰 조회가 성공적으로 수행된다.")
    void getLikedReviews_Success() {
        //given
        int page = 1;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(0, size, sort);

        Like like1 = Like.of(mockMember, mockReview);
        Like like2 = Like.of(mockMember, mockReview2);

        given(likeRepository.findByMemberId(memberId, pageable)).willReturn(
                new PageImpl<>(List.of(like1, like2), pageable, 2));

        //when
        PageResponse<LikedReviewResponse> likedReviews = likeService.getLikedReviews(memberId, page, size);

        //then
        assertThat(likedReviews.content().size()).isEqualTo(2);
        assertThat(likedReviews.content().get(0).comment()).isEqualTo("comment");
        verify(likeRepository, times(1)).findByMemberId(memberId, pageable);
    }

    @Test
    @DisplayName("페이지가 0 이하일 때 첫 번째 페이지로 조회된다.")
    void getLikedReviews_PageZeroOrNegative() {
        //given
        int invalidPage = -1;
        int size = 10;

        Sort sort = Sort.by("createdAt").descending();
        Pageable expectedPageable = PageRequest.of(0, size, sort);  // Math.max(0, -1-1) = 0

        Like like1 = Like.of(mockMember, mockReview);

        given(likeRepository.findByMemberId(memberId, expectedPageable)).willReturn(
                new PageImpl<>(List.of(like1), expectedPageable, 1));

        //when
        PageResponse<LikedReviewResponse> result = likeService.getLikedReviews(memberId, invalidPage, size);

        //then
        assertThat(result.content().get(0).comment()).isEqualTo("comment");
        verify(likeRepository).findByMemberId(memberId, expectedPageable);
    }

    @Test
    @DisplayName("좋아요한 리뷰가 없을 때 빈 페이지가 반환된다.")
    void getLikedReviews_EmptyResult() {
        //given
        int page = 1;
        int size = 10;

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, size, sort);

        given(likeRepository.findByMemberId(memberId, pageable)).willReturn(
                new PageImpl<>(List.of(), pageable, 0));

        //when
        PageResponse<LikedReviewResponse> result = likeService.getLikedReviews(memberId, page, size);

        //then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
        verify(likeRepository).findByMemberId(memberId, pageable);
    }


}