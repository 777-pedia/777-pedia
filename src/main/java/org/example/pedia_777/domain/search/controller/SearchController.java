package org.example.pedia_777.domain.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessMessage;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.dto.Response;
import org.example.pedia_777.domain.search.dto.response.PopularKeywordResponse;
import org.example.pedia_777.domain.search.dto.response.SearchMovieResponse;
import org.example.pedia_777.domain.search.service.PopularKeywordService;
import org.example.pedia_777.domain.search.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final PopularKeywordService popularKeywordService;

    // v1: DB 조회
    @GetMapping("/api/v1/search")
    public Response<PageResponse<SearchMovieResponse>> searchMovies(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        popularKeywordService.incrementSearchKeyword(keyword);
        return Response.of(SuccessMessage.REQUEST_SUCCESS, searchService.searchMovies(keyword, pageable));
    }

    // v2: Redis 적용
    @GetMapping("/api/v2/search")
    public Response<PageResponse<SearchMovieResponse>> searchMoviesWithLocalCache(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        popularKeywordService.incrementSearchKeyword(keyword);
        return Response.of(SuccessMessage.REQUEST_SUCCESS, searchService.searchMoviesWithCache(keyword, pageable));
    }

    @GetMapping("/api/v1/search/popular")
    public Response<List<PopularKeywordResponse>> getPopularKeywords() {

        return Response.of(SuccessMessage.REQUEST_SUCCESS, popularKeywordService.getPopularKeywordsOfPreviousHour());
    }
}
