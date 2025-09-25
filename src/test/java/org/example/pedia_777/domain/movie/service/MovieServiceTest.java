package org.example.pedia_777.domain.movie.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    void 존재하지_않는_영화_ID로_조회를_하면_예외를_반환한다() {

        // given
        Long nonExistentMovieId = 999L;
        given(movieRepository.findById(nonExistentMovieId)).willReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            movieService.findMovieById(nonExistentMovieId);
        });

        // then
        assertEquals(ErrorCode.MOVIE_NOT_FOUND, exception.getErrorCode());
        verify(movieRepository).findById(nonExistentMovieId);
    }
}