package org.example.pedia_777.common.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    MOVIE_SEARCH("movieSearch", 10, 5000); // 10분, 5,000개

    public static final String MOVIE_SEARCH_NAME = "movieSearch";

    private final String cacheName;
    private final long expiredAfterWrite;
    private final int maximumSize;
}
