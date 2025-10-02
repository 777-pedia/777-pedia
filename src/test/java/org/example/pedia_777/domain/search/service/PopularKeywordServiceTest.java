package org.example.pedia_777.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.example.pedia_777.common.config.JwtAuthenticationFilter;
import org.example.pedia_777.common.config.JwtUtil;
import org.example.pedia_777.domain.search.dto.response.PopularKeywordResponse;
import org.example.pedia_777.domain.search.entity.PopularKeyword;
import org.example.pedia_777.domain.search.repository.PopularKeywordRepository;
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
class PopularKeywordServiceTest {

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
    private PopularKeywordService popularKeywordService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PopularKeywordRepository dailyPopularKeywordRepository;

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
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushDb();
        dailyPopularKeywordRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("검색어 카운트가 정상적으로 1씩 증가한다")
    void incrementSearchKeyword_Success() {

        // given
        String keyword = "Spring";
        String currentHourKey = getCurrentHourKey();

        // when
        popularKeywordService.incrementSearchKeyword(keyword);
        popularKeywordService.incrementSearchKeyword(keyword);
        popularKeywordService.incrementSearchKeyword(keyword);
        popularKeywordService.incrementSearchKeyword(keyword);
        popularKeywordService.incrementSearchKeyword(keyword);

        // 다른 키워드도 한 번 호출
        popularKeywordService.incrementSearchKeyword("Java");

        // then
        // Redis에 저장된 점수 검증
        Double springScore = redisTemplate.opsForZSet().score(currentHourKey, "Spring");
        Double javaScore = redisTemplate.opsForZSet().score(currentHourKey, "Java");
        Double nonExistentScore = redisTemplate.opsForZSet().score(currentHourKey, "Docker");

        assertThat(springScore).isEqualTo(5.0);
        assertThat(javaScore).isEqualTo(1.0);
        assertThat(nonExistentScore).isNull(); // 호출된 적 없는 키워드는 점수가 없음
    }

    @Test
    @DisplayName("인기 검색어 순위 조회를 하면 이전 시간대에서 score 높은 순으로 반환된다")
    void getPopularKeywords_Success() {

        // given
        // 이전 시간대의 키를 직접 생성하여 12개의 테스트 데이터를 준비
        String previousHourKey = getPreviousHourKey();

        Map<String, Double> keywordScores = Map.ofEntries(
                Map.entry("Java", 85.0),
                Map.entry("Spring", 100.0),
                Map.entry("Docker", 70.0),
                Map.entry("JPA", 90.0),
                Map.entry("QueryDSL", 75.0),
                Map.entry("MySQL", 60.0),
                Map.entry("AWS", 55.0),
                Map.entry("Redis", 95.0),
                Map.entry("Nginx", 50.0),
                Map.entry("Kotlin", 88.0),
                Map.entry("C++", 20.0),
                Map.entry("Python", 40.0)
        );

        keywordScores.forEach((keyword, score) ->
                redisTemplate.opsForZSet().add(previousHourKey, keyword, score)
        );

        // when
        List<PopularKeywordResponse> popularKeywords = popularKeywordService.getPopularKeywordsOfPreviousHour();

        // then
        assertThat(popularKeywords).hasSize(10);

        assertThat(popularKeywords).extracting(PopularKeywordResponse::rank)
                .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 높은 순 정렬 맞는지 검증
        assertThat(popularKeywords).extracting(PopularKeywordResponse::keyword)
                .containsExactly(
                        "Spring",   // 100.0
                        "Redis",    // 95.0
                        "JPA",      // 90.0
                        "Kotlin",   // 88.0
                        "Java",     // 85.0
                        "QueryDSL", // 75.0
                        "Docker",   // 70.0
                        "MySQL",    // 60.0
                        "AWS",      // 55.0
                        "Nginx"     // 50.0
                );
    }

    @Test
    @DisplayName("어제자 시간대별 인기 검색어 데이터가 일일 랭킹으로 정확히 합산되어 DB에 백업된다")
    void backupDailyPopularKeywords_Success() {

        // given
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayFmt = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 01시 데이터: Spring(5), Java(3)
        redisTemplate.opsForZSet().add(String.format("popularKeywords:%s01", yesterdayFmt), "Spring", 5);
        redisTemplate.opsForZSet().add(String.format("popularKeywords:%s01", yesterdayFmt), "Java", 3);

        // 02시 데이터: Spring(10), Docker(8)
        redisTemplate.opsForZSet().add(String.format("popularKeywords:%s02", yesterdayFmt), "Spring", 10);
        redisTemplate.opsForZSet().add(String.format("popularKeywords:%s02", yesterdayFmt), "Docker", 8);

        // (00시, 03시 ~ 23시 데이터는 없다고 가정)

        // when
        popularKeywordService.backupDailyPopularKeywords(yesterday);

        // then
        List<PopularKeyword> results = dailyPopularKeywordRepository.findAll();

        assertThat(results).hasSize(3);

        // 1위: Spring (5 + 10 = 15점)
        PopularKeyword rank1 = results.get(0);
        assertThat(rank1.getRanking()).isEqualTo(1);
        assertThat(rank1.getKeyword()).isEqualTo("Spring");
        assertThat(rank1.getScore()).isEqualTo(15);

        // 2위: Docker (8점)
        PopularKeyword rank2 = results.get(1);
        assertThat(rank2.getRanking()).isEqualTo(2);
        assertThat(rank2.getKeyword()).isEqualTo("Docker");
        assertThat(rank2.getScore()).isEqualTo(8);

        // 3위: Java (3점)
        PopularKeyword rank3 = results.get(2);
        assertThat(rank3.getRanking()).isEqualTo(3);
        assertThat(rank3.getKeyword()).isEqualTo("Java");
        assertThat(rank3.getScore()).isEqualTo(3);
    }

    private String getCurrentHourKey() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        return "popularKeywords:" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }

    private String getPreviousHourKey() {
        LocalDateTime previousHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(1);
        return "popularKeywords:" + previousHour.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }
}