package org.example.pedia_777.domain.favorite.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.common.code.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FavoriteErrorCode implements Code {

    FAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 찜을 추가한 영화입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "찜을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;
}
