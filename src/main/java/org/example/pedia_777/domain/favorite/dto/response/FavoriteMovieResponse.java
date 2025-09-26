package org.example.pedia_777.domain.favorite.dto.response;

import java.time.LocalDateTime;

import org.example.pedia_777.domain.favorite.entity.Favorite;
import org.example.pedia_777.domain.movie.entity.Movie;

public record FavoriteMovieResponse(
        Long movieId,
        String title,
        String director,
        String posterUrl,
        String genres,
        LocalDateTime favoriteAddedAt
) {

    public static FavoriteMovieResponse from(Favorite favorite) {
        Movie movie = favorite.getMovie();
        return new FavoriteMovieResponse(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getPosterUrl(),
                movie.getGenres(),
                favorite.getCreatedAt()
        );
    }
}
