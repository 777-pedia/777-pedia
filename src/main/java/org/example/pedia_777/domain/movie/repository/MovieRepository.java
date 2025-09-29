package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.movie.entity.Movie;
import org.example.pedia_777.domain.search.dto.response.MovieSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long>, MovieRepositoryCustom {

    @Query(value = """
            SELECT m.id, m.title, m.director, m.actors, m.poster_url,
                   AVG(r.star) AS avgStar,
                   COUNT(DISTINCT r.id) AS reviewCount,
                   COUNT(DISTINCT f.id) AS favoriteCount
            FROM movie m
            LEFT JOIN review r ON r.movies_id = m.id AND r.deleted_at IS NULL
            LEFT JOIN favorite f ON f.movie_id = m.id
            WHERE MATCH(m.title, m.director, m.actors) AGAINST (:keyword IN BOOLEAN MODE)       
            GROUP BY m.id
            """,
            countQuery = """
                    SELECT COUNT(*) 
                    FROM movie m 
                    WHERE MATCH(m.title, m.director, m.actors) AGAINST (:keyword IN BOOLEAN MODE)
                    """,
            nativeQuery = true)
    Page<MovieSearchProjection> searchMoviesNative(@Param("keyword") String keyword, Pageable pageable);
}
