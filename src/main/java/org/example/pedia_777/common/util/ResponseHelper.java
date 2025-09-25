package org.example.pedia_777.common.util;

import org.example.pedia_777.common.code.ErrorCode;
import org.example.pedia_777.common.code.SuccessCode;
import org.example.pedia_777.common.dto.GlobalApiResponse;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static <T> ResponseEntity<GlobalApiResponse<T>> success(SuccessCode code, T data) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(GlobalApiResponse.success(code, data));
    }

    public static ResponseEntity<GlobalApiResponse<Void>> success(SuccessCode code) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(GlobalApiResponse.success(code));
    }

    public static ResponseEntity<GlobalApiResponse<Void>> error(ErrorCode code) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(GlobalApiResponse.error(code));
    }

    public static ResponseEntity<GlobalApiResponse<Void>> error(ErrorCode code, String customMessage) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(GlobalApiResponse.error(code, customMessage));
    }
}
