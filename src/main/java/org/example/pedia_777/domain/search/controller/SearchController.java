package org.example.pedia_777.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.search.dto.respoonse.MovieSearchResponse;
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

    @GetMapping("/api/v1/search")
    public ResponseEntity<GlobalApiResponse<PageResponse<MovieSearchResponse>>> searchMovies(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS,
                searchService.searchMovies(keyword, pageable));
    }
}
