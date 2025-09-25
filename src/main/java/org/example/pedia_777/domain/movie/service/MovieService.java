package org.example.pedia_777.domain.movie.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.movie.entity.Movies;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MovieService implements MovieServiceApi {

    private final MovieRepository movieRepository;

    @Override
    public Movies findMovieById(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));
    }
}
