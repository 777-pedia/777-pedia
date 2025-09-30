package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.search.dto.response.SearchMovieProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieQueryRepository {

    Page<SearchMovieProjection> searchMovies(String keyword, Pageable pageable);

}
