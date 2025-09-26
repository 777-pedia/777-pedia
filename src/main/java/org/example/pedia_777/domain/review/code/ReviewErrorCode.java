package org.example.pedia_777.domain.review.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements Code {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    CLIENT_UPDATE_ERROR(HttpStatus.UNAUTHORIZED, "본인의 리뷰만 수정할 수 있습니다."),
    CLIENT_DELETE_ERROR(HttpStatus.UNAUTHORIZED, "본인의 리뷰만 삭제할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}
