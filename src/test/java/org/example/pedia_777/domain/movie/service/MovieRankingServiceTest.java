package org.example.pedia_777.domain.movie.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.example.pedia_777.domain.movie.dto.MovieRankResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MovieRankingServiceTest {

    @InjectMocks
    private MovieRankingService movieRankingService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @BeforeEach
    void setUp() {
        // 모든 Mock 객체의 상호작용을 초기화하기 전에,
        // redisTemplate.opsForZSet()이 호출되면 zSetOperations를 반환하도록 미리 설정합
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    @DisplayName("getDailyTop10 호출 시 어제 날짜 키로 Redis를 조회하고 순위가 매겨진 리스트를 반환한다")
    void getDailyTop10_success_return_list() {

        // given
        Set<String> topMovieIdsFromRedis = new LinkedHashSet<>(List.of("101", "105", "99"));
        List<Long> movieIdsToFetch = List.of(101L, 105L, 99L);

        String yesterdaysKey = movieRankingService.getDailyKey(LocalDate.now().minusDays(1));
        when(zSetOperations.reverseRange(yesterdaysKey, 0, 9)).thenReturn(topMovieIdsFromRedis);

        Movie movie99 = Movie.of("감독", "영화 99", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster99.jpg");
        Movie movie101 = Movie.of("감독", "영화 101", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster101.jpg");
        Movie movie105 = Movie.of("감독", "영화 105", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster105.jpg");

        ReflectionTestUtils.setField(movie99, "id", 99L);
        ReflectionTestUtils.setField(movie101, "id", 101L);
        ReflectionTestUtils.setField(movie105, "id", 105L);

        when(movieRepository.findAllById(movieIdsToFetch)).thenReturn(List.of(movie105, movie99, movie101));

        // when
        List<MovieRankResponse> result = movieRankingService.getDailyTop10();

        // then
        assertThat(result).hasSize(3);

        // 순위 검증
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).title()).isEqualTo("영화 101");

        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).title()).isEqualTo("영화 105");

        assertThat(result.get(2).rank()).isEqualTo(3);
        assertThat(result.get(2).title()).isEqualTo("영화 99");

        verify(zSetOperations, times(1)).reverseRange(yesterdaysKey, 0, 9);
        verify(movieRepository, times(1)).findAllById(movieIdsToFetch);
    }

    @Test
    @DisplayName("Redis에서 조회된 ID가 없을 경우 빈 리스트를 반환한다")
    void getDailyTop10_success_empty_list() {

        // given
        String yesterdaysKey = movieRankingService.getDailyKey(LocalDate.now().minusDays(1));
        when(zSetOperations.reverseRange(yesterdaysKey, 0, 9)).thenReturn(Collections.emptySet());

        // when
        List<MovieRankResponse> result = movieRankingService.getDailyTop10();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}