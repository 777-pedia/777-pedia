package org.example.pedia_777.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

	// Common Success
	REQUEST_SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다.");

	//Member Success

	//heart Success

	//movie Success

	//review Success

	//like Success

	//searchHistory Success

	private final HttpStatus httpStatus;
	private final String message;
}
