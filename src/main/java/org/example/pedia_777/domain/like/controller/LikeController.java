package org.example.pedia_777.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.SuccessCode;
import org.example.pedia_777.common.dto.AuthMember;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.domain.like.dto.response.LikeResponse;
import org.example.pedia_777.domain.like.service.LikeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/reviews/{reviewId}/likes")
    public GlobalApiResponse<LikeResponse> addLike(
            @PathVariable Long reviewId, @AuthenticationPrincipal AuthMember authMember) {

        LikeResponse response = likeService.addLike(authMember.id(), reviewId);

        return GlobalApiResponse.success(SuccessCode.LIKE_SUCCESS, response);
    }

}
