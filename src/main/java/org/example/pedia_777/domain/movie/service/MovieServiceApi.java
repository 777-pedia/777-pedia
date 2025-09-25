package org.example.pedia_777.domain.movie.service;

import org.example.pedia_777.domain.movie.entity.Movie;

public interface MovieServiceApi {

    Movie findMovieById(Long movieId);
}
