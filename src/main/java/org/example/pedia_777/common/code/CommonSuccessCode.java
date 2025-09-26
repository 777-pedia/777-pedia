package org.example.pedia_777.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonSuccessCode implements Code {

    REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),
    CREATED_SUCCESS(HttpStatus.CREATED, "요청을 통해 성공적으로 생성되었습니다."),
    DELETED_SUCCESS(HttpStatus.NO_CONTENT, "요청을 통해 성공적으로 삭제되었습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}