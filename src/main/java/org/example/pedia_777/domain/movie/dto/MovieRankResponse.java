package org.example.pedia_777.domain.movie.dto;

import org.example.pedia_777.domain.movie.entity.Movie;

public record MovieRankResponse(
        int rank,
        Long movieId,
        String title,
        String posterUrl
) {
    public static MovieRankResponse of(int rank, Movie movie) {
        return new MovieRankResponse(rank, movie.getId(), movie.getTitle(), movie.getPosterUrl());
    }
}
