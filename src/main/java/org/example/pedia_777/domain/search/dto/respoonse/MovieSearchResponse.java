package org.example.pedia_777.domain.search.dto.respoonse;

import static org.example.pedia_777.common.util.StringHelper.splitSafely;

public record MovieSearchResponse(
        Long id,
        String title,
        String director,
        String[] actors,
        String posterUrl,
        Double avgRating,
        Long reviewCount,
        Long favoriteCount
) {


    public static MovieSearchResponse from(MovieSearchProjection projectionDto) {
        return new MovieSearchResponse(
                projectionDto.id(),
                projectionDto.title(),
                projectionDto.director(),
                splitSafely(projectionDto.actors()),
                projectionDto.posterUrl(),
                Math.round(projectionDto.avgRating() * 100) / 100.0, // 소수점 둘째 자리까지 반올림
                projectionDto.reviewCount(),
                projectionDto.favoriteCount()
        );
    }
}
