package org.example.pedia_777.domain.movie.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.example.pedia_777.common.config.JwtAuthenticationFilter;
import org.example.pedia_777.common.config.JwtUtil;
import org.example.pedia_777.domain.movie.dto.MovieRankResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.entity.MovieRanking;
import org.example.pedia_777.domain.movie.entity.RankingPeriod;
import org.example.pedia_777.domain.movie.repository.MovieRankingRepository;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class MovieRankingServiceTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @MockitoBean
    JwtUtil jwtUtil;
    @MockitoBean
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private MovieRankingService movieRankingService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieRankingRepository movieRankingRepository;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {

        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();

        if (movieRepository.existsById(1L) || movieRankingRepository.count() > 0) {
            movieRankingRepository.deleteAllInBatch();
            movieRepository.deleteAllInBatch();
        }
    }

    @Test
    @DisplayName("영화 ID에 점수를 더하면 sorted set의 점수가 증가한다")
    void addMovieScore_success() {

        // given
        Long movieId = 101L;
        LocalDate today = LocalDate.now();
        String dailyKey = movieRankingService.getDailyKey(today);
        String weeklyKey = movieRankingService.getWeeklyKey(today);

        // when
        movieRankingService.addMovieScore(movieId, 1.0);
        movieRankingService.addMovieScore(movieId, 0.5);

        // then
        Double dailyScore = redisTemplate.opsForZSet().score(dailyKey, "101");
        Double weeklyScore = redisTemplate.opsForZSet().score(weeklyKey, "101");

        assertThat(dailyScore).isEqualTo(1.5);
        assertThat(weeklyScore).isEqualTo(1.5);
    }

    @Test
    @DisplayName("어제 Top 10 영화 정보를 순위와 함께 반환한다")
    void getDailyTop10_success() {

        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdaysKey = movieRankingService.getDailyKey(yesterday);

        Movie movie1 = movieRepository.save(
                Movie.of("감독", "영화 1", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster99.jpg"));
        Movie movie2 = movieRepository.save(
                Movie.of("감독", "영화 2", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster101.jpg"));

        redisTemplate.opsForZSet().add(yesterdaysKey, String.valueOf(movie1.getId()), 10.0); // 2위
        redisTemplate.opsForZSet().add(yesterdaysKey, String.valueOf(movie2.getId()), 20.0); // 1위

        // when
        List<MovieRankResponse> result = movieRankingService.getDailyTop10();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).movieId()).isEqualTo(movie2.getId());
        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).movieId()).isEqualTo(movie1.getId());
    }

    @Test
    @DisplayName("지난주 Top 10 영화 정보를 순위와 함께 반환한다")
    void getWeeklyTop10_success() {

        // given
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        String lastWeeksKey = movieRankingService.getWeeklyKey(lastWeek);

        Movie movie1 = movieRepository.save(
                Movie.of("감독", "영화 1", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster99.jpg"));
        Movie movie2 = movieRepository.save(
                Movie.of("감독", "영화 2", "배우", "장르", LocalDate.now(), 90, "한국", "줄거리", "/poster101.jpg"));

        redisTemplate.opsForZSet().add(lastWeeksKey, String.valueOf(movie1.getId()), 30.0); // 1위
        redisTemplate.opsForZSet().add(lastWeeksKey, String.valueOf(movie2.getId()), 10.0); // 2위

        // when
        List<MovieRankResponse> result = movieRankingService.getWeeklyTop10();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).movieId()).isEqualTo(movie1.getId());

        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).movieId()).isEqualTo(movie2.getId());
    }

    @Test
    @DisplayName("랭킹 데이터가 DB에 백업된다")
    void backupMovieRankings_Daily_success() {

        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dailyKey = movieRankingService.getDailyKey(yesterday);

        redisTemplate.opsForZSet().add(dailyKey, "101", 20.0); // 1위
        redisTemplate.opsForZSet().add(dailyKey, "102", 10.0); // 2위

        // when
        movieRankingService.backupMovieRankings(yesterday, RankingPeriod.DAILY);

        // then
        List<MovieRanking> results = movieRankingRepository.findAll();
        assertThat(results).hasSize(2);

        MovieRanking rank1 = results.stream().filter(r -> r.getMovieId().equals(101L)).findFirst().get();
        assertThat(rank1.getRanking()).isEqualTo(1);
        assertThat(rank1.getScore()).isEqualTo(20.0);
        assertThat(rank1.getRankingType()).isEqualTo(RankingPeriod.DAILY);
        assertThat(rank1.getRankingDate()).isEqualTo(yesterday);
    }
}
