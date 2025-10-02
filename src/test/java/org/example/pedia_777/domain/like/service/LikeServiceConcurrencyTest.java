package org.example.pedia_777.domain.like.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.pedia_777.domain.favorite.repository.FavoriteRepository;
import org.example.pedia_777.domain.like.entity.Like;
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

@ActiveProfiles("test")
@SpringBootTest
class LikeServiceConcurrencyTest {
    private final List<Member> likeMembers = new ArrayList<>();

    @Autowired
    private LikeService likeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;
    private Review testReview;
    private Member reviewAuthor;

    @BeforeEach
    void setUp() {
        // 이전 데이터 정리
        likeRepository.deleteAllInBatch();
        favoriteRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        movieRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();

        likeMembers.clear();

        // 1. 리뷰 작성자 생성
        reviewAuthor = memberRepository.save(Member.signUp("author@author.com", "author", "author"));

        // 2. 테스트용 영화 생성
        Movie movie = movieRepository.save(Movie.of(
                "테스트 감독",
                "테스트 영화",
                "배우1, 배우2",
                "장르1, 장르2",
                LocalDate.now(),
                120,
                "테스트 국가",
                "설명",
                "https://image.com/150"
        ));

        // 3. 리뷰 생성 및 저장 (정적 팩토리 사용)
        testReview = Review.create("좋아요 동시성 테스트 리뷰", 4.5, 0L, movie, reviewAuthor);
        testReview = reviewRepository.save(testReview);

        // 4. 좋아요 누를 100명의 멤버 생성
        for (int i = 0; i < 100; i++) {
            String email = "test" + i + "@test.com";
            likeMembers.add(memberRepository.save(Member.signUp(email, "test" + i, "tester" + i)));
        }
        System.out.println("sdfs");
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 리뷰에 좋아요를 눌렀을 때 likeCount가 정확히 100이 되어야 한다")
    void addLikeConcurrencyTest() throws InterruptedException {
        int totalThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalThreads);

        for (int i = 0; i < totalThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    likeService.addLike(likeMembers.get(index).getId(), testReview.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Review result = reviewRepository.findById(testReview.getId()).orElseThrow();
        System.out.println("최종 좋아요 수: " + result.getLikeCount());
        assertEquals(100, result.getLikeCount());
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 리뷰에 좋아요를 취소했을 때 likeCount가 정확히 0이 되어야 한다")
    void cancelLikeConcurrencyTest() throws InterruptedException {

        // given
        for (Member member : likeMembers) {
            likeRepository.save(Like.of(member, testReview));
        }
        for (int i = 0; i < 100; i++) {
            testReview.incrementLikeCount();
        }
        reviewRepository.save(testReview);

        // when
        int totalThreads = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalThreads);

        for (int i = 0; i < totalThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    likeService.cancelLike(likeMembers.get(index).getId(), testReview.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        Review result = reviewRepository.findById(testReview.getId()).orElseThrow();
        System.out.println("최종 좋아요 수: " + result.getLikeCount());
        assertEquals(0, result.getLikeCount());
    }
}