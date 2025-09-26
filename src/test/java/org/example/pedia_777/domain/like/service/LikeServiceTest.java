package org.example.pedia_777.domain.like.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.pedia_777.domain.like.entity.Like;
import org.example.pedia_777.domain.like.repository.LikeRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.service.MemberServiceApi;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.service.ReviewServiceApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {


    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ReviewServiceApi reviewServiceApi;

    @Mock
    private MemberServiceApi memberServiceApi;

    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("[이슈1] 동일 사용자가 동시 좋아요 요청시, 중복 저장 문제가 발생하는 테스트")
    void whenSameUserLikesConcurrently_thenDuplicateSaveOccurs() throws InterruptedException {
        // Given
        final long reviewId = 1L;
        final long memberId = 100L;
        final int threadCount = 10; // 10개의 동시 요청

        Movie mockMovie = Mockito.mock(Movie.class);
        Member mockMember = Member.signUp("email@email.com", "password", "nickname"); // 실제 객체나 Mock 객체 사용
        Review mockReview = Review.create("comment", 4, 0L, mockMovie, mockMember); // likeCount를 가지는 실제 객체 사용

        when(likeRepository.existsByMemberIdAndReviewId(reviewId, memberId)).thenReturn(false);
        when(memberServiceApi.findMemberById(memberId)).thenReturn(mockMember);
        when(reviewServiceApi.findReviewById(reviewId)).thenReturn(mockReview);

        // 스레드를 관리할 ExecutorService와 동시성 제어를 위한 CountDownLatch 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    likeService.addLike(reviewId, memberId);
                } catch (Exception e) {
                    // 예외가 발생하더라도 테스트는 계속 진행
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(5, TimeUnit.SECONDS); // 모든 스레드가 작업을 마칠 때까지 대기

        // Then
        // 1번만 호출되어야 할 save()가 10번 호출되는 것을 확인
        verify(likeRepository, times(threadCount)).save(any(Like.class));
    }


    @Test
    @DisplayName("[이슈2] 여러 사용자가 동시 좋아요 요청시, 카운트 누락이 발생하는 테스트")
    void whenMultipleUsersLikeConcurrently_thenLikeCountIsLost() throws InterruptedException {
        // Given
        final long reviewId = 1L;
        final int userCount = 500; // 500명의 다른 사용자

        // 중요: 동시성 문제가 있는 실제 Review 객체를 사용
        Movie mockMovie = Mockito.mock(Movie.class);
        Member mockMember = Mockito.mock(Member.class);
        Review realReview = Review.create("comment", 4, 0L, mockMovie, mockMember); // likeCount를 가지는 실제 객체 사용

        when(reviewServiceApi.findReviewById(reviewId)).thenReturn(realReview);

        // 50명의 다른 사용자를 설정
        for (int i = 0; i < userCount; i++) {
            long userId = 100L + i;
            when(likeRepository.existsByMemberIdAndReviewId(reviewId, userId)).thenReturn(false);
            when(memberServiceApi.findMemberById(userId)).thenReturn(mockMember);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch latch = new CountDownLatch(userCount);

        // When
        for (int i = 0; i < userCount; i++) {
            final long userId = 100L + i;
            executorService.submit(() -> {
                try {
                    likeService.addLike(reviewId, userId);
                } catch (Exception e) {
                    // 무시
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);

        // Then
        System.out.println("기대했던 최종 좋아요 수: " + userCount);
        System.out.println("실제 최종 좋아요 수: " + realReview.getLikeCount());

        // 500번의 '좋아요' 요청이 있었지만, 동시성 문제로 인해 일부 업데이트가 누락
        assertThat(realReview.getLikeCount()).isNotEqualTo((long) userCount);
    }


}