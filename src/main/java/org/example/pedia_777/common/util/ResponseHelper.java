package org.example.pedia_777.common.util;

import org.example.pedia_777.common.code.Code;
import org.example.pedia_777.common.dto.Response;
import org.springframework.http.ResponseEntity;

public class ResponseHelper {
    public static <T> ResponseEntity<Response<T>> success(Code code, T data) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(Response.success(code, data));
    }

    public static ResponseEntity<Response<Void>> success(Code code) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(Response.success(code));
    }

    public static ResponseEntity<Response<Void>> error(Code code) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(Response.error(code));
    }

    public static ResponseEntity<Response<Void>> error(Code code, String customMessage) {
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(Response.error(code, customMessage));
    }
}
