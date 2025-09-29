package org.example.pedia_777.domain.search.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.search.dto.response.MovieSearchResponse;
import org.example.pedia_777.domain.search.dto.response.PopularKeywordResponse;
import org.example.pedia_777.domain.search.service.PopularSearchService;
import org.example.pedia_777.domain.search.service.SearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final PopularSearchService popularSearchService;

    @GetMapping("/api/v1/search")
    public ResponseEntity<GlobalApiResponse<PageResponse<MovieSearchResponse>>> searchMovies(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS,
                searchService.searchMovies(keyword, pageable));
    }

    @GetMapping("/api/v2/search")
    public ResponseEntity<GlobalApiResponse<PageResponse<MovieSearchResponse>>> searchMoviesWithLocalCache(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS,
                searchService.searchMoviesWithLocalCache(keyword, pageable));
    }

    @GetMapping("/api/v1/search/popular")
    public ResponseEntity<GlobalApiResponse<List<PopularKeywordResponse>>> getPopularKeywords() {

        // TODO 현재는 UI 기획대로 바로 이전 시간대 데이터만 조회하도록 구현, 시간대 선택 가능하도록 RequestParam 적용 가능
        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS,
                popularSearchService.getPopularKeywordsOfPreviousHour());
    }
}
