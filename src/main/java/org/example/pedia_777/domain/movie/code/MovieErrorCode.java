package org.example.pedia_777.domain.movie.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MovieErrorCode implements Code {

    MOVIE_NOT_FOUND(HttpStatus.NOT_FOUND, "영화를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}
