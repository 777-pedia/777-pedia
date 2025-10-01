package org.example.pedia_777.domain.search.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate rankingDate;

    @Column(nullable = false)
    private int ranking;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private double score;

    @Builder(access = AccessLevel.PRIVATE)
    private PopularKeyword(LocalDate rankingDate, int ranking, String keyword, double score) {
        this.rankingDate = rankingDate;
        this.ranking = ranking;
        this.keyword = keyword;
        this.score = score;
    }

    public static PopularKeyword of(LocalDate rankingDate, int ranking, String keyword, double score) {
        return PopularKeyword.builder()
                .rankingDate(rankingDate)
                .ranking(ranking)
                .keyword(keyword)
                .score(score)
                .build();
    }
}
