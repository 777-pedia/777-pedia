package org.example.pedia_777.domain.member.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements Code {

    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    NOT_FOUND_EMAIL_PASSWORD(HttpStatus.NOT_FOUND, "이메일 혹은 비밀번호를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}