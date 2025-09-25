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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.example.pedia_777.domain.member.entity.Members;
import org.example.pedia_777.domain.movie.entity.Movies;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@NoArgsConstructor
@Getter
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
    private Movies movies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members", nullable = false)
    private Members members;

    @Builder
    public Review(String comment, double star, Long likeCount, LocalDateTime updatedAt, LocalDateTime deletedAt,
                  Movies movies, Members members) {
        this.comment = comment;
        this.star = star;
        this.likeCount = likeCount;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.movies = movies;
        this.members = members;
    }

    //정적 팩토리 메소드
    public static Review create(String comment, double star, Long likeCount, Movies movies, Members members) {
        return Review.builder()
                .comment(comment)
                .star(star)
                .likeCount(likeCount)
                .movies(movies)
                .members(members)
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

