package org.example.pedia_777.domain.favorite.dto.response;

import org.example.pedia_777.domain.favorite.entity.Favorite;

public record FavoriteItemResponse(
        Long movieId,
        String title,
        String posterUrl
) {

    public static FavoriteItemResponse from(Favorite favorite) {

        return new FavoriteItemResponse(favorite.getMovie().getId(), favorite.getMovie().getTitle(),
                favorite.getMovie().getPosterUrl());
    }
}
