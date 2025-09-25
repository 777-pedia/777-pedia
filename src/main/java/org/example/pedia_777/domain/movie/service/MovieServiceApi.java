package org.example.pedia_777.domain.movie.service;

import org.example.pedia_777.domain.movie.entity.Movies;

public interface MovieServiceApi {

    Movies findMovieById(Long movieId);
}
