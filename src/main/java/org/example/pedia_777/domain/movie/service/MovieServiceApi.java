package org.example.pedia_777.domain.movie.service;

import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.search.dto.respoonse.MovieSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieServiceApi {

    Movie findMovieById(Long movieId);

    Page<MovieSearchResponse> searchMovies(String keyword, Pageable pageable);
}
