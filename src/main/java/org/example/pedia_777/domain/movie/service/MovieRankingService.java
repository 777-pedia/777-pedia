package org.example.pedia_777.domain.movie.service;

import java.time.Duration;
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
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.domain.movie.dto.MovieRankResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.entity.MovieRanking;
import org.example.pedia_777.domain.movie.entity.RankingPeriod;
import org.example.pedia_777.domain.movie.repository.MovieRankingRepository;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieRankingService {

    private final RedisTemplate<String, String> redisTemplate;
    private final MovieRepository movieRepository;
    private final MovieRankingRepository movieRankingRepository;

    // 인기도 점수 증가 (리뷰 1점, 좋아용 0.5점)
    public void addMovieScore(Long movieId, double score) {

        LocalDate today = LocalDate.now();
        String dailyKey = getDailyKey(today);
        String weeklyKey = getWeeklyKey(today);

        // 오늘 날짜의 일간/주간 Sorted Set에 모두 점수 추가
        redisTemplate.opsForZSet().incrementScore(dailyKey, String.valueOf(movieId), score);
        redisTemplate.opsForZSet().incrementScore(weeklyKey, String.valueOf(movieId), score);

        redisTemplate.expire(dailyKey, Duration.ofHours(48));
        redisTemplate.expire(weeklyKey, Duration.ofDays(8));
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
    @Cacheable(cacheNames = CacheType.NAME_LIST_DAILY_TOP_10_MOVIES,
            key = "'daily:' + T(java.time.LocalDate).now().minusDays(1)")
    public List<MovieRankResponse> getDailyTop10() {

        String yesterdaysKey = getDailyKey(LocalDate.now().minusDays(1));
        Set<String> topMovieIds = redisTemplate.opsForZSet().reverseRange(yesterdaysKey, 0, 9);
        return convertToDtoWithRank(topMovieIds);
    }


    // 지난주 TOP 10 영화 ID 목록 조회
    @Cacheable(cacheNames = CacheType.NAME_LIST_WEEKLY_TOP_10_MOVIES,
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

    // 어제 Top 10에 포함되는지 여부
    public boolean isDailyTop10(Long movieId) {

        String yesterdaysKey = getDailyKey(LocalDate.now().minusDays(1));
        Long rank = redisTemplate.opsForZSet().reverseRank(yesterdaysKey, String.valueOf(movieId));
        return rank != null && rank < 10;
    }

    // 지난주 Top 10에 포함되는지 여부
    public boolean isWeeklyTop10(Long movieId) {

        String lastWeeksKey = getWeeklyKey(LocalDate.now().minusWeeks(1));
        Long rank = redisTemplate.opsForZSet().reverseRank(lastWeeksKey, String.valueOf(movieId));
        return rank != null && rank < 10;
    }

    // 특정 기간(일간/주간)의 영화 랭킹 백업
    @Transactional
    public void backupMovieRankings(LocalDate targetDate, RankingPeriod period) {

        String sourceKey;
        LocalDate rankingDate;

        // 랭킹 종류에 따라 Redis 키와 저장될 날짜 결정
        switch (period) {
            case DAILY -> {
                sourceKey = getDailyKey(targetDate);
                rankingDate = targetDate;
            }
            case WEEKLY -> {
                sourceKey = getWeeklyKey(targetDate);
                // 주간 랭킹은 해당 주의 시작일(월요일)을 기준으로 저장
                rankingDate = targetDate.with(WeekFields.of(Locale.KOREA).dayOfWeek(), 1);
            }
            default -> {
                log.error("지원하지 않는 랭킹 주기입니다: {}", period);
                return;
            }
        }

        log.info("[backupMovieRankings] '{}' 랭킹 데이터 백업 시작 (Redis Key: {})", period, sourceKey);

        // sorted set에서 Top 100 데이터 조회, 서비스는 Top 10이지만 내부 자료는 100까지 보관
        Set<TypedTuple<String>> topMovies = redisTemplate.opsForZSet()
                .reverseRangeWithScores(sourceKey, 0, 99);

        if (topMovies == null || topMovies.isEmpty()) {
            log.warn("[backupMovieRankings] 백업할 데이터가 없습니다.");
            return;
        }

        List<MovieRanking> backupData = new ArrayList<>();
        int rank = 1;
        for (TypedTuple<String> tuple : topMovies) {
            backupData.add(
                    MovieRanking.of(period, rankingDate, rank++, Long.valueOf(tuple.getValue()), tuple.getScore()));
        }

        movieRankingRepository.saveAll(backupData);
        log.info("[backupMovieRankings] {} 랭킹 데이터 {}건 백업 완료.", period, backupData.size());
    }
}
