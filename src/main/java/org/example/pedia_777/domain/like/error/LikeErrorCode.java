package org.example.pedia_777.domain.like.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements Code {

    LIKE_REQUEST_CONFLICT(HttpStatus.CONFLICT, "잠시 후 다시 시도해주세요"),
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요를 누른 리뷰입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰에 대한 좋아요 기록을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}
