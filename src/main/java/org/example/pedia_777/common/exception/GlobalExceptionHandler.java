package org.example.pedia_777.common.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.example.pedia_777.common.code.CommonErrorCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.example.pedia_777.common.util.ResponseHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleCustomBusinessException(BusinessException e) {
        return ResponseHelper.error(e.getErrorCode());
    }

    // MethodArgumentNotValidException 처리 (@Valid 유효성 검사 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String customMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseHelper.error(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID, customMessage);
    }

    // 정의되지 않은 내부 Exception 일괄 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleException(Exception e) {
        log.error("[GlobalExceptionHandler] 정의되지 않은 Exception: {}", e.getMessage(), e);
        return ResponseHelper.error(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }

    // @RequestParam, @PathVariable 유효성 검사 예외 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse(CommonErrorCode.VALIDATION_FAILED.getMessage());

        return ResponseHelper.error(CommonErrorCode.VALIDATION_FAILED, errorMessage);
    }

    // @RequestParam, @PathVariable 타입 변환 실패 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<GlobalApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String message = String.format("'%s' 파라미터의 형식이 올바르지 않습니다. (필요한 타입: %s)",
                e.getName(), e.getRequiredType().getSimpleName());

        return ResponseHelper.error(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID, message);
    }

}
