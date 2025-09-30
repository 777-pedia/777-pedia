package org.example.pedia_777.domain.movie.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessMessage;
import org.example.pedia_777.common.dto.Response;
import org.example.pedia_777.domain.movie.dto.MovieDetailResponse;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public Response<MovieDetailResponse> getMovieDetails(@PathVariable Long movieId) {

        return Response.of(SuccessMessage.REQUEST_SUCCESS, movieService.getMovieDetails(movieId));
    }
}
