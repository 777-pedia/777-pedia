package org.example.pedia_777.domain.movie.repository;

import org.example.pedia_777.domain.movie.entity.MovieRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRankingRepository extends JpaRepository<MovieRanking, Long> {
}
