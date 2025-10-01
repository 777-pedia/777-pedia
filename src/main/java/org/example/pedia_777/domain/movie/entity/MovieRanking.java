package org.example.pedia_777.domain.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RankingPeriod rankingType;

    @Column(nullable = false)
    private LocalDate rankingDate; // 일간: 해당 날짜, 주간: 해당 주의 시작일(월요일)

    @Column(name = "ranking", nullable = false)
    private int ranking;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private double score;

    @Builder(access = AccessLevel.PRIVATE)
    private MovieRanking(RankingPeriod rankingType, LocalDate rankingDate, int ranking, Long movieId, double score) {
        this.rankingType = rankingType;
        this.rankingDate = rankingDate;
        this.ranking = ranking;
        this.movieId = movieId;
        this.score = score;
    }

    public static MovieRanking of(RankingPeriod rankingType, LocalDate rankingDate, int ranking, Long movieId,
                                  double score) {
        return MovieRanking.builder()
                .rankingType(rankingType)
                .rankingDate(rankingDate)
                .ranking(ranking)
                .movieId(movieId)
                .score(score)
                .build();
    }
}