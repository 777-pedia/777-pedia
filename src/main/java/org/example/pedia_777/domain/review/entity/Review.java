package org.example.pedia_777.domain.review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private double star;
    private Long likeCount;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moviesId", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members", nullable = false)
    private Member member;

    @Builder
    public Review(String comment, double star, Long likeCount, LocalDateTime updatedAt, LocalDateTime deletedAt,
                  Movie movie, Member member) {
        this.comment = comment;
        this.star = star;
        this.likeCount = likeCount;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.movie = movie;
        this.member = member;
    }

    //정적 팩토리 메소드
    public static Review create(String comment, double star, Long likeCount, Movie movie, Member member) {
        return Review.builder()
                .comment(comment)
                .star(star)
                .likeCount(likeCount)
                .movie(movie)
                .member(member)
                .build();
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}

