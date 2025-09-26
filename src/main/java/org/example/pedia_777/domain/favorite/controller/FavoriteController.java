package org.example.pedia_777.domain.favorite.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteAddResponse;
import org.example.pedia_777.domain.favorite.dto.response.FavoriteItemResponse;
import org.example.pedia_777.domain.favorite.service.FavoriteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{movieId}/favorites")
    public ResponseEntity<GlobalApiResponse<FavoriteAddResponse>> addFavorite(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long movieId) {

        FavoriteAddResponse response = favoriteService.addFavorite(authMember.id(), movieId);

        return ResponseHelper.success(CommonSuccessCode.CREATED_SUCCESS, response);
    }

    @DeleteMapping("/{movieId}/favorites")
    public ResponseEntity<GlobalApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal AuthMember authMember,
            @PathVariable Long movieId) {

        favoriteService.removeFavorite(authMember.id(), movieId);

        return ResponseHelper.success(CommonSuccessCode.DELETED_SUCCESS);
    }

    @GetMapping("/favorites")
    public ResponseEntity<GlobalApiResponse<PageResponse<FavoriteItemResponse>>> getMyFavorites(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        PageResponse<FavoriteItemResponse> response = favoriteService.getMyFavorites(authMember.id(), pageable);

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, response);
    }
}
