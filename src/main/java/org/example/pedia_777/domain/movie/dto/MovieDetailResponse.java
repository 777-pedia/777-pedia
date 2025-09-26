package org.example.pedia_777.domain.movie.dto;

import java.time.LocalDate;
import java.util.Arrays;
import org.example.pedia_777.domain.movie.entity.Movie;

public record MovieDetailResponse(
        Long id,
        String title,
        String director,
        String[] actors,
        String[] genres,
        LocalDate releaseDate,
        Integer runtime,
        String country,
        String overview,
        String posterUrl
) {
    public static MovieDetailResponse from(Movie movie) {
        return new MovieDetailResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                splitSafely(movie.getActors()),
                splitSafely(movie.getGenres()),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getCountry(),
                movie.getOverview(),
                movie.getPosterUrl()
        );
    }

    private static String[] splitSafely(String source) {

        if (source == null || source.isBlank()) {
            return new String[0];
        }

        return Arrays.stream(source.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }
}
