package org.example.pedia_777.domain.search.dto.respoonse;

public record MovieSearchProjection(
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
