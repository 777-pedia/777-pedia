package org.example.pedia_777.domain.favorite.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteAddResponse;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteMovieResponse;
import org.example.pedia_777.domain.favorite.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/movies/{movieId}/favorites")
    public ResponseEntity<GlobalApiResponse<FavoriteAddResponse>> addHeart(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long movieId) {

        FavoriteAddResponse response = favoriteService.addHeart(authMember.id(), movieId);

        return ResponseHelper.success(CommonSuccessCode.CREATED_SUCCESS, response);
    }

    @GetMapping("/members/me/favorites")
    public ResponseEntity<GlobalApiResponse<PageResponse<FavoriteMovieResponse>>> getMyFavoriteMovies(
            @AuthenticationPrincipal AuthMember authMember,
            @PageableDefault(size = 10) Pageable pageable
    ) {

        PageResponse<FavoriteMovieResponse> response = favoriteService.getFavoriteMovies(authMember.id(), pageable);

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, response);
    }
}
