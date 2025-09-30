package org.example.pedia_777.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {

    REQUEST_SUCCESS("요청이 성공적으로 처리되었습니다."),
    CREATED_SUCCESS("요청을 통해 성공적으로 생성되었습니다."),
    DELETED_SUCCESS("요청을 통해 성공적으로 삭제되었습니다."),
    SIGNUP_SUCCESS("회원가입이 성공적으로 처리되었습니다."),
    LOGIN_SUCCESS("로그인이 완료되었습니다.");

    private final String message;
}
