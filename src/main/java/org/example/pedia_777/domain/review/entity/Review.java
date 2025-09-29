package org.example.pedia_777.domain.review.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.example.pedia_777.domain.like.entity.Like;
import org.example.pedia_777.domain.member.entity.Member;
import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.review.dto.request.ReviewUpdateRequest;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE review SET deleted_at = current_timestamp WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
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

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @Builder
    public Review(String comment, double star, Long likeCount,
                  LocalDateTime updatedAt, LocalDateTime deletedAt, Movie movie, Member member) {
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

    public void update(ReviewUpdateRequest request) {
        this.comment = request.comment();
        this.star = request.star();
    }
}

