package org.example.pedia_777.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessMessage;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.PageResponse;
import org.example.pedia_777.common.dto.Response;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.dto.response.LikedReviewResponse;
import org.example.pedia_777.domain.like.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews/")
public class LikeController {

    private final LikeService likeService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("{reviewId}/likes")
    public Response<LikeResponse> addLike(
            @AuthenticationPrincipal AuthMember authMember, @PathVariable Long reviewId) {

        LikeResponse response = likeService.addLike(authMember.id(), reviewId);

        return Response.of(SuccessMessage.CREATED_SUCCESS, response);
    }

    @DeleteMapping("{reviewId}/likes")
    public Response<LikeResponse> cancelLike(
            @AuthenticationPrincipal AuthMember authMember, @PathVariable Long reviewId) {

        LikeResponse response = likeService.cancelLike(authMember.id(), reviewId);

        return Response.of(SuccessMessage.DELETED_SUCCESS, response);
    }

    @GetMapping("/likes")
    public Response<PageResponse<LikedReviewResponse>> getLikedReviews(
            @AuthenticationPrincipal AuthMember authMember,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<LikedReviewResponse> response = likeService.getLikedReviews(authMember.id(), page, size);

        return Response.of(SuccessMessage.REQUEST_SUCCESS, response);
    }
}
