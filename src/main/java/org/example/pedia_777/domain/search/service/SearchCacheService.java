package org.example.pedia_777.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.search.dto.response.SearchMovieResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchCacheService {

    // AOP self-invocation 문제 해결을 위해 별도 서비스로 분리

    private final MovieService movieService;

    // 과거 인기 검색어의 포함된 키워드의 "결과"를 2시간 캐싱
    @Cacheable(cacheNames = CacheType.NAME_MOVIE_SEARCH_PREV_POPULAR, cacheManager = "redisCacheManager",
            key = "'previous:' + #keyword.trim().toLowerCase() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
            sync = true)
    public PageResponse<SearchMovieResponse> searchMoviesForPreviousPopularKeywords(String keyword, Pageable pageable) {

        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }

    // 현재 인기 검색어의 포함된 키워드의 "결과"를 5분 캐싱
    @Cacheable(cacheNames = CacheType.NAME_MOVIE_SEARCH_CURRENT_POPULAR, cacheManager = "redisCacheManager",
            key = "'current:' + #keyword.trim().toLowerCase() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
            sync = true)
    public PageResponse<SearchMovieResponse> searchMoviesForCurrentPopularKeywords(String keyword, Pageable pageable) {

        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }
}
