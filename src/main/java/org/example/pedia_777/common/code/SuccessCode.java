package org.example.pedia_777.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {


	  // common
	  REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

	  // member
	  SIGNUP_SUCCESS(HttpStatus.OK, "회원가입이 성공적으로 처리되었습니다."),
	  LOGIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다."),

    // favorite

    // movie

    // review
    REVIEW_SUCCESS(HttpStatus.OK, "리뷰 생성이 성공적으로 처리되었습니다.");
  
    // like

    // search history

    private final HttpStatus httpStatus;
    private final String message;
}
