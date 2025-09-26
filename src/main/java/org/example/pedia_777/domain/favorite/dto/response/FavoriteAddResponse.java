package org.example.pedia_777.domain.favorite.dto.response;

public record FavoriteAddResponse(
        Long movieId,
        String title,
        boolean isHeart
) {

    public static FavoriteAddResponse of(Long movieId, String title, boolean isHeart) {
        return new FavoriteAddResponse(movieId, title, isHeart);
    }
}
