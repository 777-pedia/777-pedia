package org.example.pedia_777.common.config;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    MOVIE_SEARCH("movieSearch", Duration.ofMinutes(10)), // 10분
    POPULAR_KEYWORDS("popularKeywords", Duration.ofMinutes(120)); // 2시간

    public static final String MOVIE_SEARCH_NAME = "movieSearch";
    public static final String POPULAR_KEYWORDS_NAME = "popularKeywords";

    private final String cacheName;
    private final Duration ttl;
}
