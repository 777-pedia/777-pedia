package org.example.pedia_777.domain.search.dto.response;

public record SearchMovieProjection(
        Long id,
        String title,
        String director,
        String actors,
        String posterUrl,
        Double avgRating,
        Long reviewCount,
        Long favoriteCount
) {
}
