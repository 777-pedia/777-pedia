package org.example.pedia_777.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    // Common Success
    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    //Member Success
    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입이 성공적으로 처리되었습니다."),

    //heart Success

    //movie Success

    //review Success
    REVIEW_SUCCESS(HttpStatus.OK, "리뷰 생성이 성공적으로 처리되었습니다.");
    //like Success

    //searchHistory Success

    private final HttpStatus httpStatus;
    private final String message;
}
