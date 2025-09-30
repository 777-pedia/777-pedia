package org.example.pedia_777.domain.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.like.error.LikeErrorCode;
import org.example.pedia_777.domain.like.lock.distributed.DistributedLikeService;
import org.example.pedia_777.domain.like.repository.LikeRepository;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.member.repository.MemberRepository;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.example.pedia_777.domain.review.entity.Review;
import org.example.pedia_777.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("personal")
@SpringBootTest
public class DistributedLikeTest {

    @Autowired
    private DistributedLikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MovieRepository movieRepository;

    private Review testReview;

    @BeforeEach
    public void setup() {
        likeRepository.deleteAll();
        reviewRepository.deleteAll();
        memberRepository.deleteAll();
        movieRepository.deleteAll();

        // 테스트용 영화 & 테스트용 회원 & 리뷰 생성
        Movie movie = Movie.of(
                "테스트 감독", "테스트 영화", "배우1", "액션",
                LocalDate.now(), 120, "한국", "설명", "https://image.com/150"
        );
        movieRepository.save(movie);

        Member author = Member.signUp("author@test.com", "password", "작성자");
        memberRepository.save(author);

        testReview = Review.create("좋은 영화", 5, 0L, movie, author);
        reviewRepository.save(testReview);
    }

    @Test
    @DisplayName("분산 락으로 여러 사용자의 동시 좋아요가 정확하게 반영된다")
    public void testConcurrentLikesWithDistributedLock() throws InterruptedException {
        // given
        int memberCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(memberCount);
        CyclicBarrier barrier = new CyclicBarrier(memberCount); //동시 출발
        CountDownLatch latch = new CountDownLatch(memberCount); //동시 대기

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 100명의 사용자 생성
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < memberCount; i++) {
            Member member = Member.signUp(
                    "member" + i + "@test.com",
                    "password",
                    "유저" + i
            );
            members.add(memberRepository.save(member));
        }

        // when - 100명이 동시에 같은 리뷰에 좋아요
        for (Member member : members) {
            executorService.execute(() -> {
                try {
                    barrier.await();

                    likeService.addLike(member.getId(), testReview.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Review finalReview = reviewRepository.findById(testReview.getId())
                .orElseThrow();
        long likeCount = likeRepository.countByReviewId(testReview.getId());

        System.out.println("=== 테스트 결과 ===");
        System.out.println("성공한 좋아요: " + successCount.get());
        System.out.println("실패한 좋아요: " + failCount.get());
        System.out.println("DB의 좋아요 수: " + likeCount);
        System.out.println("Review의 likeCount: " + finalReview.getLikeCount());

        assertEquals(memberCount, successCount.get());
        assertEquals(0, failCount.get());
        assertEquals(memberCount, likeCount);
        assertEquals(memberCount, finalReview.getLikeCount());
    }

    @Test
    @DisplayName("같은 사용자가 동시에 여러 번 좋아요 시도 시 한 번만 성공한다")
    public void testSameMemberConcurrentLikes() throws InterruptedException {
        // given
        Member member = Member.signUp("test@test.com", "password", "테스터");
        memberRepository.save(member);

        int attemptCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(attemptCount);
        CountDownLatch latch = new CountDownLatch(attemptCount);
        CyclicBarrier barrier = new CyclicBarrier(attemptCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 같은 사용자가 10번 동시 시도
        for (int i = 0; i < attemptCount; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();

                    likeService.addLike(member.getId(), testReview.getId());
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    if (e.getErrorCode() == LikeErrorCode.LIKE_ALREADY_EXISTS) {
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Review finalReview = reviewRepository.findById(testReview.getId())
                .orElseThrow();
        long likeCount = likeRepository.countByReviewId(testReview.getId());

        System.out.println("=== 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패(중복): " + failCount.get());
        System.out.println("DB 좋아요 수: " + likeCount);

        assertEquals(1, successCount.get());
        assertEquals(attemptCount - 1, failCount.get());
        assertEquals(1, likeCount);
        assertEquals(1, finalReview.getLikeCount());
    }

    @Test
    @DisplayName("동시에 좋아요와 취소가 발생해도 정확한 카운트가 유지된다")
    public void testConcurrentLikeAndCancel() throws InterruptedException {
        // given
        int addLikeCount = 25;
        int cancelCount = 25;
        int totalOperations = addLikeCount + cancelCount;

        List<Member> likeMembers = new ArrayList<>();
        List<Member> cancelMembers = new ArrayList<>();

        // 좋아요할 25명 생성
        for (int i = 0; i < addLikeCount; i++) {
            Member member = Member.signUp(
                    "like" + i + "@test.com",
                    "password",
                    "좋아요유저" + i
            );
            memberRepository.save(member);
            likeMembers.add(member);
        }

        // 취소할 25명 생성 (먼저 좋아요 눌러둠)
        for (int i = 0; i < cancelCount; i++) {
            Member member = Member.signUp(
                    "cancel" + i + "@test.com",
                    "password",
                    "취소유저" + i
            );
            memberRepository.save(member);
            cancelMembers.add(member);

            // 미리 좋아요 눌러둠
            likeService.addLike(member.getId(), testReview.getId());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(totalOperations);
        CountDownLatch latch = new CountDownLatch(totalOperations);
        CyclicBarrier barrier = new CyclicBarrier(totalOperations);

        // 좋아요 작업 25개
        for (Member member : likeMembers) {
            executorService.execute(() -> {
                try {
                    barrier.await();  // 50개 모두 대기
                    likeService.addLike(member.getId(), testReview.getId());
                } catch (Exception e) {
                    System.out.println("좋아요 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 취소 작업 25개
        for (Member member : cancelMembers) {
            executorService.execute(() -> {
                try {
                    barrier.await();  // 50개 모두 대기
                    likeService.cancelLike(member.getId(), testReview.getId());
                } catch (Exception e) {
                    System.out.println("취소 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Review finalReview = reviewRepository.findById(testReview.getId())
                .orElseThrow();
        long remainingLikes = likeRepository.countByReviewId(testReview.getId());

        System.out.println("=== 테스트 결과 ===");
        System.out.println("DB 좋아요 수: " + remainingLikes);
        System.out.println("Review likeCount: " + finalReview.getLikeCount());

        assertEquals(addLikeCount, remainingLikes);
        assertEquals(addLikeCount, finalReview.getLikeCount());
    }
}