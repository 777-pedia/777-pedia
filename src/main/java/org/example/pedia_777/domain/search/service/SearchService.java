package org.example.pedia_777.domain.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.search.dto.response.MovieSearchResponse;
import org.example.pedia_777.domain.search.entity.PopularType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {

    private final MovieService movieService;
    private final PopularSearchService popularSearchService;
    private final SearchCacheService searchCacheService;

    public PageResponse<MovieSearchResponse> searchMovies(String keyword, Pageable pageable) {

        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }

    public PageResponse<MovieSearchResponse> searchMoviesWithCache(String keyword, Pageable pageable) {

        PopularType popularType = popularSearchService.checkPopularity(keyword);

        log.debug("[SearchService] PopularType: {}, 검색 실행 keyword: {}", popularType, keyword);

        // 종류에 따라 다른 캐시 메서드 호출
        if (popularType == PopularType.PREVIOUS) {
            return searchCacheService.searchMoviesForPreviousPopularKeywords(keyword, pageable);
        } else if (popularType == PopularType.CURRENT) {
            return searchCacheService.searchMoviesForCurrentPopularKeywords(keyword, pageable);
        } else {
            return searchMoviesDirectly(keyword, pageable);
        }
    }

    private PageResponse<MovieSearchResponse> searchMoviesDirectly(String keyword, Pageable pageable) {

        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }
}
