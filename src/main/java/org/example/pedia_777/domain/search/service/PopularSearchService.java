package org.example.pedia_777.domain.search.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.domain.search.dto.response.PopularKeywordResponse;
import org.example.pedia_777.domain.search.entity.PopularType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PopularSearchService {

    private static final String KEY_PREFIX = "popularKeywords:";  // Sorted Set의 Key
    private final RedisTemplate<String, String> redisTemplate;


    // ZINCRBY을 이용하여 검색어의 점수를 1 증가시킴, 검색 함수에 사용
    public void incrementSearchKeyword(String keyword) {

        String currentKey = getCurrentHourKey();
        redisTemplate.opsForZSet().incrementScore(currentKey, keyword, 1);
    }

    // 이전 시간대(정각 ~ 정각 사이)의 인기 검색어 10개 조회
    @Cacheable(cacheNames = CacheType.NAME_POPULAR_KEYWORDS, cacheManager = "redisCacheManager",
            key = "#root.methodName + ':' + #root.target.getPreviousHourKey()")
    public List<PopularKeywordResponse> getPopularKeywordsOfPreviousHour() {

        log.debug("[PopularSearchService] 인기 검색어 목록을 Redis에서 조회합니다.");

        // 이전 시간대의 Key를 가져와서 ZREVRANGE 실행
        String previousKey = getPreviousHourKey();
        Set<String> topKeywords = redisTemplate.opsForZSet().reverseRange(previousKey, 0, 9);

        log.debug("[PopularSearchService] 인기 검색어 목록: {}", Objects.requireNonNull(topKeywords));

        List<String> keywordList = new ArrayList<>(topKeywords);
        return IntStream.range(0, keywordList.size())
                .mapToObj(i -> PopularKeywordResponse.of(i + 1, keywordList.get(i)))
                .collect(Collectors.toList());
    }

    // 특정 키워드가 현재 또는 이전 인기 검색어에 포함되는지 확인
    public PopularType checkPopularity(String keyword) {

        String currentKey = getCurrentHourKey();
        Set<String> currentPopularKeywords = redisTemplate.opsForZSet().reverseRange(currentKey, 0, 9);
        if (currentPopularKeywords != null && currentPopularKeywords.contains(keyword)) {
            return PopularType.CURRENT;
        }

        String previousKey = getPreviousHourKey();
        Long previousRank = redisTemplate.opsForZSet().reverseRank(previousKey, keyword);
        if (previousRank != null && previousRank < 10) {
            return PopularType.PREVIOUS;
        }

        return PopularType.NONE;
    }

    // 현재 시간을 기준으로 Key를 생성 (e.g.: 4시 1분, 4시 30분 -> 4시 "popularKeywords:2025092804")
    private String getCurrentHourKey() {

        return KEY_PREFIX + LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }

    // 조회를 위해 이전 시간대를 기준으로 Key를 생성 (e.g.: 4시 1분이면 "popularKeywords:2025092803" 반환)
    public String getPreviousHourKey() {

        // 현재 시간을 시간 단위로 절삭 후, 1시간을 뺌
        return KEY_PREFIX + LocalDateTime.now()
                .truncatedTo(ChronoUnit.HOURS)
                .minusHours(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
    }
}
