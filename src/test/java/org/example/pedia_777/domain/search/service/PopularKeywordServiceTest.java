package org.example.pedia_777.domain.search.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.example.pedia_777.domain.search.dto.response.PopularKeywordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PopularKeywordServiceTest {

    // @Container와 @ServiceConnection으로 사용될 컨테이너 정의
    @Container
    @ServiceConnection
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");

    @Container
    @ServiceConnection
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @Autowired
    private PopularKeywordService popularKeywordService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushDb();
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

    private String getCurrentHourKey() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        return "popularKeywords:" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }

    private String getPreviousHourKey() {
        LocalDateTime previousHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).minusHours(1);
        return "popularKeywords:" + previousHour.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }
}