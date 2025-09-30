package org.example.pedia_777.domain.movie.service;

import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.search.dto.response.SearchMovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieServiceApi {

    Movie getMovieEntity(Long movieId);

    Page<SearchMovieResponse> searchMovies(String keyword, Pageable pageable);
}
