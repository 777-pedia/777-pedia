package org.example.pedia_777.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// Common error
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. %s"),
	METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "입력이 잘못되었습니다. %s");

	//Member error

	//heart error

	//movie error

	//review error

	//like error

	//searchHistory error

	private final HttpStatus httpStatus;
	private final String message;

	public String getMessage(String... args) {
		return String.format(this.message, (Object[]) args);
	}
}
