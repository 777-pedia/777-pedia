package org.example.pedia_777.domain.search.dto.response;

public record PopularKeywordResponse(
        int rank,
        String keyword) {

    public static PopularKeywordResponse of(int rank, String keyword) {
        return new PopularKeywordResponse(rank, keyword);
    }
}
