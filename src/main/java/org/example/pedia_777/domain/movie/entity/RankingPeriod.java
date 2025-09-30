package org.example.pedia_777.domain.movie.entity;

import org.example.pedia_777.common.exception.BusinessException;
import org.example.pedia_777.domain.movie.error.MovieErrorCode;

public enum RankingPeriod {
    DAILY,
    WEEKLY;

    // RequestParam 소문자 입력 처리
    public static RankingPeriod fromString(String value) {

        for (RankingPeriod p : values()) {
            if (p.name().equalsIgnoreCase(value)) {
                return p;
            }
        }

        throw new BusinessException(MovieErrorCode.INVALID_RANKING_PERIOD);
    }
}