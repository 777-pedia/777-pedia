package org.example.pedia_777.common.config;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    MOVIE_SEARCH_CURRENT_POPULAR("movieSearchCurrentPopular", Duration.ofMinutes(5)), // 5분
    MOVIE_SEARCH_PREV_POPULAR("movieSearchPrevPopular", Duration.ofMinutes(120)), // 2시간
    POPULAR_KEYWORDS("popularKeywords", Duration.ofHours(26)); // 26시간

    public static final String NAME_MOVIE_SEARCH_CURRENT_POPULAR = "movieSearchCurrentPopular";
    public static final String NAME_MOVIE_SEARCH_PREV_POPULAR = "movieSearchPrevPopular";
    public static final String NAME_POPULAR_KEYWORDS = "popularKeywords";

    private final String cacheName;
    private final Duration ttl;
}
