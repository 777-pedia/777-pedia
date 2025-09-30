package org.example.pedia_777.domain.movie.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessMessage;
import org.example.pedia_777.common.dto.Response;
import org.example.pedia_777.domain.movie.dto.MovieDetailResponse;
import org.example.pedia_777.domain.movie.dto.MovieRankResponse;
import org.example.pedia_777.domain.movie.entity.RankingPeriod;
import org.example.pedia_777.domain.movie.service.MovieRankingService;
import org.example.pedia_777.domain.movie.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;
    private final MovieRankingService movieRankingService;

    @GetMapping("/{movieId}")
    public Response<MovieDetailResponse> getMovieDetails(@PathVariable Long movieId) {

        return Response.of(SuccessMessage.REQUEST_SUCCESS, movieService.getMovieDetails(movieId));
    }

    @GetMapping("/ranking")
    public ResponseEntity<Response<List<MovieRankResponse>>> getTopTenMovies(
            @RequestParam(defaultValue = "daily") String period) {

        List<MovieRankResponse> movieRankResponseList =
                RankingPeriod.fromString(period) == RankingPeriod.DAILY
                        ? movieRankingService.getDailyTop10()
                        : movieRankingService.getWeeklyTop10();

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, movieRankResponseList);
    }
}
