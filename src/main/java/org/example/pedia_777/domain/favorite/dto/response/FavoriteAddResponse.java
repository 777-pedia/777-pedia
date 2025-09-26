package org.example.pedia_777.domain.favorite.dto.response;

public record FavoriteAddResponse(
        Long movieId,
        String title,
        boolean isFavorite
) {

    public static FavoriteAddResponse of(Long movieId, String title, boolean isFavorite) {
        return new FavoriteAddResponse(movieId, title, isFavorite);
    }
}
