package org.example.pedia_777.common.config;

import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    
    MOVIE_SEARCH_CURRENT_POPULAR("movieSearchCurrentPopular", Duration.ofMinutes(5)), // 5분
    MOVIE_SEARCH_PREV_POPULAR("movieSearchPrevPopular", Duration.ofHours(2)), // 2시간
    LIST_POPULAR_KEYWORDS("popularKeywords", Duration.ofHours(1)), // 1시간
    LIST_DAILY_TOP_10_MOVIES("dailyTop10Movies", Duration.ofHours(24)), // 24시간
    LIST_WEEKLY_TOP_10_MOVIES("weeklyTop10Movies", Duration.ofDays(7)), // 7일
    MOVIE_DETAILS_TOP_10("movieDetailsTop10", Duration.ofDays(8)); // 8일

    public static final String NAME_MOVIE_SEARCH_CURRENT_POPULAR = "movieSearchCurrentPopular";
    public static final String NAME_MOVIE_SEARCH_PREV_POPULAR = "movieSearchPrevPopular";
    public static final String NAME_LIST_POPULAR_KEYWORDS = "popularKeywords";
    public static final String NAME_LIST_DAILY_TOP_10_MOVIES = "dailyTop10Movies";
    public static final String NAME_LIST_WEEKLY_TOP_10_MOVIES = "weeklyTop10Movies";
    public static final String NAME_MOVIE_DETAILS_TOP_10 = "movieDetailsTop10";

    private final String cacheName;
    private final Duration ttl;
}
