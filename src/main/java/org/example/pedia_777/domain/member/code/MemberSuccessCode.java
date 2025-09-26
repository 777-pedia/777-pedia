package org.example.pedia_777.domain.member.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberSuccessCode implements Code {

    SIGNUP_SUCCESS(HttpStatus.OK, "회원가입이 성공적으로 처리되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인이 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}