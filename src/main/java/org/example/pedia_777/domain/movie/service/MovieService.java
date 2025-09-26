package org.example.pedia_777.domain.movie.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.movie.code.MovieErrorCode;
import org.example.pedia_777.domain.movie.dto.MovieDetailResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MovieService implements MovieServiceApi {

    private final MovieRepository movieRepository;

    public MovieDetailResponse getMovieDetails(Long movieId) {

        Movie movie = findMovieById(movieId);
        return MovieDetailResponse.from(movie);
    }


    @Override
    public Movie findMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));
    }
}
