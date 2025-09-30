package org.example.pedia_777.domain.movie.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.pedia_777.common.entity.BaseTimeEntity;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(indexes = {
        @Index(name = "idx_movie_release_date", columnList = "releaseDate")
})
public class Movie extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String director;
    private String actors;
    private String genres;
    private LocalDate releaseDate;
    private Integer runtime;
    private String country;
    private String overview;
    private String posterUrl;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Builder
    public Movie(String director, String title, String actors, String genres, LocalDate releaseDate, Integer runtime,
                 String country, String overview, String posterUrl) {
        this.director = director;
        this.title = title;
        this.actors = actors;
        this.genres = genres;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.country = country;
        this.overview = overview;
        this.posterUrl = posterUrl;
    }

    public static Movie of(String director, String title, String actors, String genres, LocalDate releaseDate,
                           Integer runtime,
                           String country, String overview, String posterUrl) {
        return Movie.builder()
                .director(director)
                .title(title)
                .actors(actors)
                .genres(genres)
                .releaseDate(releaseDate)
                .runtime(runtime)
                .country(country)
                .overview(overview)
                .posterUrl(posterUrl)
                .build();
    }
}
