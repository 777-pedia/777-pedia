package org.example.pedia_777.domain.like.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.pedia_777.Pedia777Application;
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

@SpringBootTest(classes = Pedia777Application.class)
class LikeServiceConcurrencyTest {

    @Autowired
    private LikeService likeService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private LikeRepository likeRepository;

    private Long reviewId;

    @BeforeEach
    void setUp() {
        Movie movie = movieRepository.save(
                Movie.of("감독", "제목", "배우1,배우2", "드라마",
                        LocalDate.now(), 120, "KR", "줄거리", "http://img")
        );

        Member writer = memberRepository.save(Member.signUp("writer@test.com", "pw", "writer"));
        Review review = reviewRepository.save(Review.create("코멘트", 5, 0L, movie, writer));
        reviewId = review.getId();
    }

    @Test
    @DisplayName("1000명이 동시에 좋아요를 누르면 Like row가 1000개 저장되고 Review.likeCount도 1000이다")
    void addLike_concurrent_increaseLikeCount() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final Member user = memberRepository.save(
                    Member.signUp("user" + i + "@test.com", "pw", "user" + i)
            );

            executor.submit(() -> {
                try {
                    likeService.addLike(user.getId(), reviewId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        double sec = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println("소요 시간: " + sec + "초");

        // ✅ DB에 저장된 Like row 개수 확인
        long savedCount = likeRepository.count();
        System.out.println("저장된 Like row 개수 = " + savedCount);

        // ✅ Review.likeCount fresh 조회
        Long likeCount = reviewRepository.getLikeCount(reviewId);
        System.out.println("Review.likeCount = " + likeCount);

        // 검증
        assertThat(savedCount).isEqualTo(threadCount);
        assertThat(likeCount).isEqualTo(threadCount);
    }
}

