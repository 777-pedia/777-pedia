package org.example.pedia_777.common.config;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    MOVIE_SEARCH("movieSearch", Duration.ofMinutes(10)), // 10분
    POPULAR_KEYWORDS("popularKeywords", Duration.ofMinutes(120)), // 2시간
    IS_POPULAR_KEYWORD("isPopularKeyword", Duration.ofMinutes(1)); // 1분

    public static final String MOVIE_SEARCH_NAME = "movieSearch";
    public static final String POPULAR_KEYWORDS_NAME = "popularKeywords";
    public static final String IS_POPULAR_KEYWORD_NAME = "isPopularKeyword";

    private final String cacheName;
    private final Duration ttl;
}
