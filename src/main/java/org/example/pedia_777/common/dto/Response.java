package org.example.pedia_777.common.dto;

import java.time.LocalDateTime;
import org.example.pedia_777.common.code.Code;
import org.example.pedia_777.common.code.SuccessMessage;
import org.springframework.http.ResponseEntity;

public record Response<T>(

        boolean success,
        String message,
        T data,
        LocalDateTime timestamp) {

    // 정적 팩토리 메소드 사용
    // 성공 응답은 200 OK으로 통일
    // httpStatus Code가 200 OK가 아닌 경우 호출한 Controller의 메소드에 @ResponseStatus 어노테이션을 붙여서 처리
    public static <T> Response<T> of(SuccessMessage msg, T data) {
        return new Response<>(true, msg.getMessage(), data, LocalDateTime.now());
    }

    // 실패 응답 생성 (GlobalExceptionHandler용)
    public static Response<Void> error(Code code) {
        return new Response<>(false, code.getMessage(), null, LocalDateTime.now());
    }

    // 실패 응답 생성 (MethodArgumentNotValidException용)
    public static Response<Void> error(Code code, String customMessage) {
        return new Response<>(false, code.getMessage(customMessage), null, LocalDateTime.now());
    }

    public static ResponseEntity<Response<Void>> errorEntity(Code code) {
        return ResponseEntity.status(code.getHttpStatus()).body(error(code));
    }

    public static ResponseEntity<Response<Void>> errorEntity(Code code, String customMessage) {
        return ResponseEntity.status(code.getHttpStatus()).body(error(code, customMessage));
    }
}
