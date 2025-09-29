package org.example.pedia_777.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.search.dto.response.MovieSearchResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {

    private final MovieService movieService;
    private final PopularSearchService popularSearchService;

    public PageResponse<MovieSearchResponse> searchMovies(String keyword, Pageable pageable) {

        popularSearchService.incrementSearchKeyword(keyword);
        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }

    @Cacheable(
            cacheNames = CacheType.MOVIE_SEARCH_NAME, cacheManager = "redisCacheManager",
            key = "'search:' + #keyword.trim().toLowerCase() + ':' + #pageable.pageNumber + ':' + #pageable.pageSize",
            condition = "#keyword != null && #keyword.trim().length() <= 30 && #pageable.pageSize <= 30 && #pageable.pageNumber <= 3",
            unless = "#result == null || #result.content.isEmpty()")
    public PageResponse<MovieSearchResponse> searchMoviesWithCache(String keyword, Pageable pageable) {

        log.debug("[SearchService] searchMoviesWithCache Cache miss: keyword: {}, pageSize: {}, pageNumber: {}",
                keyword, pageable.getPageSize(), pageable.getPageNumber());

        popularSearchService.incrementSearchKeyword(keyword);
        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }

}
