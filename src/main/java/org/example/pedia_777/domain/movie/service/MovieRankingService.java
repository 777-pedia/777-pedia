package org.example.pedia_777.domain.movie.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.domain.movie.dto.MovieRankResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieRankingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MovieRepository movieRepository;


    // 인기도 점수 증가 (리뷰 1점, 좋아용 0.5점)
    public void addMovieScore(Long movieId, double score) {

        LocalDate today = LocalDate.now();

        // 오늘 날짜의 일간/주간 Sorted Set에 모두 점수 추가
        redisTemplate.opsForZSet().incrementScore(getDailyKey(today), String.valueOf(movieId), score);
        redisTemplate.opsForZSet().incrementScore(getWeeklyKey(today), String.valueOf(movieId), score);
    }

    // "movie_scores:daily:20250930"
    public String getDailyKey(LocalDate date) {
        return "movie_scores:daily:" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    // "movie_scores:weekly:2025_W40" (40번째 주)
    public String getWeeklyKey(LocalDate date) {

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        int year = date.getYear();
        return String.format("movie_scores:weekly:%d_W%02d", year, weekNumber);
    }

    // 어제 TOP 10 영화 ID 목록을 조회
    @Cacheable(cacheNames = CacheType.NAME_DAILY_TOP_10_MOVIES,
            key = "'daily:' + T(java.time.LocalDate).now().minusDays(1)")
    public List<MovieRankResponse> getDailyTop10() {

        String yesterdaysKey = getDailyKey(LocalDate.now().minusDays(1));
        Set<String> topMovieIds = redisTemplate.opsForZSet().reverseRange(yesterdaysKey, 0, 9);
        return convertToDtoWithRank(topMovieIds);
    }


    // 지난주 TOP 10 영화 ID 목록 조회
    @Cacheable(cacheNames = CacheType.NAME_WEEKLY_TOP_10_MOVIES,
            key = "'weekly:' + #root.target.getWeeklyKey(T(java.time.LocalDate).now().minusWeeks(1))")
    public List<MovieRankResponse> getWeeklyTop10() {

        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        Set<String> topMovieIds = redisTemplate.opsForZSet().reverseRange(getWeeklyKey(lastWeek), 0, 9);
        return convertToDtoWithRank(topMovieIds);
    }

    private List<MovieRankResponse> convertToDtoWithRank(Set<String> topMovieIds) {

        if (topMovieIds == null || topMovieIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 조회
        List<Long> movieIds = topMovieIds.stream().map(Long::valueOf).toList();
        List<Movie> movies = movieRepository.findAllById(movieIds);

        // 매핑
        Map<Long, Movie> movieMap = movies.stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));

        // 정렬
        List<Movie> sortedMovies = movieIds.stream()
                .map(movieMap::get)
                .filter(Objects::nonNull)
                .toList();

        // 정렬된 리스트의 인덱스를 사용하여 Rank 부여 및 DTO 생성
        return IntStream.range(0, sortedMovies.size())
                .mapToObj(i -> MovieRankResponse.of(i + 1, sortedMovies.get(i)))
                .collect(Collectors.toList());

    }
}
