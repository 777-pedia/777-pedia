package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.search.dto.response.MovieSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRepositoryCustom {

    Page<MovieSearchProjection> searchMovies(String keyword, Pageable pageable);

}
