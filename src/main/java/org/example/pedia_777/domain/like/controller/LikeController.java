package org.example.pedia_777.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.CommonSuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews/")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("{reviewId}/likes")
    public ResponseEntity<GlobalApiResponse<LikeResponse>> addLike(
            @AuthenticationPrincipal AuthMember authMember, @PathVariable Long reviewId) {

        LikeResponse response = likeService.addLike(authMember.id(), reviewId);

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, response);
    }

    @DeleteMapping("{reviewId}/likes")
    public ResponseEntity<GlobalApiResponse<LikeResponse>> cancelLike(
            @AuthenticationPrincipal AuthMember authMember, @PathVariable Long reviewId) {

        LikeResponse response = likeService.cancelLike(authMember.id(), reviewId);

        return ResponseHelper.success(CommonSuccessCode.REQUEST_SUCCESS, response);
    }


}
