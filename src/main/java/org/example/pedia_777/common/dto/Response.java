package org.example.pedia_777.common.dto;

import java.time.LocalDateTime;
import org.example.pedia_777.common.code.Code;

public record Response<T>(

        boolean success,
        String message,
        T data,
        LocalDateTime timestamp) {

    // 명세서에서 성공 메시지가 모두 별도로 존재하므로 success 이라는 이름의 정적 팩토리 메소드 사용
    // 대신 httpStatus Code가 200OK가 아닌 경우 호출한 Controller의 메소드에 @ResponseStatus 어노테이션을 붙여서 처리
    // 성공 응답 생성 (데이터 포함)
    public static <T> Response<T> success(Code code, T data) {
        return new Response<>(true, code.getMessage(), data, LocalDateTime.now());
    }

    // 성공 응답 생성 (데이터 미포함)
    public static Response<Void> success(Code code) {
        return new Response<>(true, code.getMessage(), null, LocalDateTime.now());
    }

    // 실패 응답 생성 (GlobalExceptionHandler용)
    public static Response<Void> error(Code code) {
        return new Response<>(false, code.getMessage(), null, LocalDateTime.now());
    }

    // 실패 응답 생성 (MethodArgumentNotValidException용)
    public static Response<Void> error(Code code, String customMessage) {
        return new Response<>(false, code.getMessage(customMessage), null, LocalDateTime.now());
    }
}
