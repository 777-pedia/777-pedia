package org.example.pedia_777.domain.search.dto.respoonse;

import static org.example.pedia_777.common.util.StringHelper.splitSafely;

import org.example.pedia_777.domain.movie.entity.Movie;

public record MovieSearchResponse(
        Long id,
        String title,
        String director,
        String[] actors,
        String posterUrl,
        double avgRating,
        long reviewCount,
        long favoriteCount
) {


    public static MovieSearchResponse of(Movie movie, double avgScore,
                                         long reviewCount, long likeCount) {
        return new MovieSearchResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                splitSafely(movie.getActors()),
                movie.getPosterUrl(),
                avgScore,
                reviewCount,
                likeCount
        );
    }
}
