package org.example.pedia_777.domain.movie.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.pedia_777.domain.favorite.entity.QFavorite;
import org.example.pedia_777.domain.movie.entity.QMovie;
import org.example.pedia_777.domain.review.entity.QReview;
import org.example.pedia_777.domain.search.dto.response.MovieSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class MovieSearchRepositoryImpl implements MovieSearchRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MovieSearchProjection> searchMovies(String keyword, Pageable pageable) {

        QMovie movie = QMovie.movie;
        QReview review = QReview.review;
        QFavorite favorite = QFavorite.favorite;

        List<MovieSearchProjection> content = queryFactory.select(
                        Projections.constructor(MovieSearchProjection.class,
                                movie.id,
                                movie.title,
                                movie.director,
                                movie.actors,
                                movie.posterUrl,
                                review.star.avg(),
                                review.id.countDistinct(),
                                favorite.id.countDistinct()
                        ))
                .from(movie)
                .leftJoin(review).on(review.movie.id.eq(movie.id))
                .leftJoin(favorite).on(favorite.movie.id.eq(movie.id))
                .where(movie.title.containsIgnoreCase(keyword)
                        .or(movie.director.containsIgnoreCase(keyword))
                        .or(movie.actors.containsIgnoreCase(keyword)))
                .groupBy(movie.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리 (성능 최적화를 위해 별도 실행)
        JPAQuery<Long> countQuery = queryFactory
                .select(movie.count())
                .from(movie)
                .where(movie.title.containsIgnoreCase(keyword)
                        .or(movie.director.containsIgnoreCase(keyword))
                        .or(movie.actors.containsIgnoreCase(keyword)));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
