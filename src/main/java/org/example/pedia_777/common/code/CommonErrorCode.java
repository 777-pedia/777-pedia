package org.example.pedia_777.common.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements Code {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. %s"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "입력이 잘못되었습니다. %s"),

    // auth
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}