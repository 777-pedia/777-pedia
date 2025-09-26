package org.example.pedia_777.common.exception;

import lombok.Getter;
import org.example.pedia_777.common.code.Code;

@Getter
public class BusinessException extends RuntimeException {

    private final Code errorCode;

    public BusinessException(Code errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
