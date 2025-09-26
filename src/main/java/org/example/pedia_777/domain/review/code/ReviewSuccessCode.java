package org.example.pedia_777.domain.review.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements Code {

    REVIEW_LIST_VIEWED(HttpStatus.OK, "리뷰를 찾았습니다.");


    private final HttpStatus httpStatus;
    private final String messageTemplate;
}
