package org.example.pedia_777.domain.search.dto.response;

import static org.example.pedia_777.common.util.StringHelper.splitSafely;

public record SearchMovieResponse(
        Long id,
        String title,
        String director,
        String[] actors,
        String posterUrl,
        Double avgRating,
        Long reviewCount,
        Long favoriteCount
) {


    public static SearchMovieResponse from(SearchMovieProjection projectionDto) {
        return new SearchMovieResponse(
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
