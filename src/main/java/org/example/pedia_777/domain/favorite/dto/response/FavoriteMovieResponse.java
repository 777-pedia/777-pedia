package org.example.pedia_777.domain.favorite.dto.response;

import org.example.pedia_777.domain.favorite.entity.Favorite;

public record FavoriteMovieResponse(
        Long movieId,
        String title,
        String posterUrl
) {

    public static FavoriteMovieResponse from(Favorite favorite) {

        return new FavoriteMovieResponse(favorite.getMovie().getId(), favorite.getMovie().getTitle(),
                favorite.getMovie().getPosterUrl());
    }
}
