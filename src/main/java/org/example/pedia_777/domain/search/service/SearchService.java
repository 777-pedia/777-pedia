package org.example.pedia_777.domain.search.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.example.pedia_777.domain.search.dto.response.MovieSearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final MovieService movieService;

    public PageResponse<MovieSearchResponse> searchMovies(String keyword, Pageable pageable) {

        return PageResponse.from(movieService.searchMovies(keyword, pageable));
    }
}
