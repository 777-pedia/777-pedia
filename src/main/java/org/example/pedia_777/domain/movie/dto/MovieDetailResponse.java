package org.example.pedia_777.domain.movie.dto;

import java.time.LocalDateTime;
import org.example.pedia_777.domain.movie.entity.Movie;

public record MovieDetailResponse(
        Long id,
        String title,
        String director,
        String actors,
        String genres,
        LocalDateTime releaseDate,
        Long runtime,
        String country,
        String overview,
        String posterUrl
) {
    public static MovieDetailResponse from(Movie movie) {
        return new MovieDetailResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getActors(),
                movie.getGenres(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getCountry(),
                movie.getOverview(),
                movie.getPosterUrl()
        );
    }

}
