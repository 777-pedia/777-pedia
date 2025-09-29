package org.example.pedia_777.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.example.pedia_777.common.config.JwtAuthenticationFilter;
import org.example.pedia_777.common.config.JwtUtil;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class LikeServiceConcurrencyTest {

    @MockitoBean // 가짜 객체 주입
    JwtUtil jwtUtil;

    @MockitoBean // 가짜 객체 주입
    JwtAuthenticationFilter jwtAuthenticationFilter;


    @Autowired
    private LikeService likeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private LikeRepository likeRepository;

    private Long reviewId;
    private List<Long> likeId;

    @BeforeEach
    void setUp() {
        // 작성자, 영화, 리뷰 생성
        Member author = memberRepository.save(Member.signUp("author@test.com", "1234", "author"));
        Movie movie = movieRepository.save(
                Movie.of(
                        "감독", "동시성 테스트 영화", "배우1,배우2", "장르",
                        LocalDate.now(), 120, "한국", "overview", "http://poster"
                )
        );
        Review review = reviewRepository.save(
                Review.create("좋아요 동시성 테스트", 4.5, 0L, movie, author)
        );
        this.reviewId = review.getId();

        // 유저 100명 생성
        int users = 100;
        likeId = new ArrayList<>(users);
        for (int i = 0; i < users; i++) {
            Member m = memberRepository.save(Member.signUp("u" + i + "@test.com", "1234", "u" + i));
            likeId.add(m.getId());
        }
    }

    @Test
    @DisplayName("서로 다른 유저 여러명이 동시에 같은 리뷰에 좋아요를 누르고 likeCount가 정확해야 한다")
    void concurrentAddLike() throws Exception {

        // given
        int threads = likeId.size();

        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        List<Future<?>> futures = new ArrayList<>(threads);

        // when
        for (Long memberId : likeId) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();
                likeService.addLike(memberId, reviewId);
                done.countDown();
                return null;
            }));
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        done.await(30, TimeUnit.SECONDS);

        // then
        for (Future<?> f : futures) {
            f.get(0, TimeUnit.SECONDS);
        }
        pool.shutdown();

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(review.getLikeCount()).isEqualTo(likeId.size());

        for (int i = 0; i < Math.min(10, likeId.size()); i++) {
            Long mid = likeId.get(i);
            boolean exists = likeRepository.existsByMemberIdAndReviewId(mid, reviewId);
            assertThat(exists).isTrue();
        }
    }

    @Test
    @DisplayName("서로 다른 유저 여러명이 동시에 같은 리뷰에 좋아요를 취소했을 때 likeCount가 정확해야 한다")
    void concurrentCancelLike() throws Exception {

        // given
        // 미리 100명이 리뷰에 좋아요 누름
        for (Long memberId : likeId) {
            likeService.addLike(memberId, reviewId);
        }

        int threads = likeId.size();

        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        List<Future<?>> futures = new ArrayList<>(threads);

        // when
        for (Long memberId : likeId) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();
                likeService.cancelLike(memberId, reviewId);
                done.countDown();
                return null;
            }));
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        done.await(30, TimeUnit.SECONDS);

        // then
        for (Future<?> f : futures) {
            f.get(0, TimeUnit.SECONDS);
        }
        pool.shutdown();

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(review.getLikeCount()).isZero();

        for (int i = 0; i < Math.min(10, likeId.size()); i++) {
            Long mid = likeId.get(i);
            boolean exists = likeRepository.existsByMemberIdAndReviewId(mid, reviewId);
            assertThat(exists).isFalse();
        }
    }
}
