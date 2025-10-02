package org.example.pedia_777.domain.movie.service;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.config.CacheType;
import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.movie.dto.MovieDetailResponse;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.movie.error.MovieErrorCode;
import org.example.pedia_777.domain.movie.repository.MovieRepository;
import org.example.pedia_777.domain.search.dto.response.SearchMovieResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MovieService implements MovieServiceApi {

    private final MovieRepository movieRepository;

    @Cacheable(
            cacheNames = CacheType.NAME_MOVIE_DETAILS_TOP_10,
            cacheManager = "redisCacheManager",
            key = "#movieId",
            condition = "@movieRankingService.isDailyTop10(#movieId) or @movieRankingService.isWeeklyTop10(#movieId)",
            sync = true
    )
    public MovieDetailResponse getMovieDetails(Long movieId) {

        return MovieDetailResponse.from(getMovieEntity(movieId));
    }

    // 다른 서비스에서 Movie 엔티티가 필요할 때 겸용하는 메서드
    @Override
    public Movie getMovieEntity(Long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new BusinessException(MovieErrorCode.MOVIE_NOT_FOUND));
    }

    @Override
    public Page<SearchMovieResponse> searchMovies(String keyword, Pageable pageable) {
        return movieRepository.searchMovies(keyword, pageable)
                .map(SearchMovieResponse::from);
    }
}
