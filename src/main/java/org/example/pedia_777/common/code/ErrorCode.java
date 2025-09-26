package org.example.pedia_777.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. %s"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "입력이 잘못되었습니다. %s"),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않는 JWT 서명입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다."),

    // member
    EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    NOT_FOUND_EMAIL_PASSWORD(HttpStatus.NOT_FOUND, "이메일 혹은 비밀번호를 찾을 수 없습니다."),

    // favorite

    // movie
    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다."),

    // review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),

    //like
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요를 누른 리뷰입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰에 대한 좋아요 기록을 찾을 수 없습니다.");

    // search history

    private final HttpStatus httpStatus;
    private final String message;

    public String getMessage(String... args) {
        return String.format(this.message, (Object[]) args);
    }
}
